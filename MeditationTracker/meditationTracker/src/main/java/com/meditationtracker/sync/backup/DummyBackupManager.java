package com.meditationtracker.sync.backup;

import android.app.backup.RestoreObserver;
import android.content.Context;

public class DummyBackupManager implements IBackupManager {

	public DummyBackupManager(Context context) {	}
	
	@Override
	public void dataChanged() {	}

	@Override
	public int requestRestore(RestoreObserver observer) {	return 1;	}

}
