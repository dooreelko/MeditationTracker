package com.meditationtracker;

import doo.util.Pair;
import doo.util.Util;

import android.database.Cursor;
import android.net.Uri;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.SimpleCursorAdapter.ViewBinder;

public class SmartViewBinder implements ViewBinder
{

	public boolean setViewValue(View view, Cursor cursor, int columnIndex)
	{
		String textToBind = cursor.getString(columnIndex);
		if (textToBind == null)
			return false;
		
		if (view instanceof ImageView) {
			ImageView imgView = (ImageView)view;

			Pair<Boolean, Long> parsed = Util.tryParse(textToBind);
			if (parsed._1){
				imgView.setImageResource(parsed._2.intValue());
			}
			else {
				// it's an url
				imgView.setImageURI(Uri.parse(textToBind));
			}
		}
		else if (view instanceof TextView) {
			((TextView)view).setText(textToBind);
		}
		else
			return false;
			
			
		return true;
	}

}
