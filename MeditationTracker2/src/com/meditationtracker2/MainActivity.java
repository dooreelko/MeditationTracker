package com.meditationtracker2;

import java.util.Collections;
import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import butterknife.InjectView;
import butterknife.OnClick;
import butterknife.Views;

import com.actionbarsherlock.app.SherlockDialogFragment;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.fima.cardsui.views.CardUI;
import com.meditationtracker2.content.ICanFillView;
import com.meditationtracker2.content.data.IPracticeProvider;
import com.meditationtracker2.content.data.Practice;
import com.meditationtracker2.content.data.PracticeProviderFactory;
import com.meditationtracker2.preferences.Settinger;
import com.nostra13.universalimageloader.core.ImageLoader;

public class MainActivity extends PracticeActivity {
	@InjectView(R.id.flipper) CardUI flipper;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		PreferenceManager.setDefaultValues(this, R.xml.pref_general, true);
		PreferenceManager.setDefaultValues(this, R.xml.pref_notification, true);

		doMaybeFirstRun();
		
		setContentView(R.layout.activity_main);
		Views.inject(this);

//		flipper.setOnItemClickListener(itemSelected);
//		flipper.setEmptyView(Views.findById(this, R.id.layoutEmptyPracticeList));

		bindData();
	}

	protected void doMaybeFirstRun() {
		Settinger settinger = new Settinger(this);
		
		PackageManager pm = this.getPackageManager();
		PackageInfo packageInfo;
		try {
			packageInfo = pm.getPackageInfo(this.getPackageName(), 0);
			String currentVersion = packageInfo.versionCode + " \"" + packageInfo.versionName + "\"";

			String version = settinger.getString(R.string.prefVersion, "");
			
			if (!version.equals(currentVersion)) {
				settinger.putString(R.string.prefVersion, currentVersion);

				new SherlockDialogFragment(){
					@Override
					public View onCreateView(LayoutInflater inflater, ViewGroup container,
							Bundle savedInstanceState) {
						View view = inflater.inflate(R.layout.fragment_post_install, container);
						
				        getDialog().setTitle(R.string.hi);

				        return view;
				    }
					
				}.show(getSupportFragmentManager(), "");
			}
		} catch (NameNotFoundException e) {
		}
	}

	protected void bindData() {
		List<Practice> filteredPractices = filterAndSortPractices(getPracticeProvider().getPractices());
		boolean showFlat = new Settinger(this).getBoolean("dbgPrefFlatDisplay", true);
		

		flipper.clearCards();
		if (filteredPractices.size() == 0) {
			flipper.refresh();
			return;
		}

		if (!showFlat) {
			Collections.reverse(filteredPractices);
		}

		for (Practice p : filteredPractices) {
			PracticeCard card = new PracticeCard(p, viewFiller);
			card.setOnClickListener(cardClickListener);
			
			if (showFlat) {
				flipper.addCard(card);
			} else {
				flipper.addCardToLastStack(card);
				flipper.setCurrentStackTitle("All practices");
			}
		}
		
		flipper.refresh();
	}

	private List<Practice> filterAndSortPractices(final List<Practice> practices) {
		boolean showNgondro = new Settinger(this).getBoolean(R.string.prefShowNgondro, true);
		
		if (showNgondro) {
			return practices;
		}
		
		for (int x=practices.size()-1; x>=0; x--) {
			if (practices.get(x).isNgondro) {
				practices.remove(x);
			}
		}
		
		Collections.sort(practices);
		
		return practices;
	}

	private ICanFillView<Practice> viewFiller = new ICanFillView<Practice>() {
		private ImageLoader imageLoader = ImageLoader.getInstance();
		private int[] Colors = new int[] { 0xFFfae46a, 0xFFf2be44, 0xFFea8e37, 0xFFce4b39};
		
		@Override
		public void fill(View view, Practice with) {
			ImageView image = Views.findById(view, R.id.practice_image);
			TextView scheduledCountText = Views.findById(view, R.id.scheduled_count);
			TextView titleText = Views.findById(view, R.id.practice_title);
			TextView currentCountText = Views.findById(view, R.id.completed_count);
			
			titleText.setText(with.title);
			imageLoader.displayImage(with.imageUrl, image);
			
			int scheduledForToday = with.scheduledForToday;
			if (scheduledForToday > 0) {
				scheduledCountText.setText(String.valueOf(scheduledForToday));
			}
			else {
				Views.findById(view, R.id.scheduled_title).setVisibility(View.INVISIBLE);
				scheduledCountText.setVisibility(View.INVISIBLE);
			}
			
			if (with.currentCount != 0 && with.totalCount > 0) {
				long progress = with.currentCount * 100 / with.totalCount;
				currentCountText.setText(String.format("%d (%d%%)", with.currentCount, progress));

				ProgressBar pb = Views.findById(view, R.id.progressBarPractice);
				pb.setProgress((int) progress);
			}
			else {
				currentCountText.setText(String.valueOf(with.currentCount));
			}
			
			View bg = Views.findById(view, R.id.lay_outer);
			bg.setBackgroundColor(Colors[with.id % 4]);
			
			
			view.setTag((Integer)with.id);
		}
	};

	private IPracticeProvider getPracticeProvider() {
		return PracticeProviderFactory.getMeAProvider(this);
	}

	private OnClickListener cardClickListener = new OnClickListener() {
		
		@Override
		public void onClick(View view) {
			Integer practiceId = getPracticeIdFromTag(view);
			Practice practice = getPracticeProvider().getPractice(practiceId);
			startActivityForResult(new Intent(MainActivity.this, PracticeDetailActivity.class).putExtra(Constants.PRACTICE_ID, practice.id), 
									Constants.PRACTICE_VIEW_DONE);
		}
	};

	
	private Integer getPracticeIdFromTag(View view) {
		FrameLayout vv = ((FrameLayout)view);
		
		if (vv == null || vv.getChildCount() == 0) {
			return -1;
		}
		
		Integer practiceId = (Integer) (vv.getChildAt(0)).getTag();
		return practiceId;
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getSupportMenuInflater().inflate(R.menu.activity_main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {

		switch (item.getItemId()) {
			case R.id.menu_add:
				initiateAddPractice();
				break;

			case R.id.menu_settings:
				startActivityForPractice(-1, SettingsActivity.class, Constants.SETTINGS_DONE);
				break;
		}
		
		return true;
	}

	@OnClick(R.id.buttonAddPractice)
	void initiateAddPractice() {
		startActivityForPractice(Constants.NO_PRACTICE_ID, PracticeEditActivity.class, Constants.PRACTICE_EDIT_DONE);
	}

	private void startActivityForPractice(int practiceId, Class<? extends Activity> activityClass, int resultId) {
		startActivityForResult(new Intent(this, activityClass).putExtra(Constants.PRACTICE_ID, practiceId), resultId);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		
		bindData();
	}
}
