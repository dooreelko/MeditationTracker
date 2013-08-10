package com.meditationtracker2;

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface.OnClickListener;

import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.meditationtracker2.content.data.Practice;
import com.meditationtracker2.content.data.PracticeProviderFactory;

public class PracticeActivity extends SherlockFragmentActivity {
//	private Practice practice;
	
	protected Practice getPractice() {
		return PracticeProviderFactory
				.getMeAProvider(this).getPractice(getPracticeIdFromIntent());
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
				.setPositiveButton(R.string.yes, yesListener)
				.setNegativeButton(R.string.no, noListener)
				.setIcon(android.R.drawable.ic_dialog_alert).show();
	}
}
