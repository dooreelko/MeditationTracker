package com.meditationtracker.controls;

import android.content.Context;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ListView;

public class NoScrollListView extends ListView
{

	private boolean assumedMeasurements;

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

		
		int childCount = Math.max(getCount(), getChildCount());
		int childHeight = getMeasuredHeight() - (getListPaddingTop() + getListPaddingBottom() +  getVerticalFadingEdgeLength() * 2);
		
		// on a first run let's have a space for at least one child so it'll trigger remeasurement
		int fullHeight = getListPaddingTop() + getListPaddingBottom() +  /*getVerticalFadingEdgeLength() * 2*/ + childHeight*(childCount);

		assumedMeasurements = false;
		int newChildHeight = 0;
		for (int x = 0; x<childCount; x++ ){
//			Log.d("MTRK", String.format("Try get child: %d", x));
			View childAt = getChildAt(x);
			
//			Log.d("MTRK", String.format("Child: %d is %s", x, childAt));
			if (childAt != null) {
				int height = childAt.getHeight();
				newChildHeight += height;

//				Log.d("MTRK", String.format("Measuring. Child: %d, childHeight: %d", x, height));
			}
			else {
				assumedMeasurements = true;
				newChildHeight += childHeight;
//				Log.d("MTRK", String.format("!!!Assumed measuring. Child: %d, childHeight: %d", x, childHeight));
				
			}
		}

		//on a second run with actual items - use proper size
		if (newChildHeight != 0)
			fullHeight = getListPaddingTop() + getListPaddingBottom() +  /*getVerticalFadingEdgeLength() * 2*/ + newChildHeight;

//		Log.d("MTRK", String.format("Measured. Cnt: %d, ChildCnt: %d, childHeight: %d, fullHeight: %d", getCount(), getChildCount(), childHeight, fullHeight));
		setMeasuredDimension(getMeasuredWidth(), fullHeight);
	}
	
	

	@Override
	public boolean requestChildRectangleOnScreen(View child, Rect rect, boolean immediate) {
//		Log.d("MTRK", "=====    requestChildRectangleOnScreen");
		forceRelayoutIfNeeded();
		return super.requestChildRectangleOnScreen(child, rect, immediate);
	}

	@Override
	public boolean onTouchEvent(MotionEvent ev) {
//		Log.d("MTRK", "=====    onTouchEvent");
		forceRelayoutIfNeeded();
		return super.onTouchEvent(ev);
	}

	@Override
	protected void layoutChildren() {
//		Log.d("MTRK", "=====    layoutChildren");
		super.layoutChildren();
		
		forceRelayoutIfNeeded();
	}

	private void forceRelayoutIfNeeded() {
		if (assumedMeasurements) {
//			Log.d("MTRK", "Forcing relayout");
			requestLayout();
		}
	}
	
}
