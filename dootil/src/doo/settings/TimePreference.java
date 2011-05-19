package doo.settings;

import android.content.Context;
import android.os.Parcelable;
import android.preference.Preference;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TimePicker;
import android.widget.TimePicker.OnTimeChangedListener;
import doo.util.root.R;

public class TimePreference extends Preference implements OnTimeChangedListener
{
	private TimePicker timePicker;

	public TimePreference(Context context, AttributeSet attrs, int defStyle)
	{
		super(context, attrs, defStyle);
	}

	public TimePreference(Context context, AttributeSet attrs)
	{
		super(context, attrs);
	}

	public TimePreference(Context context)
	{
		super(context);
	}
	
	public int getTimeInMinutes()
	{
		return getPersistedInt(0);
	}
	
	@Override
	protected View onCreateView(ViewGroup parent)
	{
		if (timePicker == null)
		{
			this.setLayoutResource(R.layout.timepicker);

			View view = super.onCreateView(parent);
			TimePicker timePicker = (TimePicker) view
					.findViewById(R.id.prefTimePicker);
			timePicker.setIs24HourView(true); //TODO: for some reason it's not automatically read from system settings, so do it manually here
			timePicker.setOnTimeChangedListener(this);
			return view;
		}

		return (View) timePicker.getParent();
	}

	@Override
	protected void onBindView(View view)
	{
		super.onBindView(view);

		TimePicker tp = (TimePicker) view.findViewById(R.id.prefTimePicker);

		Integer allTime = getPersistedInt(tp.getCurrentHour() * 60 + tp.getCurrentMinute());

		tp.setCurrentHour(allTime / 60);
		tp.setCurrentMinute(allTime % 60);
	}

	@Override
	protected void onPrepareForRemoval()
	{
		// TODO Auto-generated method stub
		super.onPrepareForRemoval();
	}

	@Override
	protected void onRestoreInstanceState(Parcelable state)
	{
		// TODO Auto-generated method stub
		super.onRestoreInstanceState(state);
	}

	@Override
	protected Parcelable onSaveInstanceState()
	{
		// TODO Auto-generated method stub
		return super.onSaveInstanceState();
	}

	public void onTimeChanged(TimePicker view, final int hourOfDay,
			final int minute)
	{
		setNewTime(hourOfDay, minute);
	}

	private void setNewTime(final int hourOfDay, final int minute)
	{
		setNewTime(timeToInt(hourOfDay, minute));
	}

	private void setNewTime(final int compressedTime)
	{
        if (!callChangeListener(compressedTime)) {
            return;
        }

		this.persistInt(compressedTime);

		notifyDependencyChange(shouldDisableDependents());
        
        notifyChanged();
        
	}
	
	private int timeToInt(final int hourOfDay, final int minute)
	{
		return hourOfDay * 60 + minute;
	}

}
