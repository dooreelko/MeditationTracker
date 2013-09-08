package com.meditationtracker2;

import android.net.Uri;

public class Constants {

	public static final String PRACTICE_ID = "PracticeId";
	public static final int PRACTICE_DONE = 0;
	public static final int PRACTICE_VIEW_DONE = 1;
	public static final int PRACTICE_EDIT_DONE = 2;
	public static final int SETTINGS_DONE = 3;
	public static final int NO_PRACTICE_ID = -1;
	public static final String IMAGE_URL = "ImageUrl";

	public static final int RESULT_DATA_CHANGED = 0;
	
	public static final String MEDIA_PROVIDER_ROOT = "content://com.meditationtracker2.images/";
	public static final String SIXTEENTH_KARMAPA_PNG = "sixteenth_karmapa.png";
	public static final String GURU_YOGA_PNG = "guruYoga.png";
	public static final String MANDALA_OFFERING_PNG = "mandalaOffering.png";
	public static final String DIAMOND_MIND_PNG = "diamondMind.png";
	public static final String REFUGE_PNG = "refuge.png";
	public static final String SOURCE_URL = "sourceUrl";
	
	public static final String SIZEHINT_SCREEN = "screen";
	public static final String SIZEHINT_KEY = "sizehint";

	
	public static Uri buildScreenUri(String path) {
		return buildScreenUri(Uri.parse(path));
	}

	public static Uri buildScreenUri(Uri path) {
		return path
				.buildUpon()
				.appendQueryParameter(SIZEHINT_KEY, SIZEHINT_SCREEN)
				.build();
	}
	
	public static Uri getCleanImageUri(Uri path) {
		return path.buildUpon().clearQuery().build();
	}
}
