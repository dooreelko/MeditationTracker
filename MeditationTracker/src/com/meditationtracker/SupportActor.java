package com.meditationtracker;

import android.content.Intent;
import android.net.Uri;
import android.preference.Preference;

import com.meditationtracker.preferences.ActionPreference;

public class SupportActor implements ActionPreference.IActor {

	@Override
	public void act(Preference preference, String param) {
		preference.getContext().startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(param)));
	}

}
