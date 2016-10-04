package com.meditationtracker.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.nio.channels.FileChannel;
import java.util.Calendar;
import java.util.Date;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.text.format.DateFormat;
import android.util.Log;
import android.widget.Toast;

import com.meditationtracker.PracticeDatabase;
import com.meditationtracker.R;

public final class Util
{
	public static void showWhateverError(Context ctx, String message, OnClickListener okClick){
		AlertDialog.Builder builder = new AlertDialog.Builder(ctx);
		builder.setMessage(message).setTitle(android.R.string.dialog_alert_title).setIcon(
				android.R.drawable.ic_dialog_alert).setPositiveButton(android.R.string.ok, okClick).show();
		
	}
	
	public static Calendar parseSqliteDate(String date) {
		Calendar cal = Calendar.getInstance();
		
		String[] parts = date.split("-");
		if (parts.length != 3)
			return cal;
		
		//int[] partPositions = new int[] {Calendar.YEAR, Calendar.MONTH, Calendar.DAY_OF_MONTH};
		int[] ymd = new int[3];
		
		for (int x=0; x<3; x++) {
			Pair<Boolean, Long> parsed = Util.tryParse(parts[x]);
			//cal.set(partPositions[x], parsed._2.intValue());
			ymd[x] = parsed._2.intValue();
		}
		
		cal.set(ymd[0], ymd[1]-1, ymd[2]);
		
		
		return cal;
	}
	
	public static String formatCalendar(Calendar cal, Context ctx) {
		Date time = cal.getTime();
		
		return DateFormat.getDateFormat(ctx).format(time);		
	} 
	
	public static Pair<Boolean, Long> tryParse(String value) {
		Pair<Boolean, Long> result = new Pair<Boolean, Long>();
		try {
			result._2 = Long.parseLong(value);
			result._1 = true;
		} catch(Exception e) {
			result._1 = false;
		}
		
		return result;
	}

    public static void exportDatabase(Context context) {
        exportDatabase(context, false);
    }

    public static void exportDatabase(Context context, boolean silent) {
        PracticeDatabase db = new PracticeDatabase(context);

        File outDir = new File(Environment.getExternalStorageDirectory(), "MeditationTracker");
        File outFile = new File(outDir, "backup.db");
        File rollFile = new File(outDir, "backup.db."+ (new Date().getTime()));

        if (!outDir.exists()) {
            outDir.mkdir();
        } else {
            if (outFile.exists()) {
                outFile.renameTo(rollFile);
            }
        }

        try {
            db.exportDatabase(outFile);

            if (!silent) {
                Toast
                        .makeText(context, "Data exported. You can find the file at " + outFile.getAbsolutePath(), Toast.LENGTH_LONG)
                        .show();
            }
        } catch (IOException e) {
            if (!silent) {
                Toast
                        .makeText(context, "Failed data export. Get in touch with us.", Toast.LENGTH_LONG)
                        .show();
            }
        }

        db.release();

    }

    public static void importDatabase(final Context context) {
        PracticeDatabase db = new PracticeDatabase(context);

        File outDir = new File(Environment.getExternalStorageDirectory(), "MeditationTracker");
        File inFile = new File(outDir, "backup.db");

        if (!inFile.exists()) {
            return;
        }

        try {
            db.importDatabase(inFile, context);
            if (!db.hasNgondroEntries(context)) {
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder
                        .setTitle(R.string.error_db_title)
                        .setMessage(R.string.error_db_text)
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .setPositiveButton(R.string.appUrl, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                context.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(context.getString(R.string.appUrl))));
                            }
                        }).show();

            }

            Toast
                .makeText(context, "Data successfully imported.", Toast.LENGTH_LONG)
                .show();
        } catch (Exception e) {
            Toast
                .makeText(context, "Failed data import. Get in touch with us.", Toast.LENGTH_LONG)
                .show();

            writeError(e.toString());
        }
    }

    public static void writeError(String text)  {
        Log.d("MTRK", text);

        File outDir = new File(Environment.getExternalStorageDirectory(), "MeditationTracker");
        File outFile = new File(outDir, "error-" + (new Date().getTime()) + ".txt");

        if (!outDir.exists()) {
            outDir.mkdir();
        }

        FileWriter streamWriter = null;
        try {
            streamWriter = new FileWriter(outFile);
            streamWriter.write(text);
            streamWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public static class Reflection
	{

		public static void dumpDeclareds(Object on)
		{
			dumpDeclareds(on.getClass());
		}

		public static void dumpDeclareds(Class<?> onClass)
		{
			if (onClass == null)
				return;

			Log.d("MTRK", "Methods for class: " + onClass.getSimpleName());
			for (Method m : onClass.getDeclaredMethods())
			{
				Log.d("MTRK", m.getName());
			}

			dumpDeclareds(onClass.getSuperclass());
		}

		public static Object invokePrivateMethod(Class<?> declaringClass, Object on, String methodName,
				Class<?>[] paramTypes, Object... args)
		{
			try
			{
				Method m = declaringClass.getDeclaredMethod(methodName, (Class<?>[]) paramTypes);
				m.setAccessible(true);
				return m.invoke(on, args);
			} catch (SecurityException e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (NoSuchMethodException e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
				Util.Reflection.dumpDeclareds(on);
			} catch (IllegalArgumentException e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IllegalAccessException e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (InvocationTargetException e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		
			return null;
		}
	}

}
