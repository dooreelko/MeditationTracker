package com.meditationtracker;

import java.io.File;
import java.io.FileNotFoundException;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.ParcelFileDescriptor;

public class PracticeImageProvider extends ContentProvider
{
	private static final String NOT_SUPPORTED_BY_THIS_PROVIDER = "Not supported by this provider";
	static final String URI_PREFIX = "content://com.meditationtracker.imageProvider";

	public static String constructUri(String url)
	{
		Uri uri = Uri.parse(url);
		return uri.isAbsolute() ? url : URI_PREFIX + url;
	}

	@Override
	public ParcelFileDescriptor openFile(Uri uri, String mode) throws FileNotFoundException
	{
		File file = new File(uri.getPath());
		Context context = getContext();

		if (context != null)
		{
			file = new File(context.getFilesDir() + File.separator + file.getAbsolutePath());
		}

		/*
		//TODO: DO I WANT THIS AT ALL?
		 * NO
		
		String fileName = file.getName();
		if (!fileName.startsWith("img") || !fileName.endsWith(".jpeg") && !file.exists())
		{

			if (context != null)
			{
				try
				{
					Field f = R.drawable.class.getField(fileName);

					BitmapDrawable bitDrawable = (BitmapDrawable) context.getResources().getDrawable((Integer) f.get(null));
					FileOutputStream fos = context.openFileOutput(fileName, Context.MODE_PRIVATE);
					bitDrawable.getBitmap().compress(Bitmap.CompressFormat.JPEG, 95, fos);
					fos.close();
				} catch (Exception e)
				{
					e.printStackTrace();
					throw new FileNotFoundException("You dare not accessing file " + uri);
				}
			} else
				throw new FileNotFoundException("You dare not accessing file " + uri);
		}*/

		ParcelFileDescriptor parcel = ParcelFileDescriptor.open(file, ParcelFileDescriptor.MODE_READ_ONLY);
		return parcel;
	}

	@Override
	public int delete(Uri uri, String selection, String[] selectionArgs)
	{
		throw new UnsupportedOperationException(NOT_SUPPORTED_BY_THIS_PROVIDER);
	}

	@Override
	public String getType(Uri uri)
	{
		throw new UnsupportedOperationException(NOT_SUPPORTED_BY_THIS_PROVIDER);
	}

	@Override
	public Uri insert(Uri uri, ContentValues values)
	{
		throw new UnsupportedOperationException(NOT_SUPPORTED_BY_THIS_PROVIDER);
	}

	@Override
	public boolean onCreate()
	{
		return true;
	}

	@Override
	public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder)
	{
		throw new UnsupportedOperationException(NOT_SUPPORTED_BY_THIS_PROVIDER);
	}

	@Override
	public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs)
	{
		throw new UnsupportedOperationException(NOT_SUPPORTED_BY_THIS_PROVIDER);
	}

}
