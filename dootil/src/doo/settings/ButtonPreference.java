package doo.settings;

import android.content.Context;
import android.preference.Preference;
import android.util.AttributeSet;

public class ButtonPreference extends Preference
{
	public ButtonPreference(Context context, AttributeSet attrs, int defStyle)
	{
		super(context, attrs, defStyle);
	}

	public ButtonPreference(Context context, AttributeSet attrs)
	{
		super(context, attrs, 0);
	}

	public ButtonPreference(Context context)
	{
		super(context, null);
	}
}
