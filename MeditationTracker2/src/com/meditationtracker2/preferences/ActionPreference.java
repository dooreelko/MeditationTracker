package com.meditationtracker2.preferences;

import com.meditationtracker2.R;

import android.content.Context;
import android.content.res.TypedArray;
import android.preference.Preference;
import android.util.AttributeSet;
import android.widget.Toast;

public class ActionPreference extends Preference {
	public interface IActor {
		void act(Preference preference, String param);
	}

	private String actorClass;
	private String actionParam;
	
	public ActionPreference(Context context, AttributeSet attrs) {
		super(context, attrs);

		TypedArray a=getContext().obtainStyledAttributes(attrs,R.styleable.Attributes);
		actorClass = a.getString(R.styleable.Attributes_actor);
		actionParam = a.getString(R.styleable.Attributes_param);
	}

	@Override
	protected void onClick() {
		try {
			Class<?> actor = Class.forName(actorClass);
			Object newInstance = actor.getConstructor((Class[])null).newInstance((Object[])null);
			
			((IActor)newInstance).act(this, actionParam);
		} catch (Exception e) {
			Toast.makeText(getContext(), "Failed acting with class " + actorClass + "\n"+e, Toast.LENGTH_LONG).show();
		} 	}

	
}
