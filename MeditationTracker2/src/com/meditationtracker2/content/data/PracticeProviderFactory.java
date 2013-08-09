package com.meditationtracker2.content.data;

import android.content.Context;

import com.meditationtracker2.content.data.ormlite.OrmLitePracticeProvider;

public class PracticeProviderFactory {
	public static IPracticeProvider getMeAProvider(Context where) {
		return new OrmLitePracticeProvider(where);
//		return new MockContentProvider(where);
	}
}
