package doo.android;

import android.app.KeyguardManager;
import android.app.KeyguardManager.KeyguardLock;
import android.content.Context;
import android.os.PowerManager;
import android.os.PowerManager.WakeLock;

public class Locker {

	public static WakeLock wakeThePhone(Context ctx)
	{
		return wakeThePhone(ctx, ctx.getClass().getName());
	}
	
	public static WakeLock wakeThePhone(Context ctx, String tag)
	{
		return getWakeLock(ctx, tag, PowerManager.FULL_WAKE_LOCK | PowerManager.ACQUIRE_CAUSES_WAKEUP);
	}
	
	public static WakeLock getWakeLock(Context ctx, String tag, int flags)
	{
		final PowerManager powerManager = (PowerManager) ctx.getSystemService(Context.POWER_SERVICE);
		WakeLock wakeLock = powerManager.newWakeLock(flags, tag);
		wakeLock.acquire();
		
		return wakeLock;
	}

	public static KeyguardLock disableKeyGuard(Context ctx)
	{
		return disableKeyGuard(ctx, ctx.getClass().getName());
	}
	
	public static KeyguardLock disableKeyGuard(Context ctx, String tag)
	{
		KeyguardManager keyguardManager = (KeyguardManager) ctx.getSystemService(Context.KEYGUARD_SERVICE);
		KeyguardLock keyLock = keyguardManager.newKeyguardLock(tag);
		keyLock.disableKeyguard();
		
		return keyLock;
	}

}
