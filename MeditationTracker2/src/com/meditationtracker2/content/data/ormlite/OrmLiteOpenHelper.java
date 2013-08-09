package com.meditationtracker2.content.data.ormlite;

import java.sql.SQLException;
import java.util.List;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import com.j256.ormlite.android.apptools.OrmLiteSqliteOpenHelper;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;
import com.meditationtracker2.R;
import com.meditationtracker2.content.data.Practice;
import com.meditationtracker2.content.data.PracticeHistory;

public class OrmLiteOpenHelper extends OrmLiteSqliteOpenHelper {
	private static final int DATABASE_VERSION = 10;
	private static final String DATABASE_NAME = "MediTracker";
	private Context context;
	
	public OrmLiteOpenHelper(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
		this.context = context;
	}

	@Override
	public void onCreate(SQLiteDatabase database, ConnectionSource connectionSource) {
		try {
			TableUtils.createTable(connectionSource, Practice.class);
			TableUtils.createTable(connectionSource, PracticeHistory.class);
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
		
		createNgondro();
	}

	@Override
	public void onUpgrade(SQLiteDatabase database, ConnectionSource connectionSource, int oldVersion, int newVersion) {
		ensureNgondroImages();
	}


	private void createNgondro() {
		try {
			Dao<Practice, Integer> dao = getDao(Practice.class);
			
/*
			db.insertPractice(true, 0, getResources().getString(R.string.refuge), R.drawable.refuge,
					R.drawable.icon_refuge, 111111);
			db.insertPractice(true, 1, getResources().getString(R.string.diamondMind),
					R.drawable.diamond_mind_big, R.drawable.icon_diamond_mind, 111111);
			db.insertPractice(true, 2, getResources().getString(R.string.mandalaOffering),
					R.drawable.mandala_offering_big, R.drawable.icon_mandala_offering, 111111);
			db.insertPractice(true, 3, getResources().getString(R.string.guruYoga), R.drawable.guru_yoga_big,
					R.drawable.icon_guru_yoga, 111111);


*/			
			dao.create(new Practice(true, getResourceString(R.string.refuge), 
					getResourceString(R.string.url_refuge), 111111).setId(1));
			dao.create(new Practice(true, getResourceString(R.string.diamondMind), 
					getResourceString(R.string.url_diamondMind), 111111).setId(2));
			dao.create(new Practice(true, getResourceString(R.string.mandalaOffering), 
					getResourceString(R.string.url_mandalaOffering), 111111).setId(3));
			dao.create(new Practice(true, getResourceString(R.string.guruYoga), 
					getResourceString(R.string.url_guruYoga), 111111).setId(4));
			
		} catch (SQLException e) {
			// TODO Auto-generated catch block
		}
		
	}

	protected String getResourceString(int id) {
		return context.getResources().getString(id);
	}

	private void ensureNgondroImages() {
/*
			updateNgondroIcons(db, 1, R.drawable.refuge, R.drawable.icon_refuge);
			updateNgondroIcons(db, 2, R.drawable.diamond_mind_big, R.drawable.icon_diamond_mind);
			updateNgondroIcons(db, 3, R.drawable.mandala_offering_big, R.drawable.icon_mandala_offering);
			updateNgondroIcons(db, 4, R.drawable.guru_yoga_big, R.drawable.icon_guru_yoga);
		
*/
		int int_len = String.valueOf(Integer.MAX_VALUE).length()+5; // should cover all diffs between content-url and resIds 
		
		try {
			Dao<Practice, Integer> dao = getDao(Practice.class);
			
			upgradePracticeImage(dao, getResourceString(R.string.url_refuge), 1);
			upgradePracticeImage(dao, getResourceString(R.string.url_diamondMind), 2);
			upgradePracticeImage(dao, getResourceString(R.string.url_mandalaOffering), 3);
			upgradePracticeImage(dao, getResourceString(R.string.url_guruYoga), 4);
			
			List<Practice> aboveNgondro = dao.queryBuilder().where().gt("_id", 4).query();
			
			for (Practice p : aboveNgondro) {
				if (p.imageUrl.length() < int_len) {
					upgradePracticeImage(dao, getResourceString(R.string.url_sixteenthKarmapa), p);
				}
			}
			
		} catch (SQLException e) {
			// TODO Auto-generated catch block
		}
	
	}

	protected void upgradePracticeImage(Dao<Practice, Integer> dao, String resourceString, int practiceId) throws SQLException {
		Practice practice = dao.queryForId(practiceId);
		upgradePracticeImage(dao, resourceString, practice);
	}

	protected void upgradePracticeImage(Dao<Practice, Integer> dao, String resourceString, Practice practice)
			throws SQLException {
		practice.imageUrl = resourceString;
		dao.update(practice);
	}
}
