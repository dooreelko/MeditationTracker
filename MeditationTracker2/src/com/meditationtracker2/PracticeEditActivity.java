package com.meditationtracker2;

import java.io.File;

import android.annotation.TargetApi;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.View;
import android.widget.ImageButton;
import butterknife.InjectView;
import butterknife.OnClick;
import butterknife.Views;

import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.dmitriy.tarasov.android.intents.IntentUtils;
import com.meditationtracker2.PictureSourceDialog.IChoosePicture;
import com.meditationtracker2.content.PracticeViewUpdater;
import com.meditationtracker2.content.data.Practice;
import com.meditationtracker2.content.data.PracticeProviderFactory;
import com.meditationtracker2.model.PracticeEditModel;

import doo.bandera.ModelBinder;

public class PracticeEditActivity extends PracticeActivity implements PictureSourceDialog.IChoosePicture {
	private static final int IMAGE_CHOOSEN = 0;
	private static final int PICTURE_TAKEN = 1;

	@InjectView(R.id.buttonPracticeImage)
	ImageButton buttonPracticeImage;

	private Practice practice = new Practice();
	private PracticeEditModel model;
	private ModelBinder binder;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_practice_edit);
		Views.inject(this);

		if (getPracticeIdFromIntent() != Constants.NO_PRACTICE_ID) {
			practice = getPractice();
			getSupportActionBar().setTitle(practice.title);
		} else {
			getSupportActionBar().setTitle(R.string.new_practice);
		}

		getSupportActionBar().setDisplayHomeAsUpEnabled(true);

		model = new PracticeEditModel(practice);
		binder = doo.bandera.Models.Bind(this, model, new PracticeViewUpdater());
	}

	@OnClick(R.id.buttonPracticeImage)
	void onClickChangePicture(View v) {
		pickPicture();
	}

	@Override
	public void onBackPressed() {
		askIfToSaveAndMaybeDo();
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {

		switch (item.getItemId()) {
		case (android.R.id.home):
			askIfToSaveAndMaybeDo();
			break;
		case (R.id.menu_discard):
			finish();
			break;
		case (R.id.menu_picture):
			pickPicture();
			break;
		case (R.id.menu_accept):
			saveAndClose();
		}

		return true;
	}

	private void saveAndClose() {
		if (binder.isDirty()) {
			model.updatePractice(practice);

			PracticeProviderFactory.getMeAProvider(this).savePractice(practice);
		}

		setResult(Constants.RESULT_DATA_CHANGED);
		finish();
	}

	private void pickPicture() {
		new PictureSourceDialog().show(getSupportFragmentManager(), "picture_chooser");
	}

	@Override
	public void onPictureSourceChosen(int result) {

		if (result == IChoosePicture.CHOOSE_EXISTING) {
			startActivityForResult(IntentUtils.pickImage(), IMAGE_CHOOSEN);
		} else {
			Uri uri = Uri.fromFile(getCaptureFileName());
			Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
			intent.putExtra(MediaStore.EXTRA_OUTPUT, uri);

			startActivityForResult(intent, PICTURE_TAKEN);
		}
	}

	@TargetApi(Build.VERSION_CODES.FROYO)
	protected File getCaptureFileName() {
		if (android.os.Build.VERSION.SDK_INT > Build.VERSION_CODES.FROYO) {
			return new File(getExternalCacheDir(), "mtrk.png");
		} else {
			return new File(Environment.getExternalStorageDirectory(), "mtrk.png");
		}
	}

	private void askIfToSaveAndMaybeDo() {
		if (!binder.isDirty()) {
			finish();
			return;
		}

		doTheYesNoDialog(R.string.save_changes_title, R.string.save_practice_changes_message, new OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				saveAndClose();
			}
		}, new OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				finish();
			}
		});
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);

		outState.putString(Constants.IMAGE_URL, practice.imageUrl);
	}

	@Override
	protected void onRestoreInstanceState(Bundle savedInstanceState) {
		super.onRestoreInstanceState(savedInstanceState);

		practice.imageUrl = savedInstanceState.getString(Constants.IMAGE_URL);
//TODO		binder.updateDirtyValues();
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (resultCode != RESULT_OK) {
			return;
		}
		
		Uri source = Uri.parse(Constants.SIXTEENTH_KARMAPA_PNG);
		switch (requestCode) {
		case IMAGE_CHOOSEN:
			source = data.getData();
			
			break;
		case PICTURE_TAKEN:
			source = Uri.fromFile(getCaptureFileName());
			
			break;
		}

		ContentValues sourcePaths = new ContentValues();
		sourcePaths.put(Constants.SOURCE_URL, source.toString());
		
		String fileName = model.getTitle();
		if (fileName == null || fileName.length() == 0) {
			fileName = "practice.png";
		}

		model.setImageUri(getContentResolver().insert(Uri.parse(Constants.MEDIA_PROVIDER_ROOT + fileName), 
								sourcePaths));
		binder.updateDirtyValues();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getSupportMenuInflater().inflate(R.menu.activity_practice_edit, menu);
		return true;
	}
}
