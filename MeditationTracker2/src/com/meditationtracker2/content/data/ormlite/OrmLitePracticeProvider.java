package com.meditationtracker2.content.data.ormlite;

import java.sql.SQLException;
import java.util.Date;
import java.util.List;

import android.content.Context;

import com.j256.ormlite.android.apptools.OpenHelperManager;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.GenericRawResults;
import com.j256.ormlite.field.DataType;
import com.meditationtracker2.content.data.IPracticeProvider;
import com.meditationtracker2.content.data.Practice;
import com.meditationtracker2.content.data.PracticeHistory;

public class OrmLitePracticeProvider implements IPracticeProvider {
	private final Context context;
	
	public OrmLitePracticeProvider(final Context where) {
		this.context = where;
	}

	@Override
	public List<Practice> getPractices() {
		
		try {
			List<Practice> practices = new DoWithDao<Practice, Integer, List<Practice>>() {
				@Override protected List<Practice> theCall(Dao<Practice, Integer> dao) throws SQLException {
					return dao.queryForAll();
				}
			}.doIt(Practice.class);

			for (Practice p : practices) {
				updatePracticeHistoryFields(p);
			}
			
			return practices;
		} catch (SQLException e) {
			//TODO
			throw new RuntimeException(e);
		}
		
//		return null;
	}

	@Override
	public Practice getPractice(final int id) {

		try {
			final Practice practice = new DoWithDao<Practice, Integer, Practice>() {
				@Override protected Practice theCall(Dao<Practice, Integer> dao) throws SQLException {
					return dao.queryForId(id);
				}
			}.doIt(Practice.class);
			
			updatePracticeHistoryFields(practice);
			
			return practice;
		} catch (SQLException e) {
			//TODO
			throw new RuntimeException(e);
		}
		
//		return null;
	}

	protected void updatePracticeHistoryFields(final Practice practice) throws SQLException {
		practice.currentCount = getCurrentCount(practice);
		practice.completedToday = getTodayCount(practice);
		practice.lastPracticeDate = getLastPracticeDate(practice);
	}

	protected Long getCurrentCount(final Practice practice) throws SQLException {
		return new DoWithDao<PracticeHistory, Integer, Long>() {
			@Override protected Long theCall(Dao<PracticeHistory, Integer> dao) throws SQLException {
				return dao.queryRawValue("SELECT SUM(DONE_COUNT) FROM PracticeHistory WHERE PRACTICE_ID = ?", String.valueOf(practice.id));
			}
		}.doIt(PracticeHistory.class);
	}

	protected Long getTodayCount(final Practice practice) throws SQLException {
		return new DoWithDao<PracticeHistory, Integer, Long>() {
			@Override protected Long theCall(Dao<PracticeHistory, Integer> dao) throws SQLException {
				return dao.queryRawValue("SELECT SUM(DONE_COUNT) FROM PracticeHistory WHERE PRACTICE_ID = ? AND PRACTICE_DATE = CURRENT_DATE", String.valueOf(practice.id));
			}
		}.doIt(PracticeHistory.class);
	}

	protected Date getLastPracticeDate(final Practice practice) throws SQLException {
		return new DoWithDao<PracticeHistory, Integer, Date>() {
			@Override protected Date theCall(Dao<PracticeHistory, Integer> dao) throws SQLException {
				Date result = new Date(0);
				GenericRawResults<Object[]> results = dao.queryRaw("SELECT MAX(PRACTICE_DATE) FROM PracticeHistory WHERE PRACTICE_ID = ? ", new DataType[] {DataType.DATE_LONG}, String.valueOf(practice.id));

				if (results != null) {
					Object[] firstRow = results.getFirstResult();
					if (firstRow.length > 0) {
						result = (Date) firstRow[0];
					}
				}
				
				return result;
			}
		}.doIt(PracticeHistory.class);
	}
	
	@Override
	public void savePractice(final Practice practice) {
		try {
			new DoWithDao<Practice, Integer, Practice>() {
				@Override protected Practice theCall(Dao<Practice, Integer> dao) throws SQLException {
					dao.createOrUpdate(practice);
					return null;
				}
			}.doIt(Practice.class);
		} catch (SQLException e) {
			//TODO
			throw new RuntimeException(e);
		}
	}

	@Override
	public void addSession(final Practice practice, final int count) {
		try {
			new DoWithDao<PracticeHistory, Integer, PracticeHistory>() {
				@Override protected PracticeHistory theCall(Dao<PracticeHistory, Integer> dao) throws SQLException {
					dao.createOrUpdate(new PracticeHistory(practice, count));
					return null;
				}
			}.doIt(PracticeHistory.class);
		} catch (SQLException e) {
			//TODO
			throw new RuntimeException(e);
		}
	}

	private abstract class DoWithDao<DaoClass, ID, TResult> {
		public TResult doIt(Class<?> clazz) throws SQLException {
			OrmLiteOpenHelper helper = null;
			
			try {
				helper = OpenHelperManager.getHelper(context, OrmLiteOpenHelper.class);
				
				Dao<DaoClass, ID> dao = helper.getDao(clazz);
				return theCall(dao);
			} 
			finally {
				if (helper != null) {
					OpenHelperManager.releaseHelper();
				}
			}
		}

		protected abstract TResult theCall(Dao<DaoClass, ID> dao) throws SQLException;
	}
}