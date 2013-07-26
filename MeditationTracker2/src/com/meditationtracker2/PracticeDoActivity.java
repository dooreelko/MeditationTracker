package com.meditationtracker2;

import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import butterknife.InjectView;
import butterknife.OnClick;
import butterknife.Views;

import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.meditationtracker2.content.Practice;
import com.meditationtracker2.content.PracticeProviderFactory;
import com.meditationtracker2.helper.SimpleTextWatcher;

public class PracticeDoActivity extends PracticeActivity {

	@InjectView(R.id.buttonAddMala) ImageButton buttonAddMala;
	@InjectView(R.id.editMalaCount)	EditText editMalaCount;
	@InjectView(R.id.editPracticeTotal) EditText editMalaSize;
	@InjectView(R.id.editPracticeCompletedCount) EditText editSessionTotalSize;

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

		editMalaCount.addTextChangedListener(onMalaCountOrSizeTextChanged);
		editSessionTotalSize.addTextChangedListener(onSessionTotalTextChanged);

		totalCount = 0;
		malaCount = 0;
		malaSize = practice.malaSize;

		updateFields();
	}

	private TextWatcher onMalaCountOrSizeTextChanged = new SimpleTextWatcher() {


		@Override
		public void onTextChanged(CharSequence s, int start, int before, int count) {
			Integer newMalaCount;
			Integer newMalaSize;

			try {
				newMalaCount = Integer.valueOf(editMalaCount.getText().toString());
				newMalaSize = Integer.valueOf(editMalaSize.getText().toString());

				malaCount = newMalaCount;
				malaSize = newMalaSize;
				totalCount = newMalaCount * newMalaSize;

				updateFields();
				dirty = true;
			} catch (Exception e) {
			}
		}
	};

	private TextWatcher onSessionTotalTextChanged = new SimpleTextWatcher() {

		@Override
		public void onTextChanged(CharSequence s, int start, int before,
				int count) {
			try {
				totalCount = Integer.parseInt(s.toString());

				boolean enableOthers = totalCount == malaCount * malaSize;
				editMalaCount.setEnabled(enableOthers);
				editMalaSize.setEnabled(enableOthers);
				dirty = true;
			} catch (Exception e) {
			}
		}
	};

	@OnClick(R.id.buttonAddMala)
	void onClickAddMala(View v) {
		malaCount++;
		dirty = true;
		updateFields();
	}

	private void updateFields() {
		Uri uri = Uri.parse(getPractice().imageUrl);
		buttonAddMala.setImageURI(uri);

		editMalaCount.setTag(String.valueOf(malaCount));
		editMalaSize.setTag(String.valueOf(malaSize));
		editSessionTotalSize.setTag(String.valueOf(totalCount));
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
