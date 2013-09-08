package com.meditationtracker2.content;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.Display;
import android.view.WindowManager;

public class ImageCache {

	private static final int IGNORE_QUALITY = 90;

	@SuppressWarnings("deprecation")
	public static void ensureResizedImage(Context context, File source, File target) {
		if (target.exists()) {
			return;
		}
		
		BitmapFactory.Options options = new BitmapFactory.Options();
		options.inJustDecodeBounds = true;

		BitmapFactory.decodeFile(source.getPath(), options);
		int imageHeight = options.outHeight;

		WindowManager winman = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
		Display defaultDisplay = winman.getDefaultDisplay();
		int biggestScreenDimension = Math.max(defaultDisplay.getWidth(), defaultDisplay.getHeight());
		
		options.inSampleSize = imageHeight / biggestScreenDimension; // we fit height
		options.inJustDecodeBounds = false;
		Bitmap decodedFile = BitmapFactory.decodeFile(source.getPath(), options);
		OutputStream outstream;
		try {
			outstream = new FileOutputStream(target);
			decodedFile.compress(Bitmap.CompressFormat.PNG, IGNORE_QUALITY, outstream);
			outstream.close();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
	}

}
