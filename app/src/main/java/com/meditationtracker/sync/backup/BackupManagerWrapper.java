package com.meditationtracker.sync.backup;

import android.app.backup.RestoreObserver;
import android.content.Context;
import android.util.Log;

import com.meditationtracker.util.Util;

import java.util.concurrent.TimeUnit;

public class BackupManagerWrapper {
	private IBackupManager instance;
    private Context context;

    public BackupManagerWrapper(Context context) {
        this.context = context;
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

		if (System.currentTimeMillis() - Util.getLastBackupDate() > TimeUnit.DAYS.toMillis(7)) {
            Util.exportDatabase(context);
        }
	}

	public int requestRestore(RestoreObserver observer) {
		return instance.requestRestore(observer);
	}
}
