package com.meditationtracker2.content;

import java.util.List;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

public class ComplexViewArrayAdapter<T> extends ArrayAdapter<T> {
	private List<T> objects;
	private final CanFillView<T> viewFiller;

	public ComplexViewArrayAdapter(Context context, int titleTextId, int resource, List<T> objects, CanFillView<T> viewFiller) {
		super(context, titleTextId, resource, objects);

        this.objects = objects;
		this.viewFiller = viewFiller;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
        View view = super.getView(position, convertView, parent);
        
        viewFiller.fill(view, objects.get(position));
        
        return view;
	}
}
