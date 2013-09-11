package com.meditationtracker2;

import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import butterknife.OnClick;
import butterknife.Views;

import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.meditationtracker2.content.PracticeViewUpdater;
import com.meditationtracker2.content.data.Practice;
import com.meditationtracker2.content.data.PracticeProviderFactory;
import com.meditationtracker2.model.PracticeDetailModel;
import com.meditationtracker2.preferences.Settinger;

public class PracticeDetailActivity extends PracticeActivity {
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_practice_detail);
		Views.inject(this);
	
		bindData();
	}

	protected void bindData() {
		Practice practice = getPractice();
		
		getSupportActionBar().setTitle(practice.title);
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		
		doo.bandera.Models.Bind(this, new PracticeDetailModel(practice), new PracticeViewUpdater());
	}

	@OnClick(R.id.buttonStart)
	void onClickStartPractice(View v) {
		startPractice();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getSupportMenuInflater().inflate(R.menu.activity_practice_detail, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		
		switch (item.getItemId()) {
		case(android.R.id.home): 
			this.finish();
			break;
		
		case(R.id.menu_edit): 
			startActivityForResult(new Intent(this, PracticeEditActivity.class)
				.putExtra(Constants.PRACTICE_ID, getPracticeId()), Constants.PRACTICE_EDIT_DONE);
			break;
		case R.id.menu_start:
			startPractice();
			break;
		case R.id.menu_delete:
			maybeDeletePractice();
			break;

		}
		
		return true;
	}

	private void startPractice() {
		startActivityForResult(new Intent(this, PracticeDoActivity.class)
			.putExtra(Constants.PRACTICE_ID, getPracticeId()), Constants.PRACTICE_DONE);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		
		if (resultCode == Constants.RESULT_DATA_CHANGED) {
			bindData();
		}
	}

	private void maybeDeletePractice() {
		Practice practice = getPractice();
		if (practice.isNgondro) {
			doTheYesNoDialog(R.string.title_delete_ngondro, R.string.message_delete_ngondro, new OnClickListener() {
				
				@Override public void onClick(DialogInterface dialog, int which) {
					new Settinger(PracticeDetailActivity.this).putBoolean(R.string.prefShowNgondro, false);
					PracticeDetailActivity.this.finish();
				}
			}, null);
			return;
		}
		
		PracticeProviderFactory.getMeAProvider(this).deletePractice(getPractice());
	}
	
}
