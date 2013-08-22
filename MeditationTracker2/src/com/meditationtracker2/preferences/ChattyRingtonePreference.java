package com.meditationtracker2.preferences;

import android.content.Context;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.preference.RingtonePreference;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;

public class ChattyRingtonePreference extends RingtonePreference {
	public ChattyRingtonePreference(Context context) {
		this(context, null);
	}

	public ChattyRingtonePreference(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	@Override
	protected View onCreateView(ViewGroup parent)
	{
		updateChattySummary(onRestoreRingtone());
		return super.onCreateView(parent);
	}

	@Override
	protected void onSaveRingtone(Uri ringtoneUri)
	{
		super.onSaveRingtone(ringtoneUri);
		updateChattySummary(ringtoneUri);
		
	}

	protected void updateChattySummary(Uri ringtoneUri) {
		Ringtone ringtone = RingtoneManager.getRingtone(this.getContext(), ringtoneUri);
		if (ringtone != null)
		{
			setSummary(ringtone.getTitle(this.getContext()));
		}
	}
}
