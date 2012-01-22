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
	public static final String URI_PREFIX = "content://com.meditationtracker.imageProvider";

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

		ParcelFileDescriptor parcel = ParcelFileDescriptor.open(file, ParcelFileDescriptor.MODE_READ_ONLY);
		return parcel;
	}

	@Override
	public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder)
	{
		throw new UnsupportedOperationException(NOT_SUPPORTED_BY_THIS_PROVIDER);
	}

	@Override
	public String getType(Uri uri)
	{
		return android.provider.MediaStore.Images.Media.CONTENT_TYPE;
	}

	@Override
	public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs)
	{
		throw new UnsupportedOperationException(NOT_SUPPORTED_BY_THIS_PROVIDER);
	}

	@Override
	public int delete(Uri uri, String selection, String[] selectionArgs)
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
}
