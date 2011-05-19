package com.meditationtracker.preferences;

import android.content.Context;
import android.preference.EditTextPreference;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;

public abstract class ChattyEditTextPreference extends EditTextPreference {

	public ChattyEditTextPreference(Context context) {
		this(context, null);
	}

	public ChattyEditTextPreference(Context context, AttributeSet attrs) {
		super(context, attrs);
	}
	
	/*@Override
	protected void onBindView(View view) {
		super.onBindView(view);
		
		updateChattySummary();
	}*/
	
	@Override
	protected View onCreateView(ViewGroup parent)
	{
		updateChattySummary();
		return super.onCreateView(parent);
	}

	@Override
	protected void onDialogClosed(boolean positiveResult) {
		super.onDialogClosed(positiveResult);
		updateChattySummary();
	}

	protected void updateChattySummary() {
		setSummary(getChattySummary());
	}

	protected abstract CharSequence getChattySummary();
}
