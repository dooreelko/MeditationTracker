package com.meditationtracker2.content.data;

import java.util.Date;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;


@DatabaseTable(tableName = "PracticeHistory")
public class PracticeHistory {
/*
 * _id INTEGER PRIMARY KEY, 
 * PRACTICE_ID INTEGER NOT NULL, 
 * PRACTICE_DATE TEXT default CURRENT_DATE, 
 * DONE_COUNT INTEGER NOT NULL
 * */
	
	@DatabaseField(generatedId = true, allowGeneratedIdInsert = true, columnName = "_id")
    public int id;

	@DatabaseField(canBeNull = false, columnName = "PRACTICE_ID", foreign = true)
	public Practice Practice;
	
	@DatabaseField(columnName = "PRACTICE_DATE")
    public Date PracticeDate;
	
	@DatabaseField(columnName = "DONE_COUNT")
    public int DoneCount;
}
