package com.meditationtracker2;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;

import android.app.Application;
import android.graphics.Bitmap;

public class MeditationTrackerApplication extends Application {
	@Override
    public void onCreate() {
        super.onCreate();

        // Create global configuration and initialize ImageLoader with this configuration
        ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(getApplicationContext())
        	.denyCacheImageMultipleSizesInMemory()
        	.discCacheFileCount(20)
        	.defaultDisplayImageOptions(
        			new DisplayImageOptions.Builder()
        			.cacheInMemory(true)
        			.cacheOnDisc(true)
        			.showImageForEmptyUri(R.drawable.infinite_knot)
        			.showImageOnFail(R.drawable.infinite_knot)
        			.showStubImage(R.drawable.infinite_knot)
        			.bitmapConfig(Bitmap.Config.ARGB_4444)
        			.build())
            .build();
        ImageLoader.getInstance().init(config);
    }
}
