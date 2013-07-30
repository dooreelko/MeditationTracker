package com.meditationtracker2;

import java.util.Calendar;

import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextWatcher;
import android.view.View;
import android.widget.DatePicker;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.DatePicker.OnDateChangedListener;
import android.widget.EditText;
import butterknife.InjectView;
import butterknife.OnClick;
import butterknife.Views;

import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.meditationtracker2.content.Practice;
import com.meditationtracker2.content.PracticeProviderFactory;
import com.meditationtracker2.helper.SimpleTextWatcher;

public class PracticeEditActivity extends PracticeActivity {
	@InjectView(R.id.practice_image) ImageView practiceImage;
	@InjectView(R.id.buttonPracticeImage) ImageButton buttonPracticeImage;
	@InjectView(R.id.editPracticeName) EditText editPracticeName;
	@InjectView(R.id.editPracticeTotal) EditText editPracticeTotal;
	@InjectView(R.id.editPracticeCompletedCount) EditText editPracticeCompletedCount;
	@InjectView(R.id.editScheduledPerSession) EditText editScheduledPerSession;
	@InjectView(R.id.datePickerScheduledEnd) DatePicker datePickerScheduledEnd;

	private Practice practice = new Practice();
	private boolean softUpdate;
	private boolean dirty;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_edit_practice);
		Views.inject(this);
		
		if (getPracticeIdFromIntent() != Constants.NO_PRACTICE_ID) {
			practice = getPractice();
		}
		
		getSupportActionBar().setTitle(practice.title);
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		updateFields();
		
		editPracticeName.addTextChangedListener(onPracticeNameChanged);
		
		editPracticeTotal.addTextChangedListener(onScheduledCountChanged);
		editPracticeCompletedCount.addTextChangedListener(onScheduledCountChanged);
		editScheduledPerSession.addTextChangedListener(onScheduledCountChanged);
		datePickerScheduledEnd.init(2013, 5, 28, onScheduledDateChanged);
	}

	private void updateFields() {
		softUpdate = true;
		
		updatePictures();
		
		editPracticeName.setText(practice.title);
		editPracticeTotal.setText(String.valueOf(practice.totalCount));
		updateScheduledCount();
		
		recalculatePicker();
		
		softUpdate = false;
	}

	protected void updateScheduledCount() {
		editScheduledPerSession.setText(String.valueOf(practice.getScheduledForToday()));
	}

	protected void updatePictures() {
		Uri uri = Uri.parse(practice.imageUrl);
		practiceImage.setImageURI(uri);
		buttonPracticeImage.setImageURI(uri);
	}

	@OnClick(R.id.buttonPracticeImage)
	void onClickChangePicture(View v) {
	}

	private TextWatcher onPracticeNameChanged = new SimpleTextWatcher() {
		@Override
		public void onTextChanged(CharSequence s, int start, int before, int count) {
			practice.title = s.toString();
			dirty = true;
		}
	};
	
	private TextWatcher onScheduledCountChanged = new SimpleTextWatcher() {
		@Override
		public void onTextChanged(CharSequence s, int start, int before, int count) {
			if (softUpdate) {
				return;
			}
			try {
				practice.totalCount = Integer.valueOf(editPracticeTotal.getText().toString());
				practice.currentCount = Integer.valueOf(editPracticeCompletedCount.getText().toString());
				practice.setScheduledForToday(Integer.valueOf(editScheduledPerSession.getText().toString()));
				
				recalculatePicker();

				dirty = true;
			} catch (Exception e) {}
			
		}
	};

	protected void recalculatePicker() {
		if (softUpdate) {
			return;
		}
		
		softUpdate = true;
		
		try {
			if (practice.getScheduledForToday() > 0) {
				Calendar cal = Calendar.getInstance();
				cal.setTimeInMillis(practice.getScheduledCompletion().getTime());
				
				datePickerScheduledEnd.updateDate(cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH));
			}
		} catch (Exception e) {}
		softUpdate = false;
	}
	
	private OnDateChangedListener onScheduledDateChanged = new OnDateChangedListener() {
		
		@Override
		public void onDateChanged(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
			if (softUpdate) {
				return;
			}

			Calendar cal = Calendar.getInstance();
			cal.set(year, monthOfYear, dayOfMonth);
			practice.setScheduledCompletion(cal);
			
			softUpdate = true;
			updateScheduledCount();
			softUpdate = false;
			dirty = true;
		}
	};
	
	@Override
	public void onBackPressed() {
		askIfToSaveAndMaybeDo();
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		
		switch (item.getItemId()) {
			case (android.R.id.home): 
				askIfToSaveAndMaybeDo();
				break;
			case (R.id.menu_discard):
				finish();
				break;
			case (R.id.menu_picture):
				pickPicture();
				break;
			case (R.id.menu_accept):
				saveAndClose();
		}
		
		return true;
	}
	
	private void saveAndClose() {
		if (dirty) {	
			PracticeProviderFactory.getMeAProvider(this).savePractice(practice);
		}
		
		finish();
	}

	private void pickPicture() {
		// TODO Auto-generated method stub
		
	}

	private void askIfToSaveAndMaybeDo() {
		if (!dirty) {
			finish();
		}
		
		doTheYesNoDialog(R.string.save_changes_title, R.string.save_practice_changes_message, 
			new OnClickListener() {
				
				@Override
				public void onClick(DialogInterface dialog, int which) {
					saveAndClose();
				}
			}, new OnClickListener() {
				
				@Override
				public void onClick(DialogInterface dialog, int which) {
					finish();
				}
			});
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);

		outState.putString(Constants.IMAGE_URL, practice.imageUrl);
	}

	@Override
	protected void onRestoreInstanceState(Bundle savedInstanceState) {
		super.onRestoreInstanceState(savedInstanceState);

		practice.imageUrl = savedInstanceState.getString(Constants.IMAGE_URL);

		updatePictures();
		recalculatePicker();
	}
	
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getSupportMenuInflater().inflate(R.menu.activity_practice_edit, menu);
		return true;
	}
}
