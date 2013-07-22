package com.meditationtracker2;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import butterknife.InjectView;
import butterknife.OnClick;
import butterknife.Views;

import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.meditationtracker2.content.Practice;

public class PracticeDetailActivity extends PracticeActivity {
	@InjectView(R.id.practice_image) ImageView practiceImage;
	@InjectView(R.id.textScheduledForToday) TextView textScheduledToday;
	@InjectView(R.id.textCompletedToday) TextView textCompletedToday;
	@InjectView(R.id.textLastPracticeDate) TextView textLastPracticeDate;
	@InjectView(R.id.textScheduledCompletion) TextView textScheduledCompletionDate;
	@InjectView(R.id.textCurrentCount) TextView textCurrentCount;
	@InjectView(R.id.textTotalCount) TextView textTotalCount;
	@InjectView(R.id.progressBarPractice) ProgressBar progressBar;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_practice_detail);
		Views.inject(this);
	
		Practice practice = getPractice();
		
		getSupportActionBar().setTitle(practice.title);
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		updateFields(practice);
	}

	@OnClick(R.id.buttonStart)
	void onClickStartPractice(View v) {
		startPractice();
	}

	private void updateFields(Practice practice) {
		//TODO: image provider		practiceImage.setImageURI(practice.imageUrl);
		
		textScheduledToday.setText(String.valueOf(practice.getScheduledForToday()));
		textCompletedToday.setText(String.valueOf(practice.completedToday));
		textLastPracticeDate.setText(String.valueOf(practice.lastPracticeDate)); //TODO: format
		textScheduledCompletionDate.setText(String.valueOf(practice.getScheduledCompletion()));
		textCurrentCount.setText(String.valueOf(practice.currentCount));
		textTotalCount.setText(String.valueOf(practice.totalCount));
		progressBar.setMax(practice.totalCount);
		progressBar.setProgress(practice.currentCount);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getSupportMenuInflater().inflate(R.menu.activity_practice_detail, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		
		switch (item.getItemId()) {
		case(android.R.id.home): 
			this.finish();
			break;
		
		case(R.id.menu_edit): 
			startActivityForResult(new Intent(this, PracticeEditActivity.class)
				.putExtra(Constants.PRACTICE_ID, getPracticeId()), Constants.PRACTICE_EDIT_DONE);
			break;
		case R.id.menu_start:
			startPractice();
			break;

		}
		
		return true;
	}

	private void startPractice() {
		startActivityForResult(new Intent(this, PracticeDoActivity.class)
			.putExtra(Constants.PRACTICE_ID, getPracticeId()), Constants.PRACTICE_DONE);
	}
}
