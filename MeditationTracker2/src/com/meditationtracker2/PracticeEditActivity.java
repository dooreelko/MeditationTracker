package com.meditationtracker2;

import java.util.Calendar;

import android.os.Bundle;
import android.text.TextWatcher;
import android.view.View;
import android.widget.DatePicker;
import android.widget.DatePicker.OnDateChangedListener;
import android.widget.EditText;
import butterknife.InjectView;
import butterknife.OnClick;

import com.actionbarsherlock.view.Menu;
import com.meditationtracker2.content.Practice;
import com.meditationtracker2.helper.SimpleTextWatcher;

public class PracticeEditActivity extends PracticeActivity {
	@InjectView(R.id.editPracticeName) EditText editPracticeName;
	@InjectView(R.id.editPracticeTotal) EditText editPracticeTotal;
	@InjectView(R.id.editPracticeCompletedCount) EditText editPracticeCompletedCount;
	@InjectView(R.id.editScheduledPerSession) EditText editScheduledPerSession;
	@InjectView(R.id.datePickerScheduledEnd) DatePicker datePickerScheduledEnd;
	private Practice practice;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_edit_practice);
		practice = getPractice();
		
		getSupportActionBar().setTitle(practice.title);
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		updateFields();
		
		editScheduledPerSession.addTextChangedListener(onScheduledCountChanged);
		datePickerScheduledEnd.init(2013, 5, 28, onScheduledDateChanged);
	}

	private void updateFields() {
		editPracticeName.setText(practice.title);
		editPracticeTotal.setText(String.valueOf(practice.totalCount));
		editScheduledPerSession.setText(String.valueOf(practice.getScheduledForToday()));
		
		recalculatePicker();		
	}

	@OnClick(R.id.buttonPracticeImage)
	void onClickChangePicture(View v) {
	}

	private TextWatcher onScheduledCountChanged = new SimpleTextWatcher() {
		@Override
		public void onTextChanged(CharSequence s, int start, int before, int count) {
			recalculatePicker();
		}
	};

	protected void recalculatePicker() {
		practice.setScheduledForToday(Integer.valueOf(editScheduledPerSession.getText().toString()));
		Calendar cal = practice.getScheduledCompletion();
		
		datePickerScheduledEnd.updateDate(cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH));
	}
	
	private OnDateChangedListener onScheduledDateChanged = new OnDateChangedListener() {
		
		@Override
		public void onDateChanged(DatePicker view, int year, int monthOfYear,
				int dayOfMonth) {
			Calendar cal = Calendar.getInstance();
			cal.set(year, monthOfYear, dayOfMonth);
			practice.setScheduledCompletion(cal);
		}
	};
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getSupportMenuInflater().inflate(R.menu.activity_edit_practice, menu);
		return true;
	}
}
