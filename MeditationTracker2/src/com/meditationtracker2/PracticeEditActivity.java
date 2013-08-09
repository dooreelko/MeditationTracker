package com.meditationtracker2;

import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import butterknife.InjectView;
import butterknife.OnClick;
import butterknife.Views;

import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.meditationtracker2.content.data.Practice;
import com.meditationtracker2.content.data.PracticeProviderFactory;
import com.meditationtracker2.model.PracticeEditModel;

import doo.bandera.ModelBinder;

public class PracticeEditActivity extends PracticeActivity implements PictureSourceDialog.IChoosePicture {
	@InjectView(R.id.buttonPracticeImage) ImageButton buttonPracticeImage;

	private Practice practice = new Practice();
	private PracticeEditModel model;

	private ModelBinder binder;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_edit_practice);
		Views.inject(this);
		
		if (getPracticeIdFromIntent() != Constants.NO_PRACTICE_ID) {
			practice = getPractice();
		}
		
		getSupportActionBar().setTitle(practice.title);
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		
		model = new PracticeEditModel(practice);
		binder = doo.bandera.Models.Bind(this, model);
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
		
		finish();
	}

	private void pickPicture() {
		new PictureSourceDialog().show(getSupportFragmentManager(), "picture_chooser");
	}

	@Override
	public void onPictureSourceChosen(int result) {
		//TODO: get the image
		model.setImageUri(Uri.parse("content://com.meditationtracker2.images/sixteenth_karmapa"));
		binder.updateDirtyValues();
	}
	
	private void askIfToSaveAndMaybeDo() {
		if (!binder.isDirty()) {
			finish();
		}
		
		doTheYesNoDialog(R.string.save_changes_title, R.string.save_practice_changes_message, 
			new OnClickListener() {
				
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

/*TODO		updatePictures();
		recalculatePicker();*/
	}
	
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getSupportMenuInflater().inflate(R.menu.activity_practice_edit, menu);
		return true;
	}
}
