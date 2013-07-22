package com.meditationtracker2;

import com.actionbarsherlock.app.SherlockActivity;
import com.meditationtracker2.content.Practice;
import com.meditationtracker2.content.PracticeProviderFactory;

public class PracticeActivity extends SherlockActivity {
	private Practice practice;
	
	protected Practice getPractice() {
		return practice != null ? practice : (practice = PracticeProviderFactory
				.getMeAProvider(this).getPractice(getIntent().getIntExtra(Constants.PRACTICE_ID, -1)));
	}

	protected int getPracticeId() {
		return getPractice().id;
	}
}
