package com.meditationtracker2;

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface.OnClickListener;

import com.actionbarsherlock.app.SherlockActivity;
import com.meditationtracker2.content.Practice;
import com.meditationtracker2.content.PracticeProviderFactory;

public class PracticeActivity extends SherlockActivity {
	private Practice practice;
	
	protected Practice getPractice() {
		return practice != null ? practice : (practice = PracticeProviderFactory
				.getMeAProvider(this).getPractice(getPracticeIdFromIntent()));
	}

	protected int getPracticeIdFromIntent() {
		return getIntent().getIntExtra(Constants.PRACTICE_ID, -1);
	}

	protected int getPracticeId() {
		return getPractice().id;
	}
	
	protected void doTheYesNoDialog(int titleResId, int messageResId,
			OnClickListener yesListener, OnClickListener noListener) {
		AlertDialog.Builder builder = new Builder(this);
		builder.setTitle(titleResId).setMessage(messageResId)
				.setPositiveButton(android.R.string.yes, yesListener)
				.setNegativeButton(android.R.string.no, noListener)
				.setIcon(android.R.drawable.ic_dialog_alert).show();
	}
}
