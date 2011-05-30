package com.meditationtracker;

import doo.util.Pair;
import doo.util.Util;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

public class NewOrEditPracticeDBActivity extends DBActivity
{
	private boolean isEdit;

	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);

		Intent screenIntent = new Intent(this, NewOrEditPracticeScreenActivity.class).putExtra(ExtraKeys.ID, id);
		if (isEdit = (id != -1)){
			screenIntent.putExtra(ExtraKeys.Title, practice.getValues().getAsString(PracticeDatabase.KEY_TITLE));
			screenIntent.putExtra(ExtraKeys.ImgURL, practice.getValues().getAsString(PracticeDatabase.KEY_ICONURL));
			screenIntent.putExtra(ExtraKeys.ThumbURL, practice.getValues().getAsString(PracticeDatabase.KEY_THUMBURL));
			screenIntent.putExtra(ExtraKeys.TotalCount, practice.getValues().getAsInteger(PracticeDatabase.KEY_TOTALCOUNT));
			screenIntent.putExtra(ExtraKeys.CurrentCount, practice.getValues().getAsInteger(PracticeDatabase.KEY_DONE));
			screenIntent.putExtra(ExtraKeys.MalaSize, practice.getValues().getAsInteger(PracticeDatabase.KEY_MALASIZE));
		}
		
		startActivityForResult(screenIntent, DONE_EDITING);
	}
	
	@Override
	protected void onStart() {
		super.onStart();
	}	


	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data)
	{
		super.onActivityResult(requestCode, resultCode, data);
		
		if (resultCode != Activity.RESULT_OK){
			setResult(Activity.RESULT_CANCELED);
		}
		else {
			Bundle extras = data.getExtras();
			
			String title = extras.getString(ExtraKeys.Title);
			String imgUrl = extras.getString(ExtraKeys.ImgURL);
			String thumbUrl = extras.getString(ExtraKeys.ThumbURL);
			
			Pair<Boolean, Long> parsed = Util.tryParse(extras.getString(ExtraKeys.TotalCount));
			int totalCount = parsed._1 ? parsed._2.intValue() : 0;
			
			parsed = Util.tryParse(extras.getString(ExtraKeys.CurrentCount));
			int currentCount = parsed._1 ? parsed._2.intValue() : 0;
			
			parsed = Util.tryParse(extras.getString(ExtraKeys.MalaSize));
			int malaSize = parsed._1 ? parsed._2.intValue() : 0;

			if (isEdit)
				db().updatePractice(new PracticeEntry((int)id, title, imgUrl, thumbUrl, totalCount, currentCount, malaSize));
			else
				db().insertPractice(new PracticeEntry(title, imgUrl, thumbUrl, totalCount, currentCount, malaSize));
			
			setResult(RESULT_OK);
		}

		finish();
	}
	
	
}
