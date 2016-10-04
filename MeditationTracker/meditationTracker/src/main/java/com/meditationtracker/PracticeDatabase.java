package com.meditationtracker;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.nio.channels.WritableByteChannel;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.Map.Entry;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteStatement;
import android.provider.BaseColumns;
import android.util.Log;

import com.meditationtracker.util.Util;

public class PracticeDatabase {
	// private static final String MTRK_LOG_KEY = "MTRK";
	public static final String DBNAME = "MediTracker";
	private static final int DBVERSION = 1;

	private static final String PRACTICE_TABLE_NAME = "Practices";
	private static final String PRACTICE_HISTORY_TABLE_NAME = "PracticeHistory";
	private static final String DONE_TODAY_VIEW_NAME = "DONE_TODAY";

	public static final String KEY_ISNGONDRO = "ISNGONDRO";
	public static final String KEY_ORDER = "SORT_ORDER";
	public static final String KEY_TITLE = "TITLE";
	public static final String KEY_TOTALCOUNT = "TOTALCOUNT";
	public static final String KEY_SCHEDULEDCOMPLETION = "SCHEDULEDCOMPLETION";
	public static final String KEY_SCHEDULEDCOUNT = "SCHEDULEDCOUNT";
	public static final String KEY_ICONURL = "ICONURL";
	public static final String KEY_THUMBURL = "THUMBURL";
	public static final String KEY_MALASIZE = "MALASIZE";

	public static final String KEY_DONE = "DONE";
	public static final String KEY_DONE_PERC = "DONE_PERC";
	public static final String KEY_DONE_TODAY = "DONE_TODAY";
	public static final String KEY_LAST_PRACTICE = "LAST_PRACTICE";

	public static final String KEY_PRACTICE_ID = "PRACTICE_ID";
	public static final String KEY_DATE = "PRACTICE_DATE";
	public static final String KEY_COUNT = "DONE_COUNT";

	private static final String PRACTICE_TABLE_CREATE = "CREATE TABLE " + PRACTICE_TABLE_NAME + " ("
			+ BaseColumns._ID + " INTEGER PRIMARY KEY, " + KEY_ISNGONDRO + " BOOLEAN NOT NULL DEFAULT 0, "
			+ KEY_ORDER + " INTEGER NOT NULL, " + KEY_TITLE + " TEXT NOT NULL, " + KEY_ICONURL
			+ " TEXT NOT NULL, " + KEY_THUMBURL + " TEXT NOT NULL, " + KEY_TOTALCOUNT
			+ " INTEGER NOT NULL DEFAULT 111111, " + KEY_SCHEDULEDCOUNT + " INTEGER NOT NULL DEFAULT 0, "
			+ KEY_MALASIZE + " INTEGER NOT NULL DEFAULT 0) ";
	// KEY_SCHEDULEDCOMPLETION + " TEXT NOT NULL DEFAULT 0)";

	private static final String PRACTICE_HISTORY_TABLE_CREATE = "CREATE TABLE " + PRACTICE_HISTORY_TABLE_NAME
			+ " (" + BaseColumns._ID + " INTEGER PRIMARY KEY, " + KEY_PRACTICE_ID + " INTEGER NOT NULL, "
			+ KEY_DATE + " TEXT default CURRENT_DATE, " + KEY_COUNT + " INTEGER NOT NULL) ";

	private static final String DONE_TODAY_VIEW_CREATE = String.format(
			"CREATE VIEW '%s' AS SELECT %s, SUM(%s) AS %s " + "FROM %s WHERE %s = CURRENT_DATE GROUP BY %s",
			DONE_TODAY_VIEW_NAME, KEY_PRACTICE_ID, KEY_COUNT, KEY_DONE_TODAY, PRACTICE_HISTORY_TABLE_NAME,
			KEY_DATE, KEY_PRACTICE_ID);

	private static final String INSERT_PRACTICE_QUERY = "INSERT INTO " + PRACTICE_TABLE_NAME + " ("
			+ KEY_ISNGONDRO + ", " + KEY_ORDER + ", " + KEY_TITLE + ", " + KEY_ICONURL + ", " + KEY_THUMBURL
			+ ", " + KEY_TOTALCOUNT + ") " + "VALUES (?, ?, ?, ?, ?, ?)";

	private static final String INSERT_SESSION_QUERY = "INSERT INTO " + PRACTICE_HISTORY_TABLE_NAME + " ("
			+ KEY_PRACTICE_ID + ", " + KEY_DATE + ", " + KEY_COUNT + ") " + " VALUES (?, ?, ?)";

	private static final String INSERT_TODAY_SESSION_QUERY = "INSERT INTO " + PRACTICE_HISTORY_TABLE_NAME
			+ " (" + KEY_PRACTICE_ID + ", " + KEY_COUNT + ") " + " VALUES (?, ?)";

	private static final String NGONDRO_CHEK_QUERY = "SELECT COUNT(" + BaseColumns._ID + ") FROM "
			+ PRACTICE_TABLE_NAME + " WHERE " + KEY_ISNGONDRO + " = 1";

	private static final String PRACTICES_STATUS_PREFIX = String
			.format("SELECT p.%s AS %s, %s, %s, %s, %s, %s, %s, "
					+ "IFNULL(SUM(%s), 0) as %s, IFNULL(100.0*SUM(%s)/%s, 0) as %s, IFNULL(MAX(%s), 0) AS %s, "
					+ "IFNULL (dt.%s, 0) AS %s " + "FROM %s p LEFT JOIN %s ph ON p.%s = ph.%s "
					+ "LEFT JOIN %s dt ON dt.%s = p.%s ",

			BaseColumns._ID, BaseColumns._ID, KEY_ICONURL, KEY_THUMBURL, KEY_TITLE, KEY_TOTALCOUNT,
					KEY_SCHEDULEDCOUNT, KEY_MALASIZE, KEY_COUNT, KEY_DONE, KEY_COUNT, KEY_TOTALCOUNT,
					KEY_DONE_PERC, KEY_DATE, KEY_LAST_PRACTICE, KEY_DONE_TODAY, KEY_DONE_TODAY,
					PRACTICE_TABLE_NAME, PRACTICE_HISTORY_TABLE_NAME, BaseColumns._ID, KEY_PRACTICE_ID,
					DONE_TODAY_VIEW_NAME, KEY_PRACTICE_ID, BaseColumns._ID);

	private static final String PRACTICES_STATUS_SUFFIX = String.format("GROUP BY p.%s ORDER BY %s",
			BaseColumns._ID, KEY_ORDER);

	private static final String PRACTICES_STATUS = String.format(PRACTICES_STATUS_PREFIX + "WHERE %s = ? "
			+ PRACTICES_STATUS_SUFFIX, KEY_ISNGONDRO);

	private static final String SINGLE_PRACTICE_STATUS = String.format(PRACTICES_STATUS_PREFIX
			+ "WHERE p.%s = ? " + PRACTICES_STATUS_SUFFIX, BaseColumns._ID);

	/*
	 * private static final String TODAY_DONE = String.format(
	 * "SELECT %s, SUM(%s) as %s " +
	 * "FROM %s WHERE %s=CURRENT_DATE GROUP BY %s", KEY_PRACTICE_ID, KEY_DONE,
	 * KEY_DONE_TODAY, PRACTICE_HISTORY_TABLE_NAME, KEY_DATE, KEY_PRACTICE_ID);
	 */

	private static final String PRACTICE_DONE_COUNT = "SELECT SUM(" + KEY_COUNT + ") FROM "
			+ PRACTICE_HISTORY_TABLE_NAME + " WHERE " + KEY_PRACTICE_ID + " = ?";
	private static final String NEXT_SORT_ORDER = "SELECT MAX(" + KEY_ORDER + ")+1 as " + KEY_ORDER
			+ " FROM " + PRACTICE_TABLE_NAME;

	private static SQLiteDatabase db;

	private static HashMap<String, Set<String>> tableColumns;

	public PracticeDatabase(Context ctx) {
        open(ctx);
	}

    private void open(Context ctx) {
        if ((db == null || !db.isOpen())) {
            MySQLiteOpenHelper helper = new MySQLiteOpenHelper(ctx);
            db = helper.getWritableDatabase();
        }

        if (isOpen()) {
            if (tableColumns == null) {
                tableColumns = new HashMap<String, Set<String>>();

                getTableColumnsNames(PRACTICE_TABLE_NAME);
                getTableColumnsNames(PRACTICE_HISTORY_TABLE_NAME);
            }
        }
    }

    public boolean isOpen() {
		return db != null && db.isOpen();
	}

	public void release() {
		if (db != null)
			db.close();
	}

    public void exportDatabase(File outPath) throws IOException {
        String dbPath = db.getPath();

        FileChannel dbChannel = new FileInputStream(dbPath).getChannel();
        FileChannel dbDest = new FileOutputStream(outPath).getChannel();
        dbChannel.transferTo(0, dbChannel.size(), dbDest);

        dbChannel.close();
        dbDest.close();
    }

    public void importDatabase(File inFile, Context ctx) throws Exception {
        String dbPath = db.getPath();
        release();

        FileChannel dbSource = new FileInputStream(inFile).getChannel();
        FileChannel dbDest = new FileOutputStream(dbPath).getChannel();
        if (dbSource.transferTo(0, dbSource.size(), dbDest) == 0) {
            throw new Exception("No bytes were copied");
        }

        dbSource.close();
        dbDest.close();

        open(ctx);
    }


    private void getTableColumnsNames(String tableName) {
		Cursor c = db.rawQuery("SELECT * FROM " + tableName, null);
		HashSet<String> columns = new HashSet<String>();
		for (String col : c.getColumnNames()) {
			columns.add(col);
		}
		tableColumns.put(tableName, columns);
		c.close();
	}

	public void insertPractice(boolean isNgondro, int order, String title, int iconId, int thumbId,
			int totalCount) {
		insertPractice(isNgondro, order, title, String.valueOf(iconId), String.valueOf(thumbId), totalCount);
	}

	public void insertPractice(boolean isNgondro, int order, String title, String iconUrl, String thumbUrl,
			int totalCount) {
		SQLiteStatement insertQueryPractice = db.compileStatement(INSERT_PRACTICE_QUERY);

		insertQueryPractice.clearBindings();
		insertQueryPractice.bindLong(1, isNgondro ? 1l : 0l);
		insertQueryPractice.bindLong(2, order);
		insertQueryPractice.bindString(3, title);
		insertQueryPractice.bindString(4, iconUrl);
		insertQueryPractice.bindString(5, thumbUrl);
		insertQueryPractice.bindLong(6, totalCount);

		insertQueryPractice.executeInsert();
		insertQueryPractice.close();
	}

	public boolean hasNgondroEntries(Context ctx) {
		SQLiteStatement queryNgondroPresence = db.compileStatement(NGONDRO_CHEK_QUERY);
		boolean result = queryNgondroPresence.simpleQueryForLong() != 0;
		queryNgondroPresence.close();

		return result;
	}

	/*
	 * public List<PracticeEntry> getPractices(boolean ngondro){ Cursor q =
	 * db.rawQuery(PRACTICES_STATUS, new String [] {ngondro ? "1" : "0"});
	 * List<PracticeEntry> result = new ArrayList<PracticeEntry>();
	 * 
	 * if (q.moveToFirst()) do{ result.add(new PracticeEntry(q)); } while
	 * (q.moveToNext());
	 * 
	 * return result; }
	 */

	public PracticeEntry getPractice(long id) {
		/*
		 * Cursor c = db.query(PRACTICE_TABLE_NAME, new String[] { "*" },
		 * BaseColumns._ID + " = ?", new String[] { String .valueOf(id) }, null,
		 * null, null);
		 */

		Cursor c = db.rawQuery(SINGLE_PRACTICE_STATUS.replaceFirst("\\?", String.valueOf(id)), /*
																								 * new
																								 * String
																								 * [
																								 * ]
																								 * {
																								 * String
																								 * .
																								 * valueOf
																								 * (
																								 * id
																								 * )
																								 * }
																								 */null);

		PracticeEntry entry = new PracticeEntry(c);
		c.close();
		return entry;
	}

	public void updatePractice(PracticeEntry entry) {
		int affected;

		// sanitize input so we insert only valid data
		ContentValues values = sanitizeColumns(entry);

		if ((affected = db.update(PRACTICE_TABLE_NAME, values, BaseColumns._ID + " = ?",
				new String[] { String.valueOf(entry.getId()) })) != 1) {
			throw new SQLException(
					String.format("Affected %d rows instead just one during update.", affected));
		}

		long currentCount = getDoneCount(entry);
		if (currentCount != entry.getCurrentCount()) {
			db.delete(PRACTICE_HISTORY_TABLE_NAME, KEY_PRACTICE_ID + " = ? AND " + KEY_DATE + "=0",
					new String[] { String.valueOf(entry.getId()) });

			currentCount = getDoneCount(entry);

			insertSession(entry.getId(), "0", (int) (entry.getCurrentCount() - currentCount));
		}
	}

	private ContentValues sanitizeColumns(PracticeEntry entry) {
		ContentValues originals = entry.getValues();
		ContentValues values = new ContentValues(originals);
		for (Entry<String, Object> e : originals.valueSet()) {
			if (!tableColumns.get(PRACTICE_TABLE_NAME).contains(e.getKey())) {
				values.remove(e.getKey());
			}

		}
		return values;
	}

	private long getDoneCount(PracticeEntry entry) {
		long result;
		Cursor c = db.rawQuery(PRACTICE_DONE_COUNT, new String[] { String.valueOf(entry.getId()) });
		c.moveToFirst();
		result = c.getLong(0);
		c.close();

		return result;
	}

	public void insertSession(int practiceId, int count) {
		SQLiteStatement insertTodayQuerySession = db.compileStatement(INSERT_TODAY_SESSION_QUERY);

		insertTodayQuerySession.clearBindings();
		insertTodayQuerySession.bindLong(1, practiceId);
		insertTodayQuerySession.bindLong(2, count);

		insertTodayQuerySession.executeInsert();
		insertTodayQuerySession.close();
	}

	public void insertSession(int practiceId, String date, int count) {
		SQLiteStatement insertQuerySession = db.compileStatement(INSERT_SESSION_QUERY);

		insertQuerySession.clearBindings();
		insertQuerySession.bindLong(1, practiceId);
		insertQuerySession.bindString(2, date);
		insertQuerySession.bindLong(3, count);

		insertQuerySession.executeInsert();
		insertQuerySession.close();
	}

	public void insertPractice(PracticeEntry entry) {
		// ContentValues entryValues = entry.getValues();
		patchMissingDefaults(entry);
		ContentValues entryValues = sanitizeColumns(entry);
		long columnId;
		if ((columnId = db.insert(PRACTICE_TABLE_NAME, null, entryValues)) == -1) {
			throw new SQLException("There was an error inserting new row.");
		}

		entry.getValues().put(BaseColumns._ID, columnId);

		updatePractice(entry);
	}

	private void patchMissingDefaults(PracticeEntry entry) {
		if (!entry.getValues().containsKey(KEY_ISNGONDRO)) {
			entry.getValues().put(KEY_ISNGONDRO, false);
		}

		if (!entry.getValues().containsKey(KEY_ORDER)) {
			SQLiteStatement queryNextSortOrder = db.compileStatement(NEXT_SORT_ORDER);
			entry.getValues().put(KEY_ORDER, queryNextSortOrder.simpleQueryForLong());
			queryNextSortOrder.close();
		}

		/*
		 * if (!values.containsKey(KEY_SCHEDULEDCOMPLETION)){
		 * 
		 * values.put(KEY_SCHEDULEDCOMPLETION, 0); }
		 */
	}

	public void deletePractice(long id) {
		int affected;

		db.delete(PRACTICE_HISTORY_TABLE_NAME, BaseColumns._ID + " = ?", new String[] { String.valueOf(id) });
		if ((affected = db.delete(PRACTICE_TABLE_NAME, BaseColumns._ID + " = ?",
				new String[] { String.valueOf(id) })) != 1) {
			throw new SQLException(String.format("Affected %d rows instead just one during deletion.",
					affected));
		}
	}

	public Cursor getPracticesStatuses(boolean ngondroGroup) {
		return db.rawQuery(PRACTICES_STATUS.replaceFirst("\\?", ngondroGroup ? "1" : "0"), null);
	}
	
	public static SQLiteDatabase getDb() {
		return db;
	}


    public static class MySQLiteOpenHelper extends SQLiteOpenHelper {
		public MySQLiteOpenHelper(Context context) {
			super(context, DBNAME, null, DBVERSION);
		}

		@Override
		public void onCreate(SQLiteDatabase db) {
			db.execSQL(PRACTICE_TABLE_CREATE);
			db.execSQL(PRACTICE_HISTORY_TABLE_CREATE);
			db.execSQL(DONE_TODAY_VIEW_CREATE);
		}

		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
			patchIcons(db);
		}

		public static void patchIcons(SQLiteDatabase db) {
			Log.d("MTRK", "Patching icons for " + db);
			
			updateNgondroIcons(db, 1, R.drawable.refuge, R.drawable.icon_refuge);
			updateNgondroIcons(db, 2, R.drawable.diamond_mind_big, R.drawable.icon_diamond_mind);
			updateNgondroIcons(db, 3, R.drawable.mandala_offering_big, R.drawable.icon_mandala_offering);
			updateNgondroIcons(db, 4, R.drawable.guru_yoga_big, R.drawable.icon_guru_yoga);

			updateOtherIcons(db, R.drawable.karmapa, R.drawable.icon_karmapa);
		}

		public static void updateNgondroIcons(SQLiteDatabase db, int id, int iconId, int thumbId) {
			db.execSQL("UPDATE " + PRACTICE_TABLE_NAME + " SET " + KEY_ICONURL + " = '" + iconId + "', "
					+ KEY_THUMBURL + " = '" + thumbId + "' WHERE " + KEY_ICONURL + " NOT LIKE 'content%' AND " + BaseColumns._ID + " = " + id);
		}

		public static void updateOtherIcons(SQLiteDatabase db, int iconId, int thumbId) {
			db.execSQL("UPDATE " + PRACTICE_TABLE_NAME + " SET " + KEY_ICONURL + " = '" + iconId + "', "
					+ KEY_THUMBURL + " = '" + thumbId + "' WHERE " + KEY_ICONURL + " NOT LIKE 'content%' AND " + KEY_ISNGONDRO + " = 0");
		}
	}


	public void patchIcons() {
		MySQLiteOpenHelper.patchIcons(db);
	}
}