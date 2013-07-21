package com.meditationtracker2;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.StackView;
import android.widget.TextView;
import butterknife.InjectView;
import butterknife.Views;

import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.meditationtracker2.content.CanFillView;
import com.meditationtracker2.content.ComplexViewArrayAdapter;
import com.meditationtracker2.content.IPracticeProvider;
import com.meditationtracker2.content.Practice;
import com.meditationtracker2.content.PracticeProviderFactory;

public class MainActivity extends SherlockActivity {
	@InjectView(R.id.flipper) StackView flipper;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		Views.inject(this);
		
        flipper.setAdapter(new ComplexViewArrayAdapter<Practice>(
                this,
                R.layout.fragment_practice_intro,
                R.id.practice_title,
                getPracticeProvider().getPractices(), new CanFillView<Practice>() {

					@Override
					public void fill(View view, Practice with) {
						ImageView image = Views.findById(view, R.id.practice_image);
						TextView totalCountText = Views.findById(view, R.id.total_count);
						TextView currentCountText = Views.findById(view, R.id.completed_count);
						
						image.setImageResource(with.imageResId);
						totalCountText.setText(String.valueOf(with.totalCount));
						currentCountText.setText(String.valueOf(with.currentCount));
						
						view.setTag((Integer)with.id);
					}
				}));
        
        flipper.setOnItemClickListener(itemSelected);
	}

	private IPracticeProvider getPracticeProvider() {
		return PracticeProviderFactory.getMeAProvider(this);
	}

	private OnItemClickListener itemSelected = new OnItemClickListener() {

		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
			Integer practiceId = getPracticeIdFromTag(view);
			Practice practice = getPracticeProvider().getPractice(practiceId);
			startActivityForResult(new Intent(MainActivity.this, PracticeDetailActivity.class).putExtra(Constants.PRACTICE_ID, practice.id), Constants.PRACTICE_VIEW_DONE);
		}

	};

	private Integer getPracticeIdFromTag(View view) {
		FrameLayout vv = ((FrameLayout)view);
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

			case R.id.menu_settings:
				startActivityForPractice(practiceId, SettingsActivity.class, Constants.SETTINGS_DONE);
				break;
		}
		
		return true;
	}

	private void startActivityForPractice(int practiceId, Class<? extends Activity> activityClass, int resultId) {
		startActivityForResult(new Intent(this, activityClass).putExtra(Constants.PRACTICE_ID, practiceId), resultId);
	}
}
