package com.meditationtracker;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.lang.Thread.UncaughtExceptionHandler;

import android.R.drawable;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;

import com.meditationtracker.R.string;
import com.meditationtracker.util.Util;

import static com.meditationtracker.R.string.appUrl;

public class BaseActivity extends Activity
{
	private static UncaughtExceptionHandler defaultUncaughtExceptionHandler;
	private static ActivityHistory activityHistory;
	
	private PracticeDatabase db;
	
	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		if (defaultUncaughtExceptionHandler == null) {
			defaultUncaughtExceptionHandler = Thread.getDefaultUncaughtExceptionHandler();
			Thread.setDefaultUncaughtExceptionHandler(loggingExceptionHandler);
			
			activityHistory = new ActivityHistory();
		}
		
		activityHistory.logOnCreate(savedInstanceState, getIntent());
		
		super.onCreate(savedInstanceState);
	}
	
	@Override
	protected void onStop() {
		onDestroy();
		
		if (db != null) {
			db.release();
			db = null;
		}
	}

	protected PracticeDatabase db() {
		if (db == null || !db.isOpen()){
			
			db = new PracticeDatabase(this);
		}

		// still?
		if (db == null || !db.isOpen()){
            Builder builder = new Builder(this);
            builder
                    .setTitle(string.error_db_title)
                    .setMessage(string.error_db_text)
                    .setIcon(drawable.ic_dialog_alert)
                    .setPositiveButton(appUrl, new OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(getString(appUrl))));
                        }
                    }).show();

		}

		return db;
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data)
	{
		activityHistory.logOnActivityResult(requestCode, resultCode, data);
		super.onActivityResult(requestCode, resultCode, data);
	}

	private final UncaughtExceptionHandler loggingExceptionHandler = new UncaughtExceptionHandler()
	{
		@Override
        public void uncaughtException(Thread thread, Throwable ex)
		{
			try {
				Writer result = new StringWriter();
		        PrintWriter printWriter = new PrintWriter(result);
		        ex.printStackTrace(printWriter);
		        String stacktrace = result.toString();
		        printWriter.close();

                Util.writeError(ex + "\n\n" + stacktrace + "\n\n" + activityHistory.getLog());

			} catch(Exception e) {
				
			}
			
			defaultUncaughtExceptionHandler.uncaughtException(thread, ex);
		}
	};
}
