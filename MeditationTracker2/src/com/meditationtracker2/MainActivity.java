package com.meditationtracker2;

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
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.StackView;
import android.widget.TextView;
import butterknife.InjectView;
import butterknife.OnClick;
import butterknife.Views;

import com.actionbarsherlock.app.SherlockDialogFragment;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.meditationtracker2.content.ComplexViewArrayAdapter;
import com.meditationtracker2.content.ICanFillView;
import com.meditationtracker2.content.data.IPracticeProvider;
import com.meditationtracker2.content.data.Practice;
import com.meditationtracker2.content.data.PracticeProviderFactory;
import com.meditationtracker2.preferences.Settinger;
import com.nostra13.universalimageloader.core.ImageLoader;

public class MainActivity extends PracticeActivity {
	@InjectView(R.id.flipper) StackView flipper;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		PreferenceManager.setDefaultValues(this, R.xml.pref_general, true);
		PreferenceManager.setDefaultValues(this, R.xml.pref_notification, true);

		doMaybeFirstRun();
		
		setContentView(R.layout.activity_main);
		Views.inject(this);

		flipper.setOnItemClickListener(itemSelected);
		flipper.setEmptyView(Views.findById(this, R.id.layoutEmptyPracticeList));
		
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
		List<Practice> filteredPractices = filterPractices(getPracticeProvider().getPractices());

		if (filteredPractices.size() == 0) {
			flipper.setAdapter(null);
			return;
		}
		
		ComplexViewArrayAdapter<Practice> adapter = new ComplexViewArrayAdapter<Practice>(
                this,
                R.layout.fragment_practice_intro,
                R.id.practice_title,
                filteredPractices, viewFiller);

		flipper.setAdapter(adapter);
		((ComplexViewArrayAdapter<?>)flipper.getAdapter()).notifyDataSetChanged();
	}

	private List<Practice> filterPractices(final List<Practice> practices) {
		boolean showNgondro = new Settinger(this).getBoolean(R.string.prefShowNgondro, true);
		
		if (showNgondro) {
			return practices;
		}
		
		for (int x=practices.size()-1; x>=0; x--) {
			if (practices.get(x).isNgondro) {
				practices.remove(x);
			}
		}
		
		return practices;
	}

	private ICanFillView<Practice> viewFiller = new ICanFillView<Practice>() {
		private ImageLoader imageLoader = ImageLoader.getInstance();
		
		@Override
		public void fill(View view, Practice with) {
			ImageView image = Views.findById(view, R.id.practice_image);
			TextView scheduledCountText = Views.findById(view, R.id.scheduled_count);
			TextView currentCountText = Views.findById(view, R.id.completed_count);
			
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
				currentCountText.setText(String.format("%d (%d%%)", with.currentCount, with.currentCount * 100 / with.totalCount));
			}
			else {
				currentCountText.setText(String.valueOf(with.currentCount));
			}
			
			view.setTag((Integer)with.id);
		}
	};

	private IPracticeProvider getPracticeProvider() {
		return PracticeProviderFactory.getMeAProvider(this);
	}

	private OnItemClickListener itemSelected = new OnItemClickListener() {

		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
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
		Integer practiceId = getPracticeIdFromTag(flipper.getCurrentView());

		switch (item.getItemId()) {
			case R.id.menu_start:
				startActivityForPractice(practiceId, PracticeDoActivity.class, Constants.PRACTICE_DONE);
				break;

			case R.id.menu_add:
				initiateAddPractice();
				break;

			case R.id.menu_settings:
				startActivityForPractice(practiceId, SettingsActivity.class, Constants.SETTINGS_DONE);
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
