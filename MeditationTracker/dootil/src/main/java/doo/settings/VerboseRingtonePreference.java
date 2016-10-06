package doo.settings;

import android.content.Context;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.preference.RingtonePreference;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;

public class VerboseRingtonePreference extends RingtonePreference
{

	public VerboseRingtonePreference(Context context, AttributeSet attrs,
			int defStyle)
	{
		super(context, attrs, defStyle);
	}

	public VerboseRingtonePreference(Context context, AttributeSet attrs)
	{
		super(context, attrs);
	}

	public VerboseRingtonePreference(Context context)
	{
		super(context);
	}

	@Override
	protected View onCreateView(ViewGroup parent)
	{
		updateSummary(onRestoreRingtone());
		return super.onCreateView(parent);
	}

	@Override
	protected void onSaveRingtone(Uri ringtoneUri)
	{
		super.onSaveRingtone(ringtoneUri);
		
		updateSummary(ringtoneUri);
		
	}

	private void updateSummary(Uri ringtoneUri)
	{
		Ringtone ringtone = RingtoneManager.getRingtone(getContext(), ringtoneUri);
		if (ringtone != null)
		{
			setSummary(ringtone.getTitle(getContext()));
		}
	}
}
