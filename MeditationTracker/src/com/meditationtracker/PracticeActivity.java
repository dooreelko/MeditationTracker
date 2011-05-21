package com.meditationtracker;

import java.util.Calendar;

import com.meditationtracker.controls.MenuBar;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import doo.util.Pair;
import doo.util.Util;

public class PracticeActivity extends VerboseActivity
{
	protected static final int SCHEDULE_CHANGED = 0;
	protected static final int PRACTICE_CHANGED = 1;
	protected static final int SESSION_DONE = 2;
	private PracticeDatabase db;
	private PracticeEntry practice;

	protected long id;
	protected String title;
	protected String scheduledCount;
	protected String totalCount;
	protected String currentCount;
	protected String imgUrl;
	protected long customMalaSize;

	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);

		setContentView(R.layout.practice);

		findViewById(R.id.scheduleButton).setOnClickListener(scheduleClicked);
		findViewById(R.id.editPracticeButton).setOnClickListener(editClicked);
		findViewById(R.id.startPracticeButton).setOnClickListener(startClicked);

		updateView();
	}

	private void updateView()
	{
		Bundle extras = getIntent().getExtras();

		if (extras != null && extras.containsKey(ExtraKeys.ID))
		{
			id = extras.getLong(ExtraKeys.ID);
			db = new PracticeDatabase(this);
			practice = db.getPractice(id);

			title = practice.getValues().getAsString(PracticeDatabase.KEY_TITLE);
			((MenuBar)findViewById(R.id.menuBar)).setText(title);

			//title = setText(R.id.textPracticeName, PracticeDatabase.KEY_TITLE, practice);
			totalCount = setText(R.id.textRepetitionCount, PracticeDatabase.KEY_TOTALCOUNT, practice);
			currentCount = setText(R.id.textCompletedRepetitions, PracticeDatabase.KEY_DONE, practice);
			scheduledCount = setText(R.id.textScheduledForToday, PracticeDatabase.KEY_SCHEDULEDCOUNT, practice);
			customMalaSize = practice.getValues().getAsInteger(PracticeDatabase.KEY_MALASIZE);
			setText(R.id.textCompletedToday, PracticeDatabase.KEY_DONE_TODAY, practice);

			//			setText(R.id.textLastPracticeDate, PracticeDatabase.KEY_LAST_PRACTICE, practice);
			String lastPractice = practice.getValues().getAsString(PracticeDatabase.KEY_LAST_PRACTICE);
			TextView textLastPractice = (TextView) findViewById(R.id.textLastPracticeDate);

			textLastPractice.setText( lastPractice.length() != 10 ? "-" :					
					Util.formatCalendar(Util.parseSqliteDate(lastPractice), this));
		
			calculateScheduledEnd();
			
			ProgressBar pb = (ProgressBar) findViewById(R.id.practiceProgressBar);
			if (totalCount.compareTo("0") == 0)
			{
				pb.setVisibility(View.GONE);
				findViewById(R.id.textRepetitionCount).setVisibility(View.GONE);
				findViewById(R.id.titleReqRepetitions).setVisibility(View.GONE);
				findViewById(R.id.rowScheduledCompletion).setVisibility(View.GONE);
			}
			else
			{
				pb.setProgress(practice.getValues().getAsDouble(PracticeDatabase.KEY_DONE_PERC).intValue());
			}
			
			imgUrl = practice.getValues().getAsString(PracticeDatabase.KEY_ICONURL);
			Pair<Boolean, Long> parsed = Util.tryParse(imgUrl);
			if (parsed._1)
				((ImageView) findViewById(R.id.imgPractice)).setImageResource(parsed._2.intValue());
			else if (imgUrl != null)
				((ImageView) findViewById(R.id.imgPractice)).setImageURI(Uri.parse(imgUrl));

		} else
		{
			//TODO: do an error here, we cannot go into non-existent practice 
		}
	}

	private void calculateScheduledEnd()
	{
		Pair<Boolean, Long> scheduledCountParsed = Util.tryParse(scheduledCount);
		
		if (scheduledCountParsed._1 && scheduledCountParsed._2 != 0) {
			Pair<Boolean, Long> totalCountParsed = Util.tryParse(totalCount);
			Pair<Boolean, Long> currentCountParsed = Util.tryParse(currentCount);

			Calendar cal = Calendar.getInstance();
			cal.add(Calendar.DAY_OF_YEAR, (int) ((totalCountParsed._2-currentCountParsed._2)/scheduledCountParsed._2) + 1);
			
			((TextView) findViewById(R.id.textScheduledEndDate)).setText(Util.formatCalendar(cal, this));
		}
		else
			((TextView) findViewById(R.id.textScheduledEndDate)).setText("-");
	}

	private String setText(int fieldId, String dbKey, PracticeEntry practice)
	{
		String result;
		((TextView) findViewById(fieldId)).setText(result = practice.getValues().getAsString(dbKey));

		return result;
	}

	private OnClickListener scheduleClicked = new OnClickListener()
	{
		public void onClick(View v)
		{
			startActivityForResult(new Intent(PracticeActivity.this, ScheduleActivity.class).putExtra(
					ExtraKeys.ScheduledCount, scheduledCount).putExtra(ExtraKeys.TotalCount, totalCount).putExtra(
					ExtraKeys.CurrentCount, currentCount), SCHEDULE_CHANGED);
		}
	};

	private OnClickListener editClicked = new OnClickListener()
	{
		public void onClick(View v)
		{
			startActivityForResult(new Intent(PracticeActivity.this, NewOrEditPracticeDBActivity.class).putExtra(
					ExtraKeys.ID, id), PRACTICE_CHANGED);
		}
	};

	private OnClickListener startClicked = new OnClickListener()
	{
		public void onClick(View v)
		{
			SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(PracticeActivity.this);

			long malaSize = 108; 

			if (customMalaSize > 0)
			{
				malaSize = customMalaSize;
			}
			else
			{
				Pair<Boolean, Long> parsed = Util.tryParse(preferences.getString(getString(R.string.prefMalaSize), "108"));
				if (parsed._1){
					malaSize = parsed._2.intValue();
				}
			}

			startActivityForResult(
					new Intent(PracticeActivity.this, SessionActivity.class).
						putExtra(ExtraKeys.ImgURL, imgUrl).
						putExtra(ExtraKeys.MalaSize, malaSize).
						putExtra(ExtraKeys.Title, title), 
					SESSION_DONE);
		}


	};
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data)
	{
		super.onActivityResult(requestCode, resultCode, data);

		if (resultCode != RESULT_OK)
			return;

		switch (requestCode)
		{
		case SCHEDULE_CHANGED:
			if (data.hasExtra(ExtraKeys.ScheduledCount))
			{
				practice.getValues().put(PracticeDatabase.KEY_SCHEDULEDCOUNT,
						data.getLongExtra(ExtraKeys.ScheduledCount, 0));
				db.updatePractice(practice);
			}
			break;
		case PRACTICE_CHANGED:
			break;
		case SESSION_DONE:
			/*SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
			int malaSize = 108; 
			Pair<Boolean, Long> parsed = Util.tryParse(preferences.getString(getString(R.string.prefMalaSize), "108"));
			if (parsed._1){
				malaSize = parsed._2.intValue();
			}*/
			
			int addCount = data.getExtras().getInt(ExtraKeys.MalaCount);
			if (addCount != 0) {
				db.insertSession((int) id, addCount);
			}
		}

		updateView();
	}

}
