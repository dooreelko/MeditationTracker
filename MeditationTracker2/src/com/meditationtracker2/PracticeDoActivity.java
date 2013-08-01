package com.meditationtracker2;

import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextWatcher;
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

import doo.bandera.helper.SimpleTextWatcher;

public class PracticeDoActivity extends PracticeActivity {

	@InjectView(R.id.buttonAddMala) ImageButton buttonAddMala;
	@InjectView(R.id.editMalaCount)	EditText editMalaCount;
	@InjectView(R.id.editPracticeTotal) EditText editMalaSize;
	@InjectView(R.id.editPracticeCompletedCount) EditText editSessionTotalCount;

	private boolean softUpdate;
	private boolean dirty;
	private int malaCount;
	private int malaSize;
	private int totalCount;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_practice_do);
		Views.inject(this);

		Practice practice = getPractice();

		getSupportActionBar().setTitle(practice.title);
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);

		editMalaCount.addTextChangedListener(onMalaCountTextChanged);
		editMalaSize.addTextChangedListener(onMalaSizeTextChanged);
		editSessionTotalCount.addTextChangedListener(onSessionTotalTextChanged);

		editMalaCount.setOnFocusChangeListener(onFocusChanged);
		editMalaSize.setOnFocusChangeListener(onFocusChanged);
		editSessionTotalCount.setOnFocusChangeListener(onFocusChanged);
		
		totalCount = 0;
		malaCount = 0;
		malaSize = practice.malaSize;

		updatePracticeImage(practice.imageUrl);
		updateFields();
	}

	protected void updatePracticeImage(String url) {
		Uri uri = Uri.parse(url);
		buttonAddMala.setImageURI(uri);
	}

	private void updateFields() {
		softUpdate = true;
		editMalaCount.setText(String.valueOf(malaCount));
		editMalaSize.setText(String.valueOf(malaSize));
		updateTotalCount();
		softUpdate = false;
	}

	protected void updateTotalCount() {
		editSessionTotalCount.setText(String.valueOf(totalCount));
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
	
	
	private TextWatcher onMalaCountTextChanged = new SimpleTextWatcher() {
		@Override
		public void onTextChanged(CharSequence s, int start, int before, int count) {
			if (softUpdate) {
				return;
			}
			
			softUpdate = true;
			
			Integer newMalaCount;

			try {
				newMalaCount = Integer.valueOf(editMalaCount.getText().toString());

				malaCount = newMalaCount;
				totalCount = newMalaCount * malaSize;

				updateTotalCount();
				dirty = true;
			} catch (Exception e) {
			}

			softUpdate = false;
		}
	};

	private TextWatcher onMalaSizeTextChanged = new SimpleTextWatcher() {
		@Override
		public void onTextChanged(CharSequence s, int start, int before, int count) {
			if (softUpdate) {
				return;
			}
			
			softUpdate = true;
			
			Integer newMalaSize;

			try {
				newMalaSize = Integer.valueOf(editMalaSize.getText().toString());

				malaSize = newMalaSize;
				totalCount = malaCount * newMalaSize;

				updateTotalCount();
				dirty = true;
			} catch (Exception e) {
			}

			softUpdate = false;
		}
	};

	
	private TextWatcher onSessionTotalTextChanged = new SimpleTextWatcher() {

		@Override
		public void onTextChanged(CharSequence s, int start, int before, int count) {
			if (softUpdate) {
				return;
			}
			
			softUpdate = true;
			
			try {
				totalCount = Integer.parseInt(s.toString());

				boolean enableOthers = totalCount == malaCount * malaSize;
				editMalaCount.setEnabled(enableOthers);
				editMalaSize.setEnabled(enableOthers);
				dirty = true;
			} catch (Exception e) {
			}

			softUpdate = false;
		}
	};

	@OnClick(R.id.buttonAddMala)
	void onClickAddMala(View v) {
		malaCount++;
		totalCount = malaCount*malaSize;
		dirty = true;
		updateFields();
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
		if (!dirty) {
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
		if (dirty) {
			Practice practice = getPractice();
			practice.addSession(totalCount);
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
