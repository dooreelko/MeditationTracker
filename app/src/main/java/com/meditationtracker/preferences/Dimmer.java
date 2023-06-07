package com.meditationtracker.preferences;

import android.app.Activity;
import android.content.Context;
import android.view.Window;
import android.view.WindowManager;
import android.view.WindowManager.LayoutParams;

import com.meditationtracker.preferences.SliderPreference.IScrollReactor;

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
			LayoutParams lp = window.getAttributes();
			initialBrightness = lp.screenBrightness;
		}
	}

	private void setBrightness(float newBrightness) {
		if (window != null) {
			LayoutParams lp = window.getAttributes();
			lp.screenBrightness = newBrightness;
			window.setAttributes(lp);
		}
	}
}
