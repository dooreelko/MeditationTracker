package com.meditationtracker;

import java.util.Calendar;

import com.meditationtracker.controls.MenuBar;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Vibrator;
import android.preference.PreferenceManager;
import android.text.InputType;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import doo.util.Pair;
import doo.util.Util;

public class SessionActivity extends VerboseActivity {
	private static final String CURRENT_COUNT = "CURRENT_COUNT";

	protected static final int DIALOG_CHANGE_MALA_COUNT = 0;

	protected int malaCount;

	protected int sessionLength;
	protected long malaSize;
	protected boolean doStopwatch;
	protected boolean doSessionEndSound;
	protected String sessionEndSoundUrl;
	protected boolean doSessionEndBuzz;

	private boolean oneBeadHeptic;

	protected static CountDownTimer timer;
	protected static TextView timerView;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.session);

		int windowFlagsToSet = WindowManager.LayoutParams.FLAG_FULLSCREEN;
		getWindow().setFlags(windowFlagsToSet, windowFlagsToSet);

		updateUI();
	}

	private void updateUI() {
		Bundle extras = getIntent().getExtras();
		if (extras != null) {
			String imgUrl = extras.getString(ExtraKeys.ImgURL);
			Pair<Boolean, Long> parsed = Util.tryParse(imgUrl);
			if (parsed._1)
				((ImageView) findViewById(R.id.imgPractice)).setImageResource(parsed._2.intValue());
			else
				((ImageView) findViewById(R.id.imgPractice)).setImageURI(Uri.parse(imgUrl));

			malaSize = extras.getLong(ExtraKeys.MalaSize);

			String practiceTitle = extras.getString(ExtraKeys.Title);
			((MenuBar) findViewById(R.id.menuBar)).setText(practiceTitle);
			// ((TextView)
			// findViewById(R.id.textPracticeName)).setText(practiceTitle);
		}

		Button btnAdd = (Button) findViewById(R.id.addMalaButton);
		btnAdd.setOnClickListener(addMalaClick);
		btnAdd.setText(String.format("%s (%d)", getString(R.string.addMala), malaSize));

		// findViewById(R.id.textMalaCount).setOnKeyListener(malaCountChanged);

		((Button) findViewById(R.id.editMalaButton)).setOnClickListener(editMalaClick);

		SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);

		doStopwatch = preferences.getBoolean(getString(R.string.prefUseStopWatch), true);

		Pair<Boolean, Long> parsed = Util.tryParse(preferences.getString(
				getString(R.string.prefSessionLength), "10"));
		sessionLength = 10 * 60;
		if (parsed._1)
			sessionLength = parsed._2.intValue() * 60;

		doSessionEndSound = preferences.getBoolean(getString(R.string.prefTimerSound), false);
		sessionEndSoundUrl = preferences.getString(getString(R.string.prefBellSound), "");
		doSessionEndBuzz = preferences.getBoolean(getString(R.string.prefTimerBuzz), false);

		timerView = (TextView) findViewById(R.id.textTimer);

		updateTimer(sessionLength * 1000);

		boolean dimAtNight = preferences.getBoolean(getString(R.string.prefDimNight), true);
		int hour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY);
		boolean isNight = hour > 18 || hour < 9;

		if (dimAtNight && isNight) { // blind'n'night mode
			WindowManager.LayoutParams lp = getWindow().getAttributes();
			lp.screenBrightness = 0.1f;
			getWindow().setAttributes(lp);
		}

		oneBeadHeptic = malaSize == 1 && preferences.getBoolean(getString(R.string.prefOneBeadHeptic), true);
	}

	@Override
	protected void onRestoreInstanceState(Bundle savedInstanceState) {
		super.onRestoreInstanceState(savedInstanceState);

		malaCount = savedInstanceState.getInt(CURRENT_COUNT);
		updateResult();
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);

		outState.putInt(CURRENT_COUNT, malaCount);
	}

	private void updateTimer(long time) {
		long h = time / 3600000;
		long m = (time - h * 3600000) / 60000;
		long s = (time / 1000) % 60;
		if (timer != null) {
			timerView.setVisibility(View.VISIBLE);
			timerView.setText(String.format("%02d:%02d:%02d", h, m, s));
		}
	}

	private void updateResult() {
		setResult(RESULT_OK, new Intent().putExtra(ExtraKeys.MalaCount, malaCount));
		((TextView) findViewById(R.id.textViewMalaCount)).setText(String.valueOf(malaCount));
	}

	private void startTimer() {
		stopTimer();

		timer = new CountDownTimer(sessionLength * 1000, 1000) {

			@Override
			public void onFinish() {
				updateTimer(0);

				if (doSessionEndBuzz) {
					vibrate(1000);
				}

				if (doSessionEndSound) {
					MediaPlayer mp = new MediaPlayer();
					try {
						mp.setDataSource(sessionEndSoundUrl);
						mp.prepare();
						mp.start();
					} catch (Exception e) {
						e.printStackTrace();
					}
				}

			}

			@Override
			public void onTick(long millisUntilFinished) {
				long time = millisUntilFinished;
				if (doStopwatch)
					time = sessionLength * 1000 - millisUntilFinished;

				updateTimer(time);

			}

		}.start();
	}

	private void stopTimer() {
		if (timer != null) {
			timer.cancel();
			timer = null;
		}
		updateTimer(sessionLength * 1000);
		timerView.setVisibility(View.GONE);
	}

	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.timer_menu, menu);
		return true;
	}

	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.startTimerMenuItem:
			startTimer();
			return true;
		case R.id.stopTimerMenuItem:
			stopTimer();
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}

	private OnClickListener addMalaClick = new OnClickListener() {
		public void onClick(View v) {
			int preMalaCount = malaCount;
			malaCount += malaSize;
			updateResult();

			boolean is10 = (preMalaCount % 10) > (malaCount % 10);
			boolean is50 = (preMalaCount % 50) > (malaCount % 50);

			if (oneBeadHeptic && (is10 || is50))
				vibrate(is50);
			else
				vibrate(50);
		}
	};

	private OnClickListener editMalaClick = new OnClickListener() {
		@Override
		public void onClick(View v) {
			SessionActivity.this.showDialog(DIALOG_CHANGE_MALA_COUNT);
		}
	};

	private EditText editTextMalaCount;

	@Override
	protected Dialog onCreateDialog(int id) {
		editTextMalaCount = new EditText(this);
		editTextMalaCount.setInputType(InputType.TYPE_CLASS_NUMBER);
		editTextMalaCount.setText(String.valueOf(malaCount));

		return new AlertDialog.Builder(SessionActivity.this)
				.setPositiveButton(android.R.string.ok, onEditMalaOkClick)
				.setNegativeButton(android.R.string.cancel, null).setView(editTextMalaCount)
				.setTitle(R.string.setMalaCount).create();
	}

	private android.content.DialogInterface.OnClickListener onEditMalaOkClick = new android.content.DialogInterface.OnClickListener() {

		@Override
		public void onClick(DialogInterface dialog, int which) {
			updateMalaCount(editTextMalaCount.getText().toString());
		}
	};

	private void updateMalaCount(CharSequence s) {
		Pair<Boolean, Long> parsed = Util.tryParse(s.toString());
		if (parsed._1) {
			malaCount = parsed._2.intValue();
		} else {
			malaCount = 0;
		}

		updateResult();
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
}
