package com.meditationtracker;

import java.io.File;

import doo.util.Util;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.DialogInterface.OnClickListener;
import android.net.Uri;
import android.os.Bundle;

public class ImagePicker extends VerboseActivity
{
	private final String SDCARD_PATH = "/sdcard/"; //TODO: make it sdcard independent
	private final String TEMP_PATH = SDCARD_PATH + "temp_picture.jpeg"; 
	public static final String TAKE_PICTURE = "take-picture";

	private String cropFileName;
	
	private static final int SELECT_IMAGE = 0;
	private static final int TAKE_PHOTO = 1;
	private static final int REQUEST_CROP_PHOTO = 2;

	//private Uri imgCaptureUri;
	
	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		
		if (getIntent().getBooleanExtra(TAKE_PICTURE, false))
		{
			File tempFile = getTempFile();
			if (tempFile.exists()){
				tempFile.delete();
			}
			startActivityForResult(new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE).putExtra(android.provider.MediaStore.EXTRA_OUTPUT, getTempUri()/*android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI*/), TAKE_PHOTO);
		}
		else 
		{
			startActivityForResult(new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.INTERNAL_CONTENT_URI), SELECT_IMAGE);
		}
	}

	private Uri getTempUri()
	{
		return Uri.fromFile(getTempFile());
	}

	private File getTempFile()
	{
		return new File(TEMP_PATH);
	}
	
	private void generateCropFileName(){
		cropFileName = Math.random() + ".jpg";
	}
	
	private File getCropFile() {
		return new File(SDCARD_PATH + cropFileName);
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data)
	{
		super.onActivityResult(requestCode, resultCode, data);
		
		File cropFile = getCropFile();
		if (cropFile.exists())
		{
			cropFile.delete();
		}

		if (resultCode != RESULT_OK)
		{
			setResult(Activity.RESULT_CANCELED);
			finish();
			return;
		}
		
		Uri uri = null;
		switch (requestCode) {
		case TAKE_PHOTO:
			if ((data == null || (uri = data.getData()) == null) && getTempFile().exists()) {
				uri = getTempUri();
			}
	        //cropImage(uri);
	        //break;
		case SELECT_IMAGE:
			generateCropFileName();
	        cropImage(uri);
			break;
		case REQUEST_CROP_PHOTO:
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
				public void onClick(DialogInterface dialog, int which)
				{
					ImagePicker.this.finish();
				}
			});
			return;
		}
		
		File cropFile = getCropFile();
		getTempFile().renameTo(cropFile);
		
		imgUri = Uri.fromFile(cropFile);
		
        Intent intent = new Intent("com.android.camera.action.CROP", imgUri);
        intent.setDataAndType(imgUri, "image/*");
/*        if (myIntent.getStringExtra("mimeType") != null) {
            intent.setDataAndType(myIntent.getData(), myIntent.getStringExtra("mimeType"));
        }*/
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
