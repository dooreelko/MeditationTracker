package com.meditationtracker2.content;

import com.nostra13.universalimageloader.core.ImageLoader;

import android.net.Uri;
import android.widget.ImageView;
import doo.bandera.SimpleViewUpdater;

public class PracticeViewUpdater extends SimpleViewUpdater {
	private ImageLoader imageLoader = ImageLoader.getInstance();

	@Override
	public void updateImageView(ImageView v, Uri uri) {
		imageLoader.displayImage(uri.toString(), v);
	}
}
