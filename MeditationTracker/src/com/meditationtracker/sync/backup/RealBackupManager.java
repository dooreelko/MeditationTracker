package com.meditationtracker.sync.backup;

import android.app.backup.BackupManager;
import android.app.backup.RestoreObserver;
import android.content.Context;

public class RealBackupManager implements IBackupManager {
	private BackupManager instance;
	
	public RealBackupManager(Context context) {
		instance = new BackupManager(context);
	}
	
	@Override
	public void dataChanged() {
		instance.dataChanged();
	}

	@Override
	public int requestRestore(RestoreObserver observer) {
		return instance.requestRestore(observer);
	}

}
