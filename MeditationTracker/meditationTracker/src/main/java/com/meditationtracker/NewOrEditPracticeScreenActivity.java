package com.meditationtracker;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.net.Uri;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.meditationtracker.R.drawable;
import com.meditationtracker.R.id;
import com.meditationtracker.R.layout;
import com.meditationtracker.R.string;
import com.meditationtracker.controls.MenuBar;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import doo.util.Pair;
import doo.util.Util;

public class NewOrEditPracticeScreenActivity extends BaseActivity
{
	private static final String THUMBNAIL_PREFIX = "th_";

	private static final int SELECT_IMAGE = 0;

	private static final String IMAGE_URL = "ImageUrl";
	private static final String THUMB_URL = "ThumbUrl";
	
	private String imgUrl;
	private String thumbUrl;

	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);

		setResult(RESULT_CANCELED);
		
		setContentView(layout.new_meditation);

		Toast.makeText(this, getString(string.tapImgToChoose), Toast.LENGTH_SHORT).show();

		View v = findViewById(id.practiceImage);
		v.setOnTouchListener(imgTouchListener);
		registerForContextMenu(v);
		
		String title = getString(string.newPractice);
		
		Bundle extras = getIntent().getExtras();
		if (extras.containsKey(ExtraKeys.ID) && extras.getLong(ExtraKeys.ID) != -1){
			
			title = getString(string.editPractice);
			
			thumbUrl = extras.getString(ExtraKeys.ThumbURL);
			imgUrl = extras.getString(ExtraKeys.ImgURL);
			
			updatePracticeImage();
			
			((TextView)findViewById(id.textPracticeName)).setText(extras.getString(ExtraKeys.Title));
			((TextView)findViewById(id.textRepetitionCount)).setText(String.valueOf(extras.getInt(ExtraKeys.TotalCount)));
			((TextView)findViewById(id.textCompletedRepetitions)).setText(String.valueOf(extras.getInt(ExtraKeys.CurrentCount)));
			
			int malaSize = extras.getInt(ExtraKeys.MalaSize);
			if (malaSize != 0)
			{
				((TextView)findViewById(id.textMalaSize)).setText(String.valueOf(malaSize));
			}
		}
		
		findViewById(id.saveButton).setOnClickListener(saveClicked);
		((MenuBar)findViewById(id.menuBar)).setText(title);
	}

	private void updatePracticeImage() {
		View v = findViewById(id.practiceImage);

		Pair<Boolean, Long> parsed = Util.tryParse(imgUrl);
		if (parsed._1)
			((ImageView)v).setImageResource(parsed._2.intValue());
		else
			if (imgUrl != null)
				((ImageView)v).setImageURI(Uri.parse(imgUrl));
	}
	
	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);

		outState.putString(IMAGE_URL, imgUrl);
		outState.putString(THUMB_URL, thumbUrl);
	}

	@Override
	protected void onRestoreInstanceState(Bundle savedInstanceState) {
		super.onRestoreInstanceState(savedInstanceState);

		imgUrl = savedInstanceState.getString(IMAGE_URL);
		thumbUrl = savedInstanceState.getString(THUMB_URL);

		updatePracticeImage();
	}
	

	private final OnTouchListener imgTouchListener = new OnTouchListener()
	{
		@Override
		public boolean onTouch(View v, MotionEvent event)
		{
			if (event.getAction() == MotionEvent.ACTION_UP){
				
				openContextMenu(v);
			}
			return true;
		}
	};

	@Override
	public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo)
	{
		super.onCreateContextMenu(menu, v, menuInfo);
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.image_source, menu);
		menu.setHeaderTitle(string.imageSourceTitle);
	}

	@Override
	public boolean onContextItemSelected(MenuItem item)
	{
		switch (item.getItemId()) {
		case id.browseImage:
			startActivityForResult(new Intent(this, ImagePicker.class).putExtra(ImagePicker.TAKE_PICTURE, false), SELECT_IMAGE);
			return true;
		case id.takePhoto:
			startActivityForResult(new Intent(this, ImagePicker.class).putExtra(ImagePicker.TAKE_PICTURE, true), SELECT_IMAGE);
		return true;
		}
		
		return super.onContextItemSelected(item);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data)
	{
		super.onActivityResult(requestCode, resultCode, data);

		if (resultCode != RESULT_OK)
			return;
		
		ImageView v = (ImageView)findViewById(id.practiceImage);
		if (requestCode == SELECT_IMAGE) {
            Bundle extras = data.getExtras();
            if (extras != null) {
                Bitmap photo = extras.getParcelable("data");
                if (photo != null) {
                    FileOutputStream fos;
					try
					{
						File mainImageFile = File.createTempFile("img", ".png", getFilesDir());
						String practiceImageName = mainImageFile.getName();
						
						fos = openFileOutput(practiceImageName, Context.MODE_PRIVATE);
	                    photo.compress(CompressFormat.PNG, 95, fos);
	                    fos.close();
	                    
	                    fos = openFileOutput(THUMBNAIL_PREFIX + practiceImageName, Context.MODE_PRIVATE);
	                    Bitmap.createScaledBitmap(photo, 35, 46, false).compress(CompressFormat.JPEG, 95, fos);
	                    fos.close();
	                    
	                    imgUrl = PracticeImageProvider.URI_PREFIX + File.separator + practiceImageName;
						thumbUrl = PracticeImageProvider.URI_PREFIX + File.separator + THUMBNAIL_PREFIX + practiceImageName;
						v.setImageURI(Uri.parse(imgUrl));
						
						updateResult();
					} catch (FileNotFoundException e)
					{
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (IOException e)
					{
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
                }
            }

		}
	}
	
	private final OnClickListener saveClicked = new OnClickListener()
	{
		@Override
		public void onClick(View v)
		{
			updateResult();
			finish();
		}
	};


	private void updateResult()
	{
		setResult(Activity.RESULT_OK, 
				new Intent().
				putExtra(ExtraKeys.ImgURL, imgUrl == null ? String.valueOf(drawable.karmapa) : imgUrl).
				putExtra(ExtraKeys.ThumbURL, thumbUrl == null ? String.valueOf(drawable.icon_karmapa) : thumbUrl).
				putExtra(ExtraKeys.Title, ((TextView)findViewById(id.textPracticeName)).getText().toString()).
				putExtra(ExtraKeys.TotalCount, ((TextView)findViewById(id.textRepetitionCount)).getText().toString()).
				putExtra(ExtraKeys.CurrentCount, ((TextView)findViewById(id.textCompletedRepetitions)).getText().toString()).
				putExtra(ExtraKeys.MalaSize, ((TextView)findViewById(id.textMalaSize)).getText().toString()));
	}
	
}
