package com.meditationtracker2.preferences;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

public class Settinger {
	private final Context where;

	public Settinger(Context where) {
		this.where = where;
	}

	public void putBoolean(int keyResId, boolean value) {
		getPrefManager()
			.edit()
			.putBoolean(where.getString(keyResId), value)
			.commit();
	}

	public boolean getBoolean(int keyResId, boolean defValue) {
		return getPrefManager()
				.getBoolean(where.getString(keyResId), defValue);
	}

	public String getString(int keyResId, String defValue) {
		return getPrefManager()
				.getString(where.getString(keyResId), defValue);
	}

	public void putString(int keyResId, String value) {
		getPrefManager()
		.edit()
		.putString(where.getString(keyResId), value)
		.commit();
	}
	
	protected SharedPreferences getPrefManager() {
		return PreferenceManager
				.getDefaultSharedPreferences(where);
	}
}
