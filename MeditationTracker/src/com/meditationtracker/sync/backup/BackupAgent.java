package com.meditationtracker.sync.backup;

import java.io.IOException;

import android.app.backup.BackupDataInput;
import android.app.backup.BackupHelper;
import android.app.backup.SharedPreferencesBackupHelper;
import android.os.ParcelFileDescriptor;

import com.meditationtracker.PracticeDatabase;

public class BackupAgent extends android.app.backup.BackupAgentHelper {

	private BackupHelper prefsHelper;
	private AllFilesBackupHelper dbData;

	@Override
	public void onCreate() {
		super.onCreate();

		prefsHelper = new SharedPreferencesBackupHelper(this);
		addHelper("settings", prefsHelper);

		dbData = new AllFilesBackupHelper(this);
		addHelper(PracticeDatabase.DBNAME, dbData);
	}

	@Override
	public void onRestore(BackupDataInput data, int appVersionCode, ParcelFileDescriptor newState)
			throws IOException {
		super.onRestore(data, appVersionCode, newState);
	}
	
	
}
