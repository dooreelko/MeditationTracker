package com.meditationtracker.preferences;

import com.meditationtracker.R;

import android.content.Context;
import android.content.res.TypedArray;
import android.preference.Preference;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;

public class SliderPreference extends Preference {

	private static final int DEFAULT = 3;
	private int max;
	private int cur;

	public SliderPreference(Context context, AttributeSet attrs) {
		super(context, attrs);
		
		TypedArray a=getContext().obtainStyledAttributes(attrs,R.styleable.ActionPreference);
		max = a.getInteger(R.styleable.SliderPreference_max, 100);
	}

	@Override
	protected void onSetInitialValue(boolean restorePersistedValue, Object defaultValue) {
		cur = restorePersistedValue ? getPersistedInt(DEFAULT) : Integer.getInteger(defaultValue.toString(), DEFAULT);
	}

	@Override
	protected View onCreateView(ViewGroup parent) {
		setLayoutResource(R.layout.slider_pref);
		
		View result = super.onCreateView(parent);
		SeekBar sb = (SeekBar) result.findViewById(R.id.prefSeekBar);
		sb.setMax(max);
		sb.setProgress(cur);
		sb.setOnSeekBarChangeListener(seekChanged);

		return result;
	}

	private OnSeekBarChangeListener seekChanged = new OnSeekBarChangeListener() {
		
		@Override
		public void onStopTrackingTouch(SeekBar seekBar) {
			
		}
		
		@Override
		public void onStartTrackingTouch(SeekBar seekBar) {
			
		}
		
		@Override
		public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
			SliderPreference.this.persistInt(progress);
		}
	};
	
}
