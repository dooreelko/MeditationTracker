package com.meditationtracker2.content;

import android.view.View;

public interface CanFillView<T> {
	void fill(View view, T with);
}