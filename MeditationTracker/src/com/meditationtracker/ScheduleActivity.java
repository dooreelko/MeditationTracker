package com.meditationtracker;

import java.util.Calendar;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.DatePicker.OnDateChangedListener;

import doo.util.Pair;
import doo.util.Util;

public class ScheduleActivity extends BaseActivity
{
	private Long totalCount;
	private Long currentCount;
	private EditText scheduleCountText;

	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.schedule);
		
		Bundle extras = getIntent().getExtras();
		if (extras != null){
			String val;
			((TextView)findViewById(R.id.textRepetitionCount)).setText(val = extras.getString(ExtraKeys.TotalCount));
			Pair<Boolean, Long> parsed = Util.tryParse(val);
			if (parsed._1){
				totalCount = parsed._2;
			}
			else
			{
				//TODO: error and exit
			}
			
			((TextView)findViewById(R.id.textCompletedRepetitions)).setText(val = extras.getString(ExtraKeys.CurrentCount));
			parsed = Util.tryParse(val);
			if (parsed._1){
				currentCount = parsed._2;
			}
			else
			{
				//TODO: error and exit
			}
			
			
			scheduleCountText = (EditText)findViewById(R.id.textScheduledForToday);
			scheduleCountText.setText(extras.getString(ExtraKeys.ScheduledCount));
			scheduleCountText.addTextChangedListener(textChangedListener);
			//scheduleCountText.setOnKeyListener(keyPressListener);
		}

		DatePicker dp = (DatePicker)findViewById(R.id.pickerScheduledEndDate);
		dp.init(2010, 01, 01, onDateChangedListener);
		
		updatePicker();
	}

	private boolean softUpdate;
	
	protected void updatePicker()
	{
		if (softUpdate)
			return;
		
		softUpdate = true;
	
		try {
			DatePicker dp = (DatePicker)findViewById(R.id.pickerScheduledEndDate);
	
			String scheduledString = ((TextView)findViewById(R.id.textScheduledForToday)).getText().toString();
			Pair<Boolean, Long> parsed = Util.tryParse(scheduledString);
			if (parsed._1 && parsed._2 != 0){
				long remainingDays = (totalCount - currentCount) / parsed._2 + 1;
				Calendar cal = Calendar.getInstance();
				cal.add(Calendar.DAY_OF_YEAR, (int)remainingDays);
				
				dp.updateDate(cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH));
				
				setResult(RESULT_OK, new Intent().putExtra(ExtraKeys.ScheduledCount, parsed._2));
			}
		}
		catch(Exception ignore) {} // depending on theme min/max values for date picker vary and thus can throw

		softUpdate = false;
		
	}

	protected void updateCount(int year, int monthOfYear, int dayOfMonth)
	{
		if (softUpdate)
			return;
		
		softUpdate = true;
		
		try {
			Calendar now = Calendar.getInstance();
			now.add(Calendar.DAY_OF_YEAR, 1);
			
			Calendar then = Calendar.getInstance();
			then.set(year, monthOfYear, dayOfMonth);
			
			if (then.after(now)) {
				long days = (then.getTimeInMillis() - now.getTimeInMillis()) / (1000*60*60*24) + 1;
				
				long newCount = Math.round(((double)(totalCount - currentCount % totalCount)) / days);
				scheduleCountText.setText(String.valueOf(newCount < 1 ? 1 : newCount));

				setResult(RESULT_OK, new Intent().putExtra(ExtraKeys.ScheduledCount, newCount));
			}
		}
		catch(Exception ignore) {}

		softUpdate = false;
	}

	
	private TextWatcher textChangedListener = new TextWatcher()
	{
		public void onTextChanged(CharSequence s, int start, int before, int count)
		{
			updatePicker();
		}
		
		public void beforeTextChanged(CharSequence s, int start, int count, int after)
		{
			// TODO Auto-generated method stub
			
		}
		
		public void afterTextChanged(Editable s)
		{
			// TODO Auto-generated method stub
			
		}
	};
	
	private OnDateChangedListener onDateChangedListener = new OnDateChangedListener()
	{
		public void onDateChanged(DatePicker view, int year, int monthOfYear, int dayOfMonth)
		{
			updateCount(year, monthOfYear, dayOfMonth);
		}
	};
}
