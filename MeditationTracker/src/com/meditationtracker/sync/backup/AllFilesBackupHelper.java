package com.meditationtracker.sync.backup;

import java.io.File;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import com.meditationtracker.PracticeDatabase;

import android.app.backup.BackupDataInputStream;
import android.app.backup.FileBackupHelper;
import android.content.Context;
import android.util.Log;


public class AllFilesBackupHelper extends FileBackupHelper {

	private static File mFilesDir;

	public AllFilesBackupHelper(Context context) {
		super(context, getFiles(context));
		// TODO Auto-generated constructor stub
	}

	private static String[] getFiles(Context context) {
		List<String> files = new ArrayList<String>();
		files.add("../databases/" + PracticeDatabase.DBNAME);

		Log.d("MTRK", "File backup");
		mFilesDir = context.getFilesDir();
		for (File f : mFilesDir.listFiles()) {
			String fname = f.getName();
			files.add(fname);
			Log.d("MTRK", "Adding " + fname + " to backup");
		}

		return files.toArray(new String[0]);
	}

	//XXX: WTF: Actually overloading the isKeyInList should've been enough if it wasn't package-private
	@Override
	public void restoreEntity(BackupDataInputStream data) {
       	Log.d("MTRK", "got entity '" + data.getKey() + "' size=" + data.size());
        String key = data.getKey();
        File f = new File(mFilesDir, key);
        
//      writeFile(f, data);
        try {
			Method declaredMethod = FileBackupHelper.class.getSuperclass().getDeclaredMethod("writeFile", new Class[] { File.class, BackupDataInputStream.class});
			declaredMethod.setAccessible(true);
			declaredMethod.invoke(this, f, data);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			Log.e("MTRK", "Failed restoring file", e);
		} 
        
	}
	
}
