package com.meditationtracker2;

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.os.Bundle;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import butterknife.InjectView;
import butterknife.OnClick;
import butterknife.Views;

import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.meditationtracker2.content.Practice;
import com.meditationtracker2.content.PracticeProviderFactory;
import com.meditationtracker2.helper.SimpleTextWatcher;

public class PracticeDoActivity extends SherlockActivity {
	
	@InjectView(R.id.editMalaCount) EditText editMalaCount;
	@InjectView(R.id.editPracticeTotal) EditText editMalaSize;
	@InjectView(R.id.editPracticeCompletedCount) EditText editSessionTotalSize;
	
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
		
		editMalaCount.addTextChangedListener(onMalaCountOrSizeTextChanged);
		editSessionTotalSize.addTextChangedListener(onSessionTotalTextChanged);
		
		totalCount = 0;
		malaCount = 0;
		malaSize = practice.malaSize;
		
		updateFields();
	}

	private TextWatcher onMalaCountOrSizeTextChanged = new SimpleTextWatcher() {
		
		@Override
		public void onTextChanged(CharSequence s, int start, int before, int count) {
			Integer newMalaCount;
			Integer newMalaSize;
			
			try {
				newMalaCount = Integer.valueOf(editMalaCount.getText().toString());
				newMalaSize = Integer.valueOf(editMalaSize.getText().toString());
				
				malaCount = newMalaCount;
				malaSize = newMalaSize;
				totalCount = newMalaCount*newMalaSize;
				
				updateFields();
			} catch(Exception e) { }
		}
	};

	private TextWatcher onSessionTotalTextChanged = new SimpleTextWatcher() {
		
		@Override
		public void onTextChanged(CharSequence s, int start, int before, int count) {
			try {
				totalCount = Integer.parseInt(s.toString());
				
				boolean enableOthers = totalCount == malaCount*malaSize;
				editMalaCount.setEnabled(enableOthers);
				editMalaSize.setEnabled(enableOthers);
			} catch (Exception e) {}
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
		AlertDialog.Builder builder = new Builder(this);
		builder
			.setTitle(R.string.save_changes_title)
			.setMessage(R.string.save_changes_message)
			.setPositiveButton(android.R.string.yes, new OnClickListener() {
				
				@Override
				public void onClick(DialogInterface dialog, int which) {
					saveAndClose();
				}
			})
			.setNegativeButton(android.R.string.no, new OnClickListener() {
				
				@Override
				public void onClick(DialogInterface dialog, int which) {
					PracticeDoActivity.this.finish();
				}
			})
			.setIcon(android.R.drawable.ic_dialog_alert)
			.show();
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
