package com.meditationtracker2.content;

import android.view.View;

public interface ICanFillView<T> {
	void fill(View view, T with);
}