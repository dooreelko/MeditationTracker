package com.meditationtracker2.content.data.ormlite;

import java.sql.SQLException;
import java.util.List;

import android.content.Context;

import com.j256.ormlite.android.apptools.OpenHelperManager;
import com.j256.ormlite.dao.Dao;
import com.meditationtracker2.content.data.IPracticeProvider;
import com.meditationtracker2.content.data.Practice;
import com.meditationtracker2.content.data.PracticeHistory;

public class OrmLitePracticeProvider implements IPracticeProvider {
	private final Context context;
	
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

	public OrmLitePracticeProvider(Context where) {
		this.context = where;
	}

	@Override
	public List<Practice> getPractices() {
		
		try {
			return new DoWithDao<Practice, Integer, List<Practice>>() {
				@Override protected List<Practice> theCall(Dao<Practice, Integer> dao) throws SQLException {
					return dao.queryForAll();
				}
			}.doIt(Practice.class);
		} catch (SQLException e) {
			//TODO
		}
		
		return null;
	}

	@Override
	public Practice getPractice(final int id) {

		try {
			return new DoWithDao<Practice, Integer, Practice>() {
				@Override protected Practice theCall(Dao<Practice, Integer> dao) throws SQLException {
					return dao.queryForId(id);
				}
			}.doIt(Practice.class);
		} catch (SQLException e) {
			//TODO
		}
		
		return null;
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
		}
	}

	@Override
	public void addSession(final Practice practice, int count) {
		try {
			new DoWithDao<Practice, Integer, Practice>() {

				@Override protected Practice theCall(Dao<Practice, Integer> dao) throws SQLException {
					practice.history.add(new PracticeHistory());
					dao.update(practice);
					return null;
				}
			}.doIt(Practice.class);
		} catch (SQLException e) {
			//TODO
		}
	}

}