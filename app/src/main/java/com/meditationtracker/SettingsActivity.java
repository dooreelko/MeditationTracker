package com.meditationtracker;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.preference.PreferenceActivity;

import com.meditationtracker.R.xml;

public class SettingsActivity extends PreferenceActivity
{

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);
	
	    addPreferencesFromResource(xml.preferences);
	}

}
