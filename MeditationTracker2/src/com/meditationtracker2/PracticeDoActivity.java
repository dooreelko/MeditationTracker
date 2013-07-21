package com.meditationtracker2;

import butterknife.InjectView;
import butterknife.OnClick;
import butterknife.Views;

import com.actionbarsherlock.app.SherlockActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;

import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.meditationtracker2.content.Practice;
import com.meditationtracker2.content.PracticeProviderFactory;

public class PracticeDoActivity extends SherlockActivity {
	
	@InjectView(R.id.editMalaCount) EditText editMalaCount;
	@InjectView(R.id.editMalaSize) EditText editMalaSize;
	@InjectView(R.id.editSessionTotalCount) EditText editSessionTotalSize;
	
	private Practice practice;
	
	private int malaCount;
	private int malaSize;
	private int totalCount;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_practice_do);
		Views.inject(this);

		int practiceId = getIntent().getIntExtra(Constants.PRACTICE_ID, -1);
		practice = PracticeProviderFactory.getMeAProvider(this).getPractice(practiceId);
		
		getSupportActionBar().setTitle(practice.title);
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		
		editMalaCount.setOnEditorActionListener(onMalaCountOrSizeChanged);
		editMalaSize.setOnEditorActionListener(onMalaCountOrSizeChanged);
		editSessionTotalSize.setOnEditorActionListener(onSessionTotalSizeChanged);
		
		totalCount = 0;
		malaCount = 0;
		malaSize = practice.malaSize;
		
		updateFields();
	}

	private OnEditorActionListener onMalaCountOrSizeChanged = new OnEditorActionListener() {
		
		@Override
		public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
			Integer newMalaCount;
			Integer newMalaSize;
			
			try {
				newMalaCount = Integer.valueOf(editMalaCount.getText().toString());
				newMalaSize = Integer.valueOf(editMalaSize.getText().toString());
				
				malaCount = newMalaCount;
				malaSize = newMalaSize;
				totalCount = newMalaCount*newMalaSize;
				
				updateFields();
			} catch(Exception e) {
				
			}
			
			return false;
		}
	};

	private OnEditorActionListener onSessionTotalSizeChanged = new OnEditorActionListener() {
		
		@Override
		public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
			try {
				totalCount = Integer.parseInt(editSessionTotalSize.getText().toString());
				
				boolean enableOthers = totalCount == malaCount*malaSize;
				editMalaCount.setEnabled(enableOthers);
				editMalaSize.setEnabled(enableOthers);
			} catch (Exception e) {}
			
			return false;
		}
	};

	@OnClick(R.id.buttonAddMala)
	void onClickAddMala(View v) {
		malaCount++;
		updateFields();
	}

	private void updateFields() {
		editMalaCount.setTag(String.valueOf(malaCount));
		editMalaSize.setTag(String.valueOf(malaSize));
		editSessionTotalSize.setTag(String.valueOf(totalCount));
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		
		switch (item.getItemId()) {
			case (android.R.id.home): 
				askIfToSaveAndMaybeDo();
				break;
			
			case (R.id.menu_accept):
				saveAndClose();
				break;
	
			case (R.id.menu_discard):
				finish();
		}
		
		return true;
	}
	
	private void askIfToSaveAndMaybeDo() {
		
	}

	private void saveAndClose() {
		practice.addSession(totalCount);
		PracticeProviderFactory.getMeAProvider(this).savePractice(practice);
		finish();
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getSupportMenuInflater().inflate(R.menu.activity_practice_do, menu);
		return true;
	}
}
