package com.meditationtracker;

import android.app.AlertDialog.Builder;
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
import android.view.WindowManager.LayoutParams;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.meditationtracker.R.id;
import com.meditationtracker.R.layout;
import com.meditationtracker.R.string;
import com.meditationtracker.controls.MenuBar;

import java.util.Calendar;

import doo.util.Pair;
import doo.util.Util;

public class SessionActivity extends BaseActivity {
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

		setContentView(layout.session);

		int windowFlagsToSet = LayoutParams.FLAG_FULLSCREEN;
		getWindow().setFlags(windowFlagsToSet, windowFlagsToSet);

		updateUI();
	}

	private void updateUI() {
		Bundle extras = getIntent().getExtras();
		if (extras != null) {
			String imgUrl = extras.getString(ExtraKeys.ImgURL);
			Pair<Boolean, Long> parsed = Util.tryParse(imgUrl);
			if (parsed._1)
				((ImageView) findViewById(id.imgPractice)).setImageResource(parsed._2.intValue());
			else
				((ImageView) findViewById(id.imgPractice)).setImageURI(Uri.parse(imgUrl));

			malaSize = extras.getLong(ExtraKeys.MalaSize);

			String practiceTitle = extras.getString(ExtraKeys.Title);
			((MenuBar) findViewById(id.menuBar)).setText(practiceTitle);
			// ((TextView)
			// findViewById(R.id.textPracticeName)).setText(practiceTitle);
		}

		Button btnAdd = (Button) findViewById(id.addMalaButton);
		btnAdd.setOnClickListener(addMalaClick);
		btnAdd.setText(String.format("%s (%d)", getString(string.addMala), malaSize));

        View fab = findViewById(id.menu_fab);
        if (fab != null) {
            fab.setOnClickListener(new OnClickListener() {
                @Override
				public void onClick(View v) {
                    openOptionsMenu();
                }
            });
        }

		// findViewById(R.id.textMalaCount).setOnKeyListener(malaCountChanged);

		findViewById(id.editMalaButton).setOnClickListener(editMalaClick);

		SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);

		doStopwatch = preferences.getBoolean(getString(string.prefUseStopWatch), true);

		Pair<Boolean, Long> parsed = Util.tryParse(preferences.getString(
				getString(string.prefSessionLength), "10"));
		sessionLength = 10 * 60;
		if (parsed._1)
			sessionLength = parsed._2.intValue() * 60;

		doSessionEndSound = preferences.getBoolean(getString(string.prefTimerSound), false);
		sessionEndSoundUrl = preferences.getString(getString(string.prefBellSound), "");
		doSessionEndBuzz = preferences.getBoolean(getString(string.prefTimerBuzz), false);

		timerView = (TextView) findViewById(id.textTimer);

		updateTimer(sessionLength * 1000);

		boolean dimAtNight = preferences.getBoolean(getString(string.prefDimNight), true);
		int hour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY);
		boolean isNight = hour >= 18 || hour < 9;

		if (dimAtNight && isNight) { // blind'n'night mode
			LayoutParams lp = getWindow().getAttributes();
			float dimAtNightValue = preferences.getInt(getString(string.prefDimNightValue), 3);
			
			lp.screenBrightness = dimAtNightValue/100;
			getWindow().setAttributes(lp);
		}

		oneBeadHeptic = malaSize == 1 && preferences.getBoolean(getString(string.prefOneBeadHeptic), true);
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);

		outState.putInt(CURRENT_COUNT, malaCount);
	}

	@Override
	protected void onRestoreInstanceState(Bundle savedInstanceState) {
		super.onRestoreInstanceState(savedInstanceState);

		malaCount = savedInstanceState.getInt(CURRENT_COUNT);
		updateResult();
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
		((TextView) findViewById(id.textViewMalaCount)).setText(String.valueOf(malaCount));
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

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.timer_menu, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		final int iid = item.getItemId();
		if (iid == id.startTimerMenuItem) {
			startTimer();
			return true;
		} else if (iid == id.stopTimerMenuItem) {
			stopTimer();
			return true;
		}

		return super.onOptionsItemSelected(item);

	}

	private final OnClickListener addMalaClick = new OnClickListener() {
		@Override
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

	private final OnClickListener editMalaClick = new OnClickListener() {
		@Override
		public void onClick(View v) {
			showDialog(DIALOG_CHANGE_MALA_COUNT);
		}
	};

	private EditText editTextMalaCount;

	@Override
	protected Dialog onCreateDialog(int id) {
		editTextMalaCount = new EditText(this);
		editTextMalaCount.setInputType(InputType.TYPE_CLASS_NUMBER);
		editTextMalaCount.setText(String.valueOf(malaCount));

		return new Builder(this)
				.setPositiveButton(android.R.string.ok, onEditMalaOkClick)
				.setNegativeButton(android.R.string.cancel, null).setView(editTextMalaCount)
				.setTitle(string.setMalaCount).create();
	}

	private final DialogInterface.OnClickListener onEditMalaOkClick = new DialogInterface.OnClickListener() {

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
