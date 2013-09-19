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
	
	public PracticeHistory() {
		
	}
	
	public PracticeHistory(final Practice practice, final int count) {
		this.practice = practice;
		doneCount = count;
		practiceDate = new Date();
	}

	@DatabaseField(generatedId = true, allowGeneratedIdInsert = true, columnName = "_id")
    public int id;

	@DatabaseField(canBeNull = false, columnName = "PRACTICE_ID", foreign = true)
	public Practice practice;
	
	@DatabaseField(columnName = "PRACTICE_DATE", canBeNull=true)
    public Date practiceDate;
	
	@DatabaseField(columnName = "DONE_COUNT")
    public int doneCount;
}
