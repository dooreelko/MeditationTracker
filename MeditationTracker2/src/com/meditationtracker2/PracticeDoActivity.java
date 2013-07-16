package com.meditationtracker2;

import com.actionbarsherlock.app.SherlockActivity;

import android.os.Bundle;
import com.actionbarsherlock.view.Menu;

public class PracticeDoActivity extends SherlockActivity {

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_practice_do);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getSupportMenuInflater().inflate(R.menu.activity_practice_do, menu);
		return true;
	}

}
