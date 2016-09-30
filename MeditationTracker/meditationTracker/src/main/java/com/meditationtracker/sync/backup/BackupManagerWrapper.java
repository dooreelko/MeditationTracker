package com.meditationtracker.sync.backup;

import android.app.backup.RestoreObserver;
import android.content.Context;
import android.util.Log;

public class BackupManagerWrapper {
	private IBackupManager instance;
	
	public BackupManagerWrapper(Context context) {
		try {
			instance = new RealBackupManager(context);
			Log.d("MTRK", "Can do backup ops");
		}catch (VerifyError e) {
			Log.d("MTRK", "Will skip backup ops");
			instance = new DummyBackupManager(context);
		}
	}

	public void dataChanged() {
		instance.dataChanged();
	}

	public int requestRestore(RestoreObserver observer) {
		return instance.requestRestore(observer);
	}
}
