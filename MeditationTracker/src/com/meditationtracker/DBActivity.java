package com.meditationtracker;

import android.os.Bundle;

public class DBActivity extends BaseActivity
{
	protected static final int DONE_EDITING = 0;
	protected PracticeEntry practice;
	protected long id;

	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		id = getIntent().getLongExtra(ExtraKeys.ID, -1);
		
		if (id != -1){
			practice = db().getPractice(id);
		}
	}
	
	@Override
	protected void onStart() {
		super.onStart();
	}	
}
