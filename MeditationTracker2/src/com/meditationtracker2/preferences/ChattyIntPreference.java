package com.meditationtracker2.preferences;

import android.content.Context;
import android.text.InputType;
import android.util.AttributeSet;

public class ChattyIntPreference extends ChattyEditTextPreference {
	public ChattyIntPreference(Context context) {
		this(context, null);
	}

	public ChattyIntPreference(Context context, AttributeSet attrs) {
		super(context, attrs);
		
		getEditText().setInputType(InputType.TYPE_CLASS_NUMBER);
	}
}
