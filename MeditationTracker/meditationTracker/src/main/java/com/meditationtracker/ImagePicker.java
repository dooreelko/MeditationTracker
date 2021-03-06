package com.meditationtracker;

import java.io.File;


import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.DialogInterface.OnClickListener;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;

import com.meditationtracker.util.Util;

public class ImagePicker extends BaseActivity
{
	public static final String TAKE_PICTURE = "take-picture";

	private static final String cropFileName = "crop.jpg";
	
	private static final int SELECT_IMAGE = 0;
	private static final int TAKE_PHOTO = 1;
	private static final int REQUEST_CROP_PHOTO = 2;

	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		
		if (getIntent().getBooleanExtra(TAKE_PICTURE, false))
		{
			File tempFile = getCropFile();
			if (tempFile.exists()){
				tempFile.delete();
				Log.d("MTRK", "Temp file should be gone now.");
			}
			startActivityForResult(new Intent(MediaStore.ACTION_IMAGE_CAPTURE).putExtra(MediaStore.EXTRA_OUTPUT, getCropUri()), TAKE_PHOTO);
		}
		else 
		{
			startActivityForResult(new Intent(Intent.ACTION_PICK).setType("image/*"), SELECT_IMAGE);
		}
	}

	private File getCropFile() {
		return new File(Util.getDataPath(), cropFileName);
	}
	
	private Uri getCropUri()
	{
		return Uri.fromFile(getCropFile());
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data)
	{
		super.onActivityResult(requestCode, resultCode, data);
		
		if (resultCode != RESULT_OK)
		{
			setResult(Activity.RESULT_CANCELED);
			finish();
			return;
		}
		
		Uri uri = null;
		
		switch (requestCode) {
		case TAKE_PHOTO:
			if (getCropFile().exists()) {
				uri = getCropUri();/*
				Log.d("MTRK", "Got good photo file in temp " + uri);
				
				generateCropFileName();
				cropFile = getCropFile();
				File temp_crop = new File(uri.toString());
				if (temp_crop.renameTo(cropFile)) {
					uri = Uri.fromFile(cropFile);
					Log.d("MTRK", "Renamed temp to crop " + uri);
				}*/
			}


		case SELECT_IMAGE:
			if (uri == null && data != null) {
				uri = data.getData();
				Log.d("MTRK", "Using data from intent(pick or fallbackto thumb) " + uri);
			}

            setResult(Activity.RESULT_OK, new Intent(data));
            finish();
//	        cropImage(uri);
			break;
		case REQUEST_CROP_PHOTO:
			File cropFile = getCropFile();
			if (cropFile != null && cropFile.exists())
			{
				cropFile.delete();
				Log.d("MTRK", "Removed temp crop source file");
			}
			
			setResult(Activity.RESULT_OK, new Intent(data));
			finish();
			break;
		}
		
	}

	private void cropImage(Uri imgUri)
	{
		if (imgUri == null) {
			setResult(Activity.RESULT_CANCELED);
			Util.showWhateverError(this, "The image disappeared. Impermanence.", new OnClickListener()
			{
				@Override
				public void onClick(DialogInterface dialog, int which)
				{
					finish();
				}
			});
			return;
		}
		
		Log.d("MTRK", "Will crop " + imgUri);
        Intent intent = new Intent("com.android.camera.action.CROP", imgUri);
        intent.setDataAndType(imgUri, "image/*");

        intent.putExtra("crop", true);
        intent.putExtra("scale", true);
        intent.putExtra("noFaceDetection", true);
        intent.putExtra("return-data", true);

        intent.putExtra("aspectX", 150);
        intent.putExtra("aspectY", 196);
        intent.putExtra("outputX", 150);
        intent.putExtra("outputY", 196);
        startActivityForResult(intent, REQUEST_CROP_PHOTO);
	}

}
