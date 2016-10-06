package doo.alarm;

import java.util.Calendar;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class AlarmOperator
{
	private static final int REQUEST_CODE = 0;
	private final Context context;

	public AlarmOperator(Context ctx)
	{
		context = ctx;
	}
	
	public void setAlarm(Class<? extends BroadcastReceiver> alarmClass, long millisecondsAfterNow, String alarmMessage)
	{
		setAlarm(alarmClass, millisecondsAfterNow, 24 * 60 * 60 * 1000, alarmMessage);
	}
	
	public void setAlarm(Class<? extends BroadcastReceiver> alarmClass, long millisecondsAfterNow, long millisecondsInterval, String alarmMessage)
	{
		PendingIntent pendingIntent = generateIntent(alarmClass, alarmMessage);

		AlarmManager manager = getNotificationManager();
		
		manager.setRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis() + millisecondsAfterNow, millisecondsInterval, pendingIntent);
	}

	private PendingIntent generateIntent(Class<? extends BroadcastReceiver> alarmClass, String alarmMessage)
	{
		Intent intent = new Intent(context, alarmClass);
		intent.setAction(alarmMessage);
		
		PendingIntent pendingIntent = PendingIntent.getBroadcast(context, REQUEST_CODE, intent, 0);
		return pendingIntent;
	}

	public void removeAlarm(Class<? extends BroadcastReceiver> alarmClass, String alarmMessage)
	{
		PendingIntent pendingIntent = generateIntent(alarmClass, alarmMessage);

		AlarmManager manager = getNotificationManager();
		manager.cancel(pendingIntent);
	}
	
	/**
	 * @param futureTime, only time as HH:MM:00 will be taken in account
	 * @param pastTime, start point for calculation, normally "now"
	 * @return milliseconds between pastTime and nearest future at futureTime
	 */
	public static long getMillisecondsAfter(Calendar pastTime, Calendar futureTime)
	{
		Calendar future = Calendar.getInstance();
		future.set(pastTime.get(Calendar.YEAR), pastTime.get(Calendar.MONTH), pastTime.get(Calendar.DAY_OF_MONTH), futureTime.get(Calendar.HOUR_OF_DAY), futureTime.get(Calendar.MINUTE), 0);
		future.set(Calendar.MILLISECOND, pastTime.get(Calendar.MILLISECOND));
		
		long result = future.getTimeInMillis() - pastTime.getTimeInMillis();
		
		return result <= 0 ? result + (24 * 60 * 60 * 1000) : result;
	}

	private AlarmManager getNotificationManager()
	{
		return (AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
	}
}
