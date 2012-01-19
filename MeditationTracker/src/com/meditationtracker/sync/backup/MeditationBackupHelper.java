package com.meditationtracker.sync.backup;

import android.app.backup.BackupAgentHelper;
import android.app.backup.BackupDataInputStream;
import android.app.backup.BackupDataOutput;
import android.app.backup.BackupHelper;
import android.os.ParcelFileDescriptor;

public class MeditationBackupHelper extends BackupAgentHelper implements BackupHelper {

	public MeditationBackupHelper() {
	}

	@Override
	public void performBackup(ParcelFileDescriptor arg0, BackupDataOutput arg1, ParcelFileDescriptor arg2) {
		// TODO Auto-generated method stub

	}

	@Override
	public void restoreEntity(BackupDataInputStream arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void writeNewStateDescription(ParcelFileDescriptor arg0) {
		// TODO Auto-generated method stub

	}

}
