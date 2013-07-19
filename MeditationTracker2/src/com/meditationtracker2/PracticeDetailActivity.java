package com.meditationtracker2;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import butterknife.InjectView;
import butterknife.Views;

import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.meditationtracker2.content.Practice;
import com.meditationtracker2.content.PracticeProviderFactory;

public class PracticeDetailActivity extends SherlockActivity {
	@InjectView(R.id.practice_image) ImageView practiceImage;
	@InjectView(R.id.textScheduledForToday) TextView textScheduledToday;
	@InjectView(R.id.textCompletedToday) TextView textCompletedToday;
	@InjectView(R.id.textLastPracticeDate) TextView textLastPracticeDate;
	@InjectView(R.id.textScheduledCompletion) TextView textScheduledCompletionDate;
	@InjectView(R.id.textCurrentCount) TextView textCurrentCount;
	@InjectView(R.id.textTotalCount) TextView textTotalCount;
	@InjectView(R.id.progressBarPractice) ProgressBar progressBar;
	
	private Practice practice;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_practice_detail);
		Views.inject(this);
	
		int practiceId = getIntent().getIntExtra(Constants.PRACTICE_ID, -1);
		practice = PracticeProviderFactory.getMeAProvider(this).getPractice(practiceId);
		
		getSupportActionBar().setTitle(practice.title);
		updateImage(practice);
		updateStats(practice);
		
		((ImageButton)findViewById(R.id.imageButton1)).setOnClickListener(onStartPractice);
	}

	private OnClickListener onStartPractice = new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			startActivityForResult(new Intent(PracticeDetailActivity.this, PracticeDoActivity.class).putExtra("id", 0 /*TODO*/), Constants.PRACTICE_DONE);
		}
	};

	private void updateImage(Practice practice) {
//		practiceImage.setImageURI(practice.imageUrl);
	}

	private void updateStats(Practice practice) {
		
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
			startActivityForResult(new Intent(PracticeDetailActivity.this, PracticeEditActivity.class).putExtra("id", 0/*TODO*/), Constants.PRACTICE_EDIT_DONE);
			
		}
		
		return true;
	}
}
