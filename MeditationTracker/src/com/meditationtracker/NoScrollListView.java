package com.meditationtracker;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.ListView;

public class NoScrollListView extends ListView
{

	public NoScrollListView(Context context)
	{
		super(context, null);
	}

	public NoScrollListView(Context context, AttributeSet attrs)
	{
		super(context, attrs);
	}
	
	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec)
	{
		super.onMeasure(widthMeasureSpec, MeasureSpec.makeMeasureSpec(MeasureSpec.UNSPECIFIED, 0) /*heightMeasureSpec*/);

		int childHeight = getMeasuredHeight() - (getListPaddingTop() + getListPaddingBottom() +  getVerticalFadingEdgeLength() * 2);
		
		// on a first run let's have a space for at least one child so it'll trigger remeasurement
		int fullHeight = getListPaddingTop() + getListPaddingBottom() +  /*getVerticalFadingEdgeLength() * 2*/ + childHeight*(getCount());

		int newChildHeight = 0;
		for (int x = 0; x<getChildCount(); x++ ){
			View childAt = getChildAt(x);
			
			if (childAt != null) {
				int height = childAt.getHeight();
				newChildHeight += height;
			}
		}

		//on a second run with actual items - use proper size
		if (newChildHeight != 0)
			fullHeight = getListPaddingTop() + getListPaddingBottom() +  /*getVerticalFadingEdgeLength() * 2*/ + newChildHeight;

		Log.d("MTRK", String.format("Measured. Cnt: %d, childHeight: %d, fullHeight: %d", getCount(), childHeight, fullHeight));
		setMeasuredDimension(getMeasuredWidth(), fullHeight);
	}
}
