package com.meditationtracker2.preferences;

import android.app.Activity;
import android.content.Context;
import android.view.Window;
import android.view.WindowManager;

import com.meditationtracker2.preferences.SliderPreference.IScrollReactor;

public class Dimmer implements IScrollReactor {
	private Window window;
	private float initialBrightness;

	public Dimmer() {
	}

	@Override
	public void onScroll(int val) {
		float newBright = val / 100f;

		setBrightness(newBright);
	}

	@Override
	public void onStopTracking() {
		setBrightness(initialBrightness);
	}

	@Override
	public void onStartTracking(Context context) {
		if (context instanceof Activity) {
			window = ((Activity) context).getWindow();
			WindowManager.LayoutParams lp = window.getAttributes();
			initialBrightness = lp.screenBrightness;
		}
	}

	private void setBrightness(float newBrightness) {
		if (window != null) {
			WindowManager.LayoutParams lp = window.getAttributes();
			lp.screenBrightness = newBrightness;
			window.setAttributes(lp);
		}
	}
}
