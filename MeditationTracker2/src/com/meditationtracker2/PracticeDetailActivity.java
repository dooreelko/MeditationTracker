package com.meditationtracker2;

import com.actionbarsherlock.app.SherlockActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;

import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.meditationtracker2.content.MockContent;
import com.meditationtracker2.content.MockContent.Practice;

public class PracticeDetailActivity extends SherlockActivity {

	protected static final int PRACTICE_DONE = 0;
	private static final int PRACTICE_EDIT_DONE = 1;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_practice_detail);
	
		Practice practice = new MockContent(this).ITEMS.get(0);
		
		getSupportActionBar().setTitle(practice.title);
		updateImage(practice);
		updateStats(practice);
		
		((ImageButton)findViewById(R.id.imageButton1)).setOnClickListener(onStartPractice);
	}

	private OnClickListener onStartPractice = new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			startActivityForResult(new Intent(PracticeDetailActivity.this, PracticeDoActivity.class).putExtra("id", 0 /*TODO*/), PRACTICE_DONE);
		}
	};

	private void updateImage(Practice practice) {
		findViewById(R.id.practice_image);
		
	}

	private void updateStats(Practice practice) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getSupportMenuInflater().inflate(R.menu.activity_practice_detail, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		
		if (item.getItemId() == R.id.menu_edit) {
			startActivityForResult(new Intent(PracticeDetailActivity.this, PracticeEditActivity.class).putExtra("id", 0/*TODO*/), PRACTICE_EDIT_DONE);
			
		}
		
		return true;
	}
}
