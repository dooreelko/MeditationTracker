package com.meditationtracker;

import android.Manifest;
import android.R.drawable;
import android.R.string;
import android.app.AlertDialog.Builder;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewParent;
import android.widget.AdapterView;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultCaller;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContract;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;

import com.meditationtracker.R.id;
import com.meditationtracker.R.layout;
import com.meditationtracker.R.xml;
import com.meditationtracker.controls.SmartViewBinder;

public class MainActivity extends BaseActivity {
    protected static final int SETTINGS_DONE = 0;
    protected static final int NEW_OR_EDIT_PRACTICE_DONE = 1;
    protected static final int PRACTICE_DONE = 2;
    private Cursor cursorNgondro;
    private Cursor cursorCustoms;

    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        PreferenceManager.setDefaultValues(this, xml.preferences, false);

        ensureNgondroDefaultsOnFirstRun();

        setContentView(layout.main);
    }

    @Override
    protected void onStart() {
        super.onStart();

        updateUI();

        try {
            PackageManager pm = getPackageManager();
            PackageInfo packageInfo = pm.getPackageInfo(getPackageName(), 0);

            String currentVersion = packageInfo.versionCode + " \"" + packageInfo.versionName + "\"";

            SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
            String lastVersion = preferences.getString(getString(R.string.prefVersion), "");

            if (lastVersion.compareTo(currentVersion) != 0) {
                ensureProperIconsPostInstall();
                updateUI();

                Editor editor = preferences.edit();
                editor.putString(getString(R.string.prefVersion), currentVersion);
                editor.commit();

                showDialog(layout.post_install);
            }

        } catch (Exception e) {
            Log.e("MTRK", "Error while migrating", e);
        }
    }

    @Override
    protected void onStop() {
        if (cursorNgondro != null)
            cursorNgondro.close();

        if (cursorCustoms != null)
            cursorCustoms.close();

        onDestroy();
    }

    @Override
    protected Dialog onCreateDialog(int id) {
        View view = View.inflate(this, id, null);
        Dialog dlg = new Dialog(this);
        dlg.setContentView(view);
        dlg.setTitle(R.string.hi);

        return dlg;
    }

    private void updateUI() {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);

        ListView lv = (ListView) findViewById(id.ngondroList);

        int ngondroVisible = preferences.getBoolean(getString(R.string.prefShowNgondro), true) ? View.VISIBLE
                : View.GONE;

        lv.setVisibility(ngondroVisible);
        findViewById(id.ngondroTitle).setVisibility(ngondroVisible);
        findViewById(id.customPracticesTitle).setVisibility(ngondroVisible);

        findViewById(id.menu_fab).setOnClickListener(showMenu);

        SimpleCursorAdapter viewAdapter;

        if (ngondroVisible != View.GONE) {
            cursorNgondro = db().getPracticesStatuses(true);
            viewAdapter = new SimpleCursorAdapter(this, layout.practice_list_item, cursorNgondro,
                    new String[]{PracticeDatabase.KEY_THUMBURL, PracticeDatabase.KEY_TITLE,
                            PracticeDatabase.KEY_SCHEDULEDCOUNT, PracticeDatabase.KEY_DONE}, new int[]{
                    id.practiceImg, id.practiceTitle, id.scheduledText, id.completedText});
            viewAdapter.setViewBinder(new SmartViewBinder());

            lv.setAdapter(viewAdapter);
            registerForContextMenu(lv);
            lv.setOnItemClickListener(practiceClick);
        }

        lv = (ListView) findViewById(id.customList);

        cursorCustoms = db().getPracticesStatuses(false);
        viewAdapter = new SimpleCursorAdapter(this, layout.practice_list_item, cursorCustoms, new String[]{
                PracticeDatabase.KEY_THUMBURL, PracticeDatabase.KEY_TITLE,
                PracticeDatabase.KEY_SCHEDULEDCOUNT, PracticeDatabase.KEY_DONE}, new int[]{
                id.practiceImg, id.practiceTitle, id.scheduledText, id.completedText});
        viewAdapter.setViewBinder(new SmartViewBinder());

        lv.setAdapter(viewAdapter);
        registerForContextMenu(lv);
        lv.setOnItemClickListener(practiceClick);

        // db.dumpNgondroStatus();
    }

    private void ensureNgondroDefaultsOnFirstRun() {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);

        PracticeDatabase db = db();
        if (preferences.getBoolean(getString(R.string.prefFirstRun), true) && !db.hasNgondroEntries(this)) {
            db.insertPractice(true, 0, getResources().getString(R.string.refuge), R.drawable.refuge,
                    R.drawable.icon_refuge, 111111);
            db.insertPractice(true, 1, getResources().getString(R.string.diamondMind),
                    R.drawable.diamond_mind_big, R.drawable.icon_diamond_mind, 111111);
            db.insertPractice(true, 2, getResources().getString(R.string.mandalaOffering),
                    R.drawable.mandala_offering_big, R.drawable.icon_mandala_offering, 111111);
            db.insertPractice(true, 3, getResources().getString(R.string.guruYoga), R.drawable.guru_yoga_big,
                    R.drawable.icon_guru_yoga, 111111);

            Editor editor = preferences.edit();
            editor.putBoolean(getString(R.string.prefFirstRun), false);
            editor.commit();
        }
    }

    private final View.OnClickListener showMenu = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            invalidateOptionsMenu();
            openOptionsMenu();
        }
    };

    private void ensureProperIconsPostInstall() {
        db().patchIcons();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        closeOptionsMenu();
        final int iid = item.getItemId();
        if (iid == id.settingsMenuItem) {
            startActivityForResult(new Intent(this, SettingsActivity.class), SETTINGS_DONE);

            return true;
        } else 	if (iid == id.addPracticeMenuItem) {
            editPractice(-1);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case SETTINGS_DONE:
                break;
            case NEW_OR_EDIT_PRACTICE_DONE:
                if (resultCode == RESULT_OK) {

                }
                break;
            case PRACTICE_DONE:
                break;
            default:
                break;
        }

        updateUI();
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.practice_context_menu, menu);
        menu.setHeaderTitle(R.string.chooseAction);
    }

    private void openPractice(long id) {
        startActivityForResult(new Intent(this, PracticeActivity.class).putExtra("id", id), PRACTICE_DONE);
    }

    private void editPractice(long id) {
        startActivityForResult(new Intent(this, NewOrEditPracticeDBActivity.class).putExtra("id", id),
                NEW_OR_EDIT_PRACTICE_DONE);
    }

    private void deletePractice(long id) {
        db().deletePractice(id);
    }

    private final OnItemClickListener practiceClick = new OnItemClickListener() {

        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            Log.d("MTRK", "selected: " + id);
            openPractice(id);
        }
    };

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        final AdapterContextMenuInfo menuInfo = (AdapterContextMenuInfo) item.getMenuInfo();

        final int iid = item.getItemId();
        if (iid == id.openPractice) {
            openPractice(menuInfo.id);
        }
        else if (iid == id.editPractice) {
            // if (!isNgondroEditAction(menuInfo))
            editPractice(menuInfo.id);
        }
        else if (iid ==  id.deletePractice) {
                if (!isNgondroEditAction(menuInfo)) {
                    Builder builder = new Builder(this);
                    builder.setMessage(R.string.confirmDeletionMsg).setTitle(R.string.confirmDeletionTitle)
                            .setIcon(drawable.ic_dialog_alert)
                            .setPositiveButton(string.yes, new OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    deletePractice(menuInfo.id);
                                    updateUI();
                                }
                            }).setNegativeButton(string.no, null).show();

                }
        }
        return super.onContextItemSelected(item);
    }

    private boolean isNgondroEditAction(AdapterContextMenuInfo info) {
        if (isChildOf(info.targetView, id.ngondroList)) {
            ShowNoEditNgondroMessage();
            return true;
        } else
            return false;

    }

    private void ShowNoEditNgondroMessage() {
        Builder builder = new Builder(this);
        builder.setMessage(R.string.msgNoEditNgondro).setTitle(R.string.info)
                .setIcon(drawable.ic_dialog_info).setPositiveButton(string.ok, null)
                .show();

    }

    private boolean isChildOf(View child, int parentId) {
        ViewParent cur = (ViewParent) child;
        while (cur != null) {
            if (cur instanceof View)
                if (((View) cur).getId() == parentId)
                    return true;

            cur = cur.getParent();
        }

        return false;
    }

}