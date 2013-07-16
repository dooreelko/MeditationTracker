package com.meditationtracker2;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.StackView;
import android.widget.TextView;

import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.view.Menu;
import com.meditationtracker2.content.CanFillView;
import com.meditationtracker2.content.ComplexViewArrayAdapter;
import com.meditationtracker2.content.MockContent;
import com.meditationtracker2.content.MockContent.Practice;

public class MainActivity extends SherlockActivity {

	protected static final int PRACTICE_DONE = 0;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
        final StackView flipper = (StackView) findViewById(R.id.flipper);

        flipper.setAdapter(new ComplexViewArrayAdapter<MockContent.Practice>(
                this,
                R.layout.fragment_practice_intro,
                R.id.practice_title,
                new MockContent(this).ITEMS, new CanFillView<MockContent.Practice>() {

					@Override
					public void fill(View view, Practice with) {
						ImageView image = (ImageView) view.findViewById(R.id.practice_image);
						TextView totalCountText = (TextView) view.findViewById(R.id.total_count);
						TextView currentCountText = (TextView) view.findViewById(R.id.completed_count);
						
						image.setImageResource(with.imageResId);
						totalCountText.setText(String.valueOf(with.totalCount));
						currentCountText.setText(String.valueOf(with.currentCount));
					}
				}));
        
        flipper.setOnItemClickListener(itemSelected);
	}

	private OnItemClickListener itemSelected = new OnItemClickListener() {

		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position,
				long id) {
			startActivityForResult(new Intent(MainActivity.this, PracticeDetailActivity.class).putExtra("id", position), PRACTICE_DONE);
		}
	};
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getSupportMenuInflater().inflate(R.menu.activity_main, menu);
		return true;
	}

}
