package doo.wifi;

import java.util.List;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.util.Log;

public class WifiScanner
{
	public interface OnScanResults
	{
		public void onResults(List<ScanResult> results);
	}

	private WifiManager wifiMan;
	private OnScanResults resultReceiver;
	private boolean running;
	private boolean preScanState;

	public WifiScanner()
	{
	}

	public Boolean initiateScan(Context ctx)
	{
		if (wifiMan == null)
		{
			acquireWifiManager(ctx);
			ctx.registerReceiver(receiverWifi, new IntentFilter(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION));

			Log.d("cmw", "wifistate is: " + wifiMan.getWifiState());
			preScanState = isWifiEnabled();
			if (!preScanState) //TODO: move this to separate thread, ja?
			{
				try
				{
					int cnt = 0;
					while (++cnt < 5 && !(running = isWifiEnabled()))
					{
						wifiMan.setWifiEnabled(true);
						Log.d("cmw", "wifistate is: " + wifiMan.getWifiState());
						Log.d("cmw", "waiting for wifi ... " + cnt);
						Thread.sleep(400); //TODO: make sure 4*400ms is enough for slow devices.
					}
				} catch (InterruptedException e)
				{
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}

		return running = wifiMan.startScan();
	}

	private void acquireWifiManager(Context ctx) {
		wifiMan = (WifiManager) ctx.getSystemService(Context.WIFI_SERVICE);
	}

	// wifi manager's isWifiEnabled is not trustworthy, doing its job
	private boolean isWifiEnabled() {
		return wifiMan.getWifiState() >= WifiManager.WIFI_STATE_ENABLED;
	};

	public void setResultReceiver(OnScanResults receiver)
	{
		resultReceiver = receiver;
	}

	public OnScanResults getResultReceiver()
	{
		return resultReceiver;
	}

	public void releaseScanner(Context ctx)
	{
		if (wifiMan != null)
		{
			wifiMan.setWifiEnabled(preScanState);
			// ctx.unregisterReceiver(receiverWifi);

			running = false;
			// keep the instance so there would no be concurrent later
			// "oh i've got a message and the receiver even though unregistered is gone"
			wifiMan = null;
			try
			{
				ctx.unregisterReceiver(receiverWifi);
			} catch (IllegalArgumentException e)
			{

				// TODO: fook it, we don't care if it's not registered, even
				// though it's a bug since we deregister it twice...
			}
		}

	}

	public boolean isRunning()
	{
		return running;
	}

	private BroadcastReceiver receiverWifi = new BroadcastReceiver()
	{

		@Override
		public void onReceive(Context context, Intent intent)
		{
			if (running && resultReceiver != null)
			{
				resultReceiver.onResults(wifiMan.getScanResults());
			}
		}
	};
}
