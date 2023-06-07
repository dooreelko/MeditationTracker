package com.meditationtracker;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.preference.PreferenceActivity;

import com.meditationtracker.R.xml;
import com.meditationtracker.sync.backup.BackupManagerWrapper;

public class SettingsActivity extends PreferenceActivity
{

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);
	
	    addPreferencesFromResource(xml.preferences);
	}

	@Override
	public void onPause() {
		super.onPause();
		if (checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)
				== PackageManager.PERMISSION_DENIED) {
			String[] perms = {Manifest.permission.WRITE_EXTERNAL_STORAGE};
			this.requestPermissions(perms, 1);
		}
		new BackupManagerWrapper(this).dataChanged();
	}
}
