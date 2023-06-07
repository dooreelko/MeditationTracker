package com.meditationtracker.sync.backup;

import android.app.backup.RestoreObserver;

public interface IBackupManager {
	void dataChanged();
	int requestRestore(RestoreObserver observer);
}
