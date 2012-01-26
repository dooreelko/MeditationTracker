package com.meditationtracker.controls;

import com.meditationtracker.R;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

public class MenuBar extends LinearLayout {
	private View view;

	public MenuBar(Context context, AttributeSet attrs) {
		super(context, attrs);

		view = View.inflate(context, R.layout.menubar, null);
		
		// XXX: WTF?!
		view.setLayoutParams(new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT));
		
		addView(view);
		
		TypedArray a=getContext().obtainStyledAttributes(attrs,R.styleable.Attributes);
		setText(a.getString(R.styleable.Attributes_text));
	}

	public void setText(String text) {
		((TextView)view.findViewById(R.id.textWindowTitle)).setText(text);
	}


}
