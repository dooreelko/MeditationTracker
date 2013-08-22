package com.meditationtracker2.preferences;

import android.app.AlertDialog.Builder;
import android.content.Context;
import android.preference.DialogPreference;
import android.util.AttributeSet;

public class ShowDialogPreference extends DialogPreference {

	public ShowDialogPreference(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	@Override
	protected void onPrepareDialogBuilder(Builder builder) {
		super.onPrepareDialogBuilder(builder);
		builder.setNegativeButton(null, null)
		.setPositiveButton(null, null)
		.setTitle(null)
		.setCancelable(true)
		.setInverseBackgroundForced(true);
	}
}
