package com.meditationtracker2;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import android.annotation.SuppressLint;
import android.app.Application;
import android.content.Context;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.Point;
import android.view.Display;
import android.view.WindowManager;
import android.widget.Toast;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;

public class MeditationTrackerApplication extends Application {
	@Override
    public void onCreate() {
        super.onCreate();

		WindowManager winman = (WindowManager) getSystemService(Context.WINDOW_SERVICE);
		Display defaultDisplay = winman.getDefaultDisplay();
		Point outSize = new Point();
		defaultDisplay.getSize(outSize);
		int biggestScreenDimension = Math.max(outSize.x, outSize.y);

        ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(getApplicationContext())
        	.denyCacheImageMultipleSizesInMemory()
        	.discCacheFileCount(20)
        	.discCacheExtraOptions(biggestScreenDimension, biggestScreenDimension, CompressFormat.JPEG, 90, null)
        	.defaultDisplayImageOptions(
        			new DisplayImageOptions.Builder()
        			.cacheInMemory(false)
        			.cacheOnDisc(true)
        			.showImageForEmptyUri(R.drawable.infinite_knot)
        			.showImageOnFail(R.drawable.infinite_knot)
        			.showStubImage(R.drawable.infinite_knot)
        			.displayer(new FadeInBitmapDisplayer(300))
        			.build())
            .build();
        ImageLoader.getInstance().init(config);
        
        copyDbFile();
    }

	private void copyDbFile() {
		try {
			copyFile(new FileInputStream("/data/data/com.meditationtracker2/databases/MediTracker"), new File("/mnt/sdcard/mtrk.db"));
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			Toast.makeText(this, "Error dumping", Toast.LENGTH_LONG).show();
		}
	}

	private static final int DEFAULT_BUFFER_SIZE = 1024 * 100;

	private void copyFile(InputStream input, File target) {
		byte[] buffer = new byte[DEFAULT_BUFFER_SIZE];
		int n = 0;
		try {
			FileOutputStream output = new FileOutputStream(target);
			while (-1 != (n = input.read(buffer))) {
				output.write(buffer, 0, n);
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			Toast.makeText(getBaseContext(), e.toString(), Toast.LENGTH_LONG).show();
			e.printStackTrace();
		}

	}

}
