package com.meditationtracker.preferences;

import android.content.Context;
import android.os.Environment;
import android.preference.Preference;
import android.widget.Toast;

import com.meditationtracker.PracticeDatabase;
import com.meditationtracker.util.Util;

import java.io.File;
import java.io.IOException;
import java.util.Date;

/**
 * Created by sasha on 04.10.16.
 */

public class ExportActor implements ActionPreference.IActor {
    @Override
    public void act(Preference preference, String param) {
        Util.exportDatabase(preference.getContext());
    }
}
