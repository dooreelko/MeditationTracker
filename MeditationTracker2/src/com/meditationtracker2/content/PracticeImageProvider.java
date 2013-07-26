package com.meditationtracker2.content;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashMap;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.MatrixCursor;
import android.net.Uri;
import android.os.Environment;
import android.os.ParcelFileDescriptor;
import android.provider.OpenableColumns;

import com.meditationtracker2.R;

public final class PracticeImageProvider extends ContentProvider {
	public static final String URI_PREFIX = "content://com.meditationtracker2.images";

	private static HashMap<String, Integer> sysPaths = new HashMap<String, Integer>(){
		private static final long serialVersionUID = -4477167023060363542L;
		{
			put("content://com.meditationtracker2.images/refuge", R.drawable.refuge); 
			put("content://com.meditationtracker2.images/diamondMind", R.drawable.diamond_mind);
			put("content://com.meditationtracker2.images/mandalaOffering", R.drawable.mandala_offering);
			put("content://com.meditationtracker2.images/guruYoga", R.drawable.guru_yoga);
			put("content://com.meditationtracker2.images/sixteenth_karmapa", R.drawable.sixteenth_karmapa); 
		}
	};

	
	@Override
	public String getType(Uri uri) {
		return android.provider.MediaStore.Images.Media.CONTENT_TYPE;
	}

	@Override
	public String[] getStreamTypes(Uri uri, String mimeTypeFilter) {
		return mimeTypeFilter.equals("image/*") || mimeTypeFilter.equals("image/png") ? new String[] { "image/png" }
				: null;
	}

	@Override
	public ParcelFileDescriptor openFile(Uri uri, String mode) throws FileNotFoundException
	{
		File file = buildFileFromUri(uri);

		ParcelFileDescriptor parcel = ParcelFileDescriptor.open(file, ParcelFileDescriptor.MODE_READ_ONLY);
		return parcel;
	}
	
	private File buildFileFromUri(Uri uri) {
		File file = new File(uri.getPath());
		Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
		File result = new File(getContext().getFilesDir() + File.separator + file.getAbsolutePath());

		if (sysPaths.containsKey(uri.toString()) && !file.exists()) {
			ensureSystemFile(uri, result);
		}
		
		return result;
	}

	protected void ensureSystemFile(Uri uri, File file) {
		int resId = sysPaths.get(uri.toString()); //TODO: security. no content://blabla/../../../some.file
		InputStream inputRawResource = this.getContext().getResources().openRawResource(resId);
		
		try {
			FileOutputStream fs = new FileOutputStream(file);

			copyLarge(inputRawResource, fs);
			inputRawResource.close();
			fs.close();

		} catch (IOException e) {
			//TODO: reporter.error("Error extracting system resource.", e);
		}
	}
	
	private static final int DEFAULT_BUFFER_SIZE = 1024 * 4;

	private static long copyLarge(InputStream input, OutputStream output)
			throws IOException {
		byte[] buffer = new byte[DEFAULT_BUFFER_SIZE];
		long count = 0;
		int n = 0;
		while (-1 != (n = input.read(buffer))) {
			output.write(buffer, 0, n);
			count += n;
		}
		return count;
	}
	

	@Override
	public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs,
			String sortOrder) {
		return getCursorForFiles(uri.getPath());
	}

	/* Return a cursor for files in the specified path */
	private Cursor getCursorForFiles(String path) {
		/* _id doesn't really mean anything but the system really wants it */
		String[] columns = {
				"_id",
				OpenableColumns.DISPLAY_NAME,
				OpenableColumns.SIZE,
				"_data"
		};

		MatrixCursor c = new MatrixCursor(columns);

		File baseDir = new File(path);
		if (baseDir.isDirectory()) {
			File[] files = baseDir.listFiles();
			int id = 0;
			for (File file : files) {
				addRow(c, file, id, file.getAbsolutePath());
				id++;
			}
		} else if (baseDir.isFile()) {
			addRow(c, baseDir, 0, baseDir.getAbsolutePath());
		}

		return c;
	}

	/* Add a row to the matrix cursor */
	private void addRow(MatrixCursor cursor, File file, int id, String path) {
		String fileName = file.getName();
		int fileSize = (int) file.length();
		cursor.addRow(new Object[] { id, fileName, fileSize, path });
	}
	
	@Override
	public boolean onCreate() {
		return true;
	}

	@Override
	public Uri insert(Uri uri, ContentValues values) {
		return null;
	}

	@Override
	public int delete(Uri uri, String selection, String[] selectionArgs) {
		return 0;
	}

	@Override
	public int update(Uri uri, ContentValues values, String selection,
			String[] selectionArgs) {
		return 0;
	}

}
