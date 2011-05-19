package com.meditationtracker;

import android.content.ContentValues;
import android.database.Cursor;
import android.provider.BaseColumns;

public class PracticeEntry
{
	/*
	 * private int resourceIdIcon; private int resourceIdTitle; private int
	 * totalCount; 
	 * 
	 * private long scheduledCompletion; private long lastPracticed;
	 */
	
	private ContentValues values;

	public PracticeEntry(Cursor q)
	{
		int columnCount = q.getColumnCount();
		values = new ContentValues(columnCount);
		if (q.moveToFirst())
		{
			for (int x = 0; x <columnCount; x++)
			{
				values.put(q.getColumnName(x), q.getString(x));
			}
		}
		
		q.close();
	}

	public PracticeEntry(int id, String title, String imageUrl, String thumbUrl, int totalCount, int currentCount, int malaSize)
	{
		values = new ContentValues(5);
		if (id != -1)
			values.put(BaseColumns._ID, id);
		values.put(PracticeDatabase.KEY_TITLE, title);
		values.put(PracticeDatabase.KEY_ICONURL, imageUrl);
		values.put(PracticeDatabase.KEY_THUMBURL, thumbUrl);
		values.put(PracticeDatabase.KEY_TOTALCOUNT, totalCount);
		values.put(PracticeDatabase.KEY_DONE, currentCount);
		values.put(PracticeDatabase.KEY_MALASIZE, malaSize);
	}
	
	public PracticeEntry(String title, String imageUrl, String thumbUrl, int totalCount, int currentCount, int malaSize)
	{
		this(-1, title, imageUrl, thumbUrl, totalCount, currentCount, malaSize);
	}

	public ContentValues getValues()
	{
		return values;
	}
	
	public int getId()
	{
		Integer id = values.getAsInteger(BaseColumns._ID);
		
		return id == null ? -1 : id;
	}
	
	public int getCurrentCount() {
		return values.getAsInteger(PracticeDatabase.KEY_DONE);
//		return currentCount;
	}

}
