package com.meditationtracker.sync.backup;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.meditationtracker.PracticeDatabase;

import android.app.backup.BackupHelper;
import android.app.backup.FileBackupHelper;
import android.app.backup.SharedPreferencesBackupHelper;

public class BackupAgent extends android.app.backup.BackupAgentHelper {

	private BackupHelper prefsHelper;
	//private BackupHelper meditationHelper;

	@Override
	public void onCreate() {
		super.onCreate();
		
		prefsHelper = new SharedPreferencesBackupHelper(this);
		addHelper("settings", prefsHelper);
		
/*		meditationHelper = new MeditationBackupHelper();
		addHelper("meditations", meditationHelper);*/
		

		List<String> files = new ArrayList<String>();
		files.add("../databases/" + PracticeDatabase.DBNAME);
		
		File filesDir = getFilesDir();
		for (File f : filesDir.listFiles()) {
			files.add(f.getAbsolutePath());
		}

		FileBackupHelper dbData = new FileBackupHelper(this,
			    (String[])files.toArray());
		addHelper(PracticeDatabase.DBNAME, dbData);
	}
}
