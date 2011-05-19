package com.meditationtracker.preferences;

import com.meditationtracker.R;

import android.app.Dialog;
import android.content.Context;
import android.content.res.TypedArray;
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
	protected void onClick() {
		this.showDialog(null);
	}

	@Override
	protected View onCreateDialogView() {
		new View(getContext());
		return View.inflate(getContext(), layoutId, null);
	}

	
}
