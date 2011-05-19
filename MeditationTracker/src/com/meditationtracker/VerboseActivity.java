package com.meditationtracker;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.lang.Thread.UncaughtExceptionHandler;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;

public class VerboseActivity extends Activity
{
	private static UncaughtExceptionHandler defaultUncaughtExceptionHandler;
	private static ActivityHistory activityHistory;
	
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
	protected void onActivityResult(int requestCode, int resultCode, Intent data)
	{
		activityHistory.logOnActivityResult(requestCode, resultCode, data);
		super.onActivityResult(requestCode, resultCode, data);
	}

	private UncaughtExceptionHandler loggingExceptionHandler = new UncaughtExceptionHandler()
	{
		public void uncaughtException(Thread thread, Throwable ex)
		{
			try {
				final Writer result = new StringWriter();
		        final PrintWriter printWriter = new PrintWriter(result);
		        ex.printStackTrace(printWriter);
		        String stacktrace = result.toString();
		        printWriter.close();
				
				BufferedWriter bos = new BufferedWriter(new FileWriter(Environment.getExternalStorageDirectory().getAbsolutePath() + "/meditracker.crash.log"));
	            bos.write(ex.toString() + "\n\n" + stacktrace + "\n\n" + activityHistory.getLog());
	            bos.flush();
	            bos.close();

			} catch(Exception e) {
				
			}
			
			defaultUncaughtExceptionHandler.uncaughtException(thread, ex);
		}
	};
}
