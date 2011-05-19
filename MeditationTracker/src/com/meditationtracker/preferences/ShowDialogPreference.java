package com.meditationtracker.preferences;

import com.meditationtracker.R;

import android.app.AlertDialog.Builder;
import android.app.Dialog;
import android.content.Context;
import android.content.res.TypedArray;
import android.os.Parcelable.Creator;
import android.preference.DialogPreference;
import android.util.AttributeSet;
import android.view.View;

public class ShowDialogPreference extends DialogPreference {

	private int layoutId;

	public ShowDialogPreference(Context context, AttributeSet attrs) {
		super(context, attrs);

		TypedArray a=getContext().obtainStyledAttributes(attrs,R.styleable.ShowDialogPreferece);
		layoutId = a.getResourceId(R.styleable.ShowDialogPreferece_layout, -1);
		
		this.setDialogTitle(getTitle());
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

	

	/*@Override
	protected void onClick() {
		this.showDialog(null);
	}

	@Override
	protected View onCreateDialogView() {
		new View(getContext());
		return View.inflate(getContext(), layoutId, null);
	}*/

	
}
