package doo.alarm;

import java.util.Calendar;

import android.R.drawable;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

public class AlarmHelper
{

	private static final Intent ACTION_ALARM_CHANGED = new Intent("android.intent.action.ALARM_CHANGED");


	public static void updateAlarmState(Context ctx, Boolean enabled,
			int alarmTime, int alarmId, Class<? extends BroadcastReceiver> alarmReceiverClass, Class<?> notificaionActionClass)
	{
		updateAlarm(ctx, enabled, alarmTime, alarmId, alarmReceiverClass);
		//updateNotification(ctx, enabled, alarmTime, alarmId, notificaionActionClass);
	}
	
	protected static void updateAlarm(Context ctx, Boolean enabled,
			int alarmTime, int alarmId, Class<? extends BroadcastReceiver> alarmReceiverClass)
	{
		AlarmOperator operator = new AlarmOperator(ctx);
		
		operator.removeAlarm(alarmReceiverClass, Integer.toString(alarmId));
	
		if (enabled)
		{
			Calendar calNow = Calendar.getInstance();
			calNow.setTimeInMillis(System.currentTimeMillis());
			Calendar futureTime = Calendar.getInstance();
	
			futureTime.set(1, 1, 1, alarmTime / 60, alarmTime % 60);
	
			long millisecondsAfter = AlarmOperator.getMillisecondsAfter(calNow,
					futureTime);
			operator.setAlarm(alarmReceiverClass, millisecondsAfter, Integer
					.toString(alarmId));
	
			int dueInHours = (int) (millisecondsAfter / (60 * 60 * 1000));
			long dueInMinutes = (millisecondsAfter / (60 * 1000)) % 60;
			Toast.makeText(
					ctx,
					"Alarm is set due in about "
							+ (dueInHours != 0 ? dueInHours + " hours and "
									: "") + dueInMinutes + " minute"
							+ (dueInMinutes != 1 ? "s" : ""),
					Toast.LENGTH_SHORT).show();
					
		}
		updateStatusBarIcon(ctx, enabled);		
	}
	
    protected static void updateStatusBarIcon(Context context, boolean enabled) {
        Intent alarmChanged = new Intent(ACTION_ALARM_CHANGED);
        alarmChanged.putExtra("alarmSet", enabled);
        context.sendBroadcast(alarmChanged);
    }
	
	
	protected static void updateNotification(Context ctx, Boolean enabled, int alarmTime, int alarmId, Class<?> notificaionActionClass)
	{
		NotificationManager nm = (NotificationManager)ctx.getSystemService(Context.NOTIFICATION_SERVICE);
		
		if (!enabled || notificaionActionClass == null)
		{
			nm.cancelAll();
		}
		else
		{
			Notification notification = new Notification(drawable.ic_lock_idle_alarm, null, System.currentTimeMillis());
			Intent dummyIntent = new Intent(ctx, notificaionActionClass); 
			PendingIntent dummyPending = PendingIntent.getActivity(ctx, 0, dummyIntent, 0/*PendingIntent.FLAG_NO_CREATE*/); 
			int hours = alarmTime / 60;
			int minutes = alarmTime % 60;
//			notification.setLatestEventInfo(ctx, "Alarm is on", "It will fire at " + hours + ":" + (minutes<10 ? "0"+minutes : minutes), dummyPending);
			
			notification.flags |= Notification.FLAG_NO_CLEAR | Notification.FLAG_ONGOING_EVENT;
			nm.notify(alarmId, notification);
		}
	}

}
