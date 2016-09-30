package doo.android;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public abstract class BroadcastActivityStarter extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {
		Intent activityIntent = new Intent();
		activityIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		activityIntent.setClass(context, getActivityClass());
		activityIntent.putExtras(intent.getExtras());

		try
		{
			Integer.parseInt(intent.getAction());

			activityIntent.setAction(intent.getAction());
		} catch (NumberFormatException e)
		{
			e.printStackTrace();

			activityIntent.setAction("WEIRD|" + intent.getAction());
		}

		context.startActivity(activityIntent);
	}

	protected abstract Class<?> getActivityClass();

}
