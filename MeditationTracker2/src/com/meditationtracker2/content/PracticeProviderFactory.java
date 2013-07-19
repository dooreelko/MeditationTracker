package com.meditationtracker2.content;

import android.content.Context;

public class PracticeProviderFactory {
	public static IPracticeProvider getMeAProvider(Context where) {
		return new MockContent(where);
	}
}
