package com.meditationtracker;

import android.os.Bundle;

public class DBActivity extends VerboseActivity
{
	protected static final int DONE_EDITING = 0;
	protected PracticeDatabase db;
	protected PracticeEntry practice;
	protected long id;

	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		id = getIntent().getLongExtra(ExtraKeys.ID, -1);
		
		db = new PracticeDatabase(this);
		if (id != -1){
			practice = db.getPractice(id);
		}
	}
}
