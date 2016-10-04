package com.meditationtracker.preferences;

import android.preference.Preference;

import com.meditationtracker.util.Util;

/**
 * Created by sasha on 04.10.16.
 */

public class ImportActor implements ActionPreference.IActor {
    @Override
    public void act(Preference preference, String param) {
        Util.importDatabase(preference.getContext());
    }
}
