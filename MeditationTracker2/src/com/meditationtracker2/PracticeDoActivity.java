package com.meditationtracker2;

import java.util.Calendar;

import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.os.Bundle;
import android.os.Vibrator;
import android.view.View;
import android.view.View.OnFocusChangeListener;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageButton;
import butterknife.InjectView;
import butterknife.OnClick;
import butterknife.Views;

import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.meditationtracker2.content.PracticeViewUpdater;
import com.meditationtracker2.content.data.IPracticeProvider;
import com.meditationtracker2.content.data.Practice;
import com.meditationtracker2.content.data.PracticeProviderFactory;
import com.meditationtracker2.model.PracticeDoModel;
import com.meditationtracker2.preferences.Settinger;

import doo.bandera.ModelBinder;

public class PracticeDoActivity extends PracticeActivity {

	private static final int DEFAULT_MALA_SIZE = 108;
	@InjectView(R.id.buttonAddMala) ImageButton buttonAddMala;
	@InjectView(R.id.editMalaCount)	EditText editMalaCount;
	@InjectView(R.id.editMalaSize) EditText editMalaSize;
	@InjectView(R.id.editSessionCompletedCount) EditText editSessionTotalCount;
	
	private ModelBinder binder;
	private PracticeDoModel model;
	private boolean beadHaptic;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setupScreenForSession();
		
		setContentView(R.layout.activity_practice_do);
		Views.inject(this);

		editMalaCount.setOnFocusChangeListener(onFocusChanged);
		editMalaSize.setOnFocusChangeListener(onFocusChanged);
		editSessionTotalCount.setOnFocusChangeListener(onFocusChanged);

		bindData();

		beadHaptic = new Settinger(this).getBoolean(R.string.prefOneBeadHaptic, true);
	}

	public void setupScreenForSession() {
		int windowFlagsToSet = WindowManager.LayoutParams.FLAG_FULLSCREEN;
		getWindow().setFlags(windowFlagsToSet, windowFlagsToSet);
		
		Settinger settinger = new Settinger(this);
		int hour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY);
		boolean isNight = hour >= 18 || hour < 9;
		if (isNight && settinger.getBoolean(R.string.prefDimNight, false)) {
			float dimAtNightValue = settinger.getInt(R.string.prefDimNightValue, 3);
			
			WindowManager.LayoutParams lp = getWindow().getAttributes();
			
			lp.screenBrightness = dimAtNightValue/100;
			getWindow().setAttributes(lp);
		}
	}

	protected void bindData() {
		Practice practice = getPractice();

		getSupportActionBar().setTitle(practice.title);
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);

		model = new PracticeDoModel(practice, this);
		binder = doo.bandera.Models.Bind(this, model, new PracticeViewUpdater());
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
		int preMalaCount = model.getMalaCount();
		
		
		model.addMala();
		binder.updateDirtyValues();
		
		doTheBuzz(preMalaCount, model.getMalaCount());
	}

	private void doTheBuzz(int preMalaCount, int postMalaCount) {
		boolean is10 = (preMalaCount % 10) > (postMalaCount % 10);
		boolean is50 = (preMalaCount % 50) > (postMalaCount % 50);

		if (beadHaptic && (is10 || is50))
			vibrate(is50);
		else
			vibrate(50);
	}

	protected void vibrate(int duration) {
		Vibrator vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);
		vibrator.vibrate(duration);
	}

	long[] pattern10 = { 0, 30, 100, 100 };
	long[] pattern50 = { 0, 100, 100, 30, 100, 100 };

	protected void vibrate(boolean is50) {
		Vibrator vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);
		vibrator.vibrate(is50 ? pattern50 : pattern10, -1);
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
			return;
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
			IPracticeProvider dataProvider = PracticeProviderFactory.getMeAProvider(this);
			Practice practice = getPractice();
			dataProvider.addSession(practice, model.getTotalCount());
			
			int defaultMalaSize = new Settinger(this).getInt(R.string.prefMalaSize, DEFAULT_MALA_SIZE);
			if (model.getMalaSize() != defaultMalaSize && practice.malaSize != model.getMalaSize()) {
				practice.malaSize = model.getMalaSize();
				dataProvider.savePractice(practice);
			}
		}

		setResult(Constants.RESULT_DATA_CHANGED);
		finish();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getSupportMenuInflater().inflate(R.menu.activity_practice_do, menu);
		return true;
	}
}
