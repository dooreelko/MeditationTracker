package com.meditationtracker.controls;

import com.meditationtracker.R;
import com.meditationtracker.R.id;
import com.meditationtracker.R.layout;
import com.meditationtracker.R.styleable;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

public class MenuBar extends LinearLayout {
	private final View view;

	public MenuBar(Context context, AttributeSet attrs) {
		super(context, attrs);

		view = View.inflate(context, layout.menubar, null);
		
		// XXX: WTF?!
		view.setLayoutParams(new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT));
		
		addView(view);
		
		TypedArray a=getContext().obtainStyledAttributes(attrs, styleable.Attributes);
		setText(a.getString(styleable.Attributes_text));
	}

	public void setText(String text) {
		((TextView)view.findViewById(id.textWindowTitle)).setText(text);
	}


}
