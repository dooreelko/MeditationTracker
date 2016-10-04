package com.meditationtracker.sync.backup;

import android.app.backup.BackupManager;
import android.app.backup.RestoreObserver;
import android.content.Context;
import android.util.Log;

import com.meditationtracker.util.Util;

public class RealBackupManager implements IBackupManager {
	private BackupManager instance;
    private Context context;

    public RealBackupManager(Context context) {

		instance = new BackupManager(context);
        this.context = context;
    }
	
	@Override
	public void dataChanged() {
		instance.dataChanged();
		Log.d("MTRK", "Asked backup ops");
	}

	@Override
	public int requestRestore(RestoreObserver observer) {
		return instance.requestRestore(observer);
	}

}
