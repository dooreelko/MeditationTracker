package com.meditationtracker;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
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

public class MainActivity extends VerboseActivity {
	protected static final int SETTINGS_DONE = 0;
	protected static final int NEW_OR_EDIT_PRACTICE_DONE = 1;
	protected static final int PRACTICE_DONE = 2;
	private PracticeDatabase db;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		PreferenceManager.setDefaultValues(this, R.xml.preferences, false);

		setContentView(R.layout.main);

		db = new PracticeDatabase(this);
		ensureNgondroDefaultsOnFirstRun();

		UpdateUI();

		try {
			PackageManager pm = this.getPackageManager();
			PackageInfo packageInfo = pm.getPackageInfo(this.getPackageName(), 0);

			String currentVersion = packageInfo.versionCode + " \"" + packageInfo.versionName + "\"";

			SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
			String lastVersion = preferences.getString(getString(R.string.prefVersion), "");
			
			
			if (lastVersion.compareTo(currentVersion) != 0) {
				Editor editor = preferences.edit();
				editor.putString(getString(R.string.prefVersion), currentVersion);
				editor.commit();

				showDialog(R.layout.post_install);
			}

		} catch (Exception ignoreme) {
		}
	}
	
	@Override
	protected Dialog onCreateDialog(int id) {
		View view = View.inflate(this, id, null);
		Dialog dlg = new Dialog(this);
		dlg.setContentView(view);
		dlg.setTitle(R.string.hi);
		
		return dlg;
	}



	private void UpdateUI() {
		SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);

		ListView lv = (ListView) findViewById(R.id.ngondroList);

		int ngondroVisible = preferences.getBoolean(getString(R.string.prefShowNgondro), true) ? View.VISIBLE
				: View.GONE;

		lv.setVisibility(ngondroVisible);
		findViewById(R.id.ngondroTitle).setVisibility(ngondroVisible);
		findViewById(R.id.customPracticesTitle).setVisibility(ngondroVisible);

		SimpleCursorAdapter viewAdapter;
		if (ngondroVisible != View.GONE) {
			viewAdapter = new SimpleCursorAdapter(this, R.layout.practice_list_item,
					db.getPracticesStatuses(true), new String[] { PracticeDatabase.KEY_THUMBURL,
							PracticeDatabase.KEY_TITLE, PracticeDatabase.KEY_SCHEDULEDCOUNT,
							PracticeDatabase.KEY_DONE }, new int[] { R.id.practiceImg, R.id.practiceTitle,
							R.id.scheduledText, R.id.completedText });
			viewAdapter.setViewBinder(new SmartViewBinder());

			lv.setAdapter(viewAdapter);
			registerForContextMenu(lv);
			lv.setOnItemClickListener(practiceClick);
		}

		lv = (ListView) findViewById(R.id.customList);

		viewAdapter = new SimpleCursorAdapter(this, R.layout.practice_list_item,
				db.getPracticesStatuses(false), new String[] { PracticeDatabase.KEY_THUMBURL,
						PracticeDatabase.KEY_TITLE, PracticeDatabase.KEY_SCHEDULEDCOUNT,
						PracticeDatabase.KEY_DONE }, new int[] { R.id.practiceImg, R.id.practiceTitle,
						R.id.scheduledText, R.id.completedText });
		viewAdapter.setViewBinder(new SmartViewBinder());

		lv.setAdapter(viewAdapter);
		registerForContextMenu(lv);
		lv.setOnItemClickListener(practiceClick);

		// db.dumpNgondroStatus();
	}

	private void ensureNgondroDefaultsOnFirstRun() {
		SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);

		if (preferences.getBoolean(getString(R.string.prefFirstRun), true) && !db.hasNgondroEntries(this)) {
			db.insertPractice(true, 0, getResources().getString(R.string.refuge), R.drawable.refuge,
					R.drawable.icon_refuge, 111111);
			db.insertPractice(true, 1, getResources().getString(R.string.diamondMind),
					R.drawable.diamond_mind_big, R.drawable.icon_diamond_mind, 111111);
			db.insertPractice(true, 2, getResources().getString(R.string.mandalaOffering),
					R.drawable.mandala_offering_big, R.drawable.icon_mandala_offering, 111111);
			db.insertPractice(true, 3, getResources().getString(R.string.guruYoga), R.drawable.guru_yoga_big,
					R.drawable.icon_guru_yoga, 111111);

			SharedPreferences.Editor editor = preferences.edit();
			editor.putBoolean(getString(R.string.prefFirstRun), false);
			editor.commit();
		}
	}

	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.main_menu, menu);
		return true;
	}

	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.settingsMenuItem:
			startActivityForResult(new Intent(this, SettingsActivity.class), SETTINGS_DONE);

			return true;
		case R.id.addPracticeMenuItem:
			editPractice(-1);
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
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

		UpdateUI();
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
		db.deletePractice(id);
	}

	private OnItemClickListener practiceClick = new OnItemClickListener() {

		public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
			Log.d("MTRK", "selected: " + id);
			openPractice(id);
		}
	};

	@Override
	public boolean onContextItemSelected(MenuItem item) {
		final AdapterContextMenuInfo menuInfo = (AdapterContextMenuInfo) item.getMenuInfo();

		switch (item.getItemId()) {
		case R.id.openPractice:
			openPractice(menuInfo.id);
			break;
		case R.id.editPractice:
			// if (!isNgondroEditAction(menuInfo))
			editPractice(menuInfo.id);
			break;
		case R.id.deletePractice:
			if (!isNgondroEditAction(menuInfo)) {
				AlertDialog.Builder builder = new AlertDialog.Builder(this);
				builder.setMessage(R.string.confirmDeletionMsg).setTitle(R.string.confirmDeletionTitle)
						.setIcon(android.R.drawable.ic_dialog_alert)
						.setPositiveButton(android.R.string.yes, new OnClickListener() {
							public void onClick(DialogInterface dialog, int which) {
								deletePractice(menuInfo.id);
								UpdateUI();
							}
						}).setNegativeButton(android.R.string.no, null).show();

			}
			break;
		}
		return super.onContextItemSelected(item);
	}

	private boolean isNgondroEditAction(AdapterContextMenuInfo info) {
		if (isChildOf(info.targetView, R.id.ngondroList)) {
			ShowNoEditNgondroMessage();
			return true;
		} else
			return false;

	}

	private void ShowNoEditNgondroMessage() {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setMessage(R.string.msgNoEditNgondro).setTitle(R.string.info)
				.setIcon(android.R.drawable.ic_dialog_info).setPositiveButton(android.R.string.ok, null)
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