package com.meditationtracker.util;

import android.R.drawable;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.text.format.DateFormat;
import android.util.Log;
import android.widget.Toast;

import com.meditationtracker.PracticeDatabase;
import com.meditationtracker.R.string;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Calendar;
import java.util.Date;

public final class Util {

    public static final String BACKUP_DB = "backup.db";

    public static void showWhateverError(Context ctx, String message, OnClickListener okClick) {
        Builder builder = new Builder(ctx);
        builder.setMessage(message).setTitle(android.R.string.dialog_alert_title).setIcon(
                drawable.ic_dialog_alert).setPositiveButton(android.R.string.ok, okClick).show();

    }

    public static Calendar parseSqliteDate(String date) {
        Calendar cal = Calendar.getInstance();

        String[] parts = date.split("-");
        if (parts.length != 3)
            return cal;

        //int[] partPositions = new int[] {Calendar.YEAR, Calendar.MONTH, Calendar.DAY_OF_MONTH};
        int[] ymd = new int[3];

        for (int x = 0; x < 3; x++) {
            Pair<Boolean, Long> parsed = tryParse(parts[x]);
            //cal.set(partPositions[x], parsed._2.intValue());
            ymd[x] = parsed._2.intValue();
        }

        cal.set(ymd[0], ymd[1] - 1, ymd[2]);


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
        } catch (Exception e) {
            result._1 = false;
        }

        return result;
    }

    public static long getLastBackupDate() {
        File backupFile = new File(getDataPath(), BACKUP_DB);
        if (!backupFile.exists()) {
            return 0;
        }

        return backupFile.lastModified();
    }

    public static void exportDatabase(Context context) {
        exportDatabase(context, false);
    }

    public static void exportDatabase(Context context, boolean silent) {
        PracticeDatabase db = new PracticeDatabase(context);

        File outDir = getDataPath();
        File outFile = new File(outDir, BACKUP_DB);
        File rollFile = new File(outDir, BACKUP_DB + "." + (new Date().getTime()));

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

    @NonNull
    private static File getDataPath() {
        return new File(Environment.getExternalStorageDirectory(), "MeditationTracker");
    }

    public static void importDatabase(final Context context) {
        PracticeDatabase db = new PracticeDatabase(context);

        File outDir = getDataPath();
        File inFile = new File(outDir, BACKUP_DB);

        if (!inFile.exists()) {
            return;
        }

        try {
            db.importDatabase(inFile, context);
            if (!db.hasNgondroEntries(context)) {
                Builder builder = new Builder(context);
                builder
                        .setTitle(string.error_db_title)
                        .setMessage(string.error_db_text)
                        .setIcon(drawable.ic_dialog_alert)
                        .setPositiveButton(string.appUrl, new OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                context.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(context.getString(string.appUrl))));
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

    public static void writeError(String text) {
        Log.d("MTRK", text);

        File outDir = getDataPath();
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

    public static class Reflection {

        public static void dumpDeclareds(Object on) {
            dumpDeclareds(on.getClass());
        }

        public static void dumpDeclareds(Class<?> onClass) {
            if (onClass == null)
                return;

            Log.d("MTRK", "Methods for class: " + onClass.getSimpleName());
            for (Method m : onClass.getDeclaredMethods()) {
                Log.d("MTRK", m.getName());
            }

            dumpDeclareds(onClass.getSuperclass());
        }

        public static Object invokePrivateMethod(Class<?> declaringClass, Object on, String methodName,
                                                 Class<?>[] paramTypes, Object... args) {
            try {
                Method m = declaringClass.getDeclaredMethod(methodName, (Class<?>[]) paramTypes);
                m.setAccessible(true);
                return m.invoke(on, args);
            } catch (SecurityException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (NoSuchMethodException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
                dumpDeclareds(on);
            } catch (IllegalArgumentException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (InvocationTargetException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

            return null;
        }
    }

}
