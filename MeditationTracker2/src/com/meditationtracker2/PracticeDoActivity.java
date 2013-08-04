package com.meditationtracker2;

import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnFocusChangeListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageButton;
import butterknife.InjectView;
import butterknife.OnClick;
import butterknife.Views;

import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.meditationtracker2.content.Practice;
import com.meditationtracker2.content.PracticeProviderFactory;
import com.meditationtracker2.model.PracticeDoModel;

import doo.bandera.ModelBinder;

public class PracticeDoActivity extends PracticeActivity {

	@InjectView(R.id.buttonAddMala) ImageButton buttonAddMala;
	@InjectView(R.id.editMalaCount)	EditText editMalaCount;
	@InjectView(R.id.editMalaSize) EditText editMalaSize;
	@InjectView(R.id.editSessionCompletedCount) EditText editSessionTotalCount;
	
	private ModelBinder binder;
	private PracticeDoModel model;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_practice_do);
		Views.inject(this);

		Practice practice = getPractice();

		getSupportActionBar().setTitle(practice.title);
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);

		editMalaCount.setOnFocusChangeListener(onFocusChanged);
		editMalaSize.setOnFocusChangeListener(onFocusChanged);
		editSessionTotalCount.setOnFocusChangeListener(onFocusChanged);

		model = new PracticeDoModel(practice);
		binder = doo.bandera.Models.Bind(this, model);
	}

	protected void updatePracticeImage(String url) {
		Uri uri = Uri.parse(url);
		buttonAddMala.setImageURI(uri);
	}

	private OnFocusChangeListener onFocusChanged = new OnFocusChangeListener() {
		@Override
		public void onFocusChange(View v, boolean hasFocus) {
			View currentFocus = getCurrentFocus();
			if (currentFocus instanceof EditText) {
				return;
			}

			buttonAddMala.requestFocus();
			InputMethodManager im = (InputMethodManager)PracticeDoActivity.this.getSystemService(Context.INPUT_METHOD_SERVICE);
			im.hideSoftInputFromWindow(v.getWindowToken(), 0);
		}
	};
	
	@OnClick(R.id.buttonAddMala)
	void onClickAddMala(View v) {
		model.addMala();
		binder.updateDirtyValues();
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {

		switch (item.getItemId()) {
		case (android.R.id.home):
			askIfToSaveAndMaybeDo();
			break;

		case (R.id.menu_accept):
			saveAndClose();
			break;

		case (R.id.menu_discard):
			finish();
		}

		return true;
	}
	
	@Override
	public void onBackPressed() {
		askIfToSaveAndMaybeDo();
	}

	private void askIfToSaveAndMaybeDo() {
		if (!binder.isDirty()) {
			finish();
		}
		
		doTheYesNoDialog(R.string.save_changes_title,
				R.string.save_session_changes_message, 
				new OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						saveAndClose();
					}
				}, 
				new OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						PracticeDoActivity.this.finish();
					}
				});
	}

	private void saveAndClose() {
		if (binder.isDirty()) {
			Practice practice = getPractice();
			practice.addSession(model.getTotalCount());
			PracticeProviderFactory.getMeAProvider(this).savePractice(practice);
		}

		finish();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getSupportMenuInflater().inflate(R.menu.activity_practice_do, menu);
		return true;
	}
}
