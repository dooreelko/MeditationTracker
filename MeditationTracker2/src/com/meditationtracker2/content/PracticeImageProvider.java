package com.meditationtracker2.content;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Random;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.MatrixCursor;
import android.net.Uri;
import android.os.Environment;
import android.os.ParcelFileDescriptor;
import android.provider.OpenableColumns;
import android.widget.Toast;

import com.meditationtracker2.Constants;
import com.meditationtracker2.R;

public final class PracticeImageProvider extends ContentProvider {
	private static final String MEDITATION_TRACKER_FOLDER = "MeditationTracker";
	private static HashMap<String, Integer> sysPaths = new HashMap<String, Integer>(){
		private static final long serialVersionUID = -4477167023060363542L;
		{
			//TODO: get that from application strings
			put(Constants.REFUGE_PNG, R.drawable.refuge); 
			put(Constants.DIAMOND_MIND_PNG, R.drawable.diamond_mind);
			put(Constants.MANDALA_OFFERING_PNG, R.drawable.mandala_offering);
			put(Constants.GURU_YOGA_PNG, R.drawable.guru_yoga);
			put(Constants.SIXTEENTH_KARMAPA_PNG, R.drawable.sixteenth_karmapa); 
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

		if (!file.exists()) { // fallback if file not found
			file = buildFileFromUri(Constants.buildScreenUri(Constants.SIXTEENTH_KARMAPA_PNG));
		}
		
		ParcelFileDescriptor parcel = ParcelFileDescriptor.open(file, ParcelFileDescriptor.MODE_READ_ONLY);
		return parcel;
	}
	
	private File buildFileFromUri(Uri uri) {
		File result = convertContentUriToFile(uri);

		Uri originalUri = uri.buildUpon().clearQuery().build();
		File originalFile = convertContentUriToFile(originalUri);
		
		if (sysPaths.containsKey(originalUri.getLastPathSegment()) && !originalFile.exists()) {
			getPicturesFolder().mkdirs();
			ensureSystemFile(originalUri, originalFile);
		}
		
		if (isQueryWithResize(uri)) {
			ImageCache.ensureResizedImage(getContext(), originalFile, result);
		}
		
		return result;
	}

	protected File convertContentUriToFile(Uri uri) {
		File file = new File(uri.getPath());
		File picturesFolder;
		
		if (isQueryWithResize(uri)) {
			picturesFolder = getCacheFolder();
		} else {
			picturesFolder = getPicturesFolder();
		}
		
		File result = new File(picturesFolder, file.getAbsolutePath());
		return result;
	}

	protected boolean isQueryWithResize(Uri uri) {
		return uri.getQueryParameter(Constants.SIZEHINT_KEY) != null;
	}

	private File getCacheFolder() {
		return getContext().getCacheDir();
	}

	private static File getPicturesFolder() {
		return new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), MEDITATION_TRACKER_FOLDER);
	}

	protected void ensureSystemFile(Uri uri, File file) {
		int resId = sysPaths.get(uri.getLastPathSegment()); //TODO: security. no content://blabla/../../../some.file
		InputStream inputRawResource = this.getContext().getResources().openRawResource(resId);
		
		copyFile(file, inputRawResource);
	}

	protected void copyFile(File file, InputStream inputStream) {
		try {
			FileOutputStream fs = new FileOutputStream(file);

			byte[] data = new byte[inputStream.available()];

			inputStream.read(data);
	        fs.write(data);

			inputStream.close();
			fs.close();

		} catch (Exception e) {
			Toast.makeText(getContext(), e.toString(), Toast.LENGTH_LONG).show();
			//TODO: reporter.error("Error extracting system resource.", e);
		}
	}
	
	@Override
	public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs,
			String sortOrder) {
		return getCursorForFiles(getPicturesFolder().getPath());
	}

	/* Return a cursor for files in the specified path */
	private Cursor getCursorForFiles(String path) {
		/* _id doesn't really mean anything but the system really wants it */
		String[] columns = {
				"_id",
				OpenableColumns.DISPLAY_NAME,
				OpenableColumns.SIZE,
				"_data",
				"mime_type"
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
		
		cursor.addRow(new Object[] { id, fileName, fileSize, path, "image/png" });
	}
	
	@Override
	public boolean onCreate() {
		return true;
	}

	@Override
	public Uri insert(Uri uri, ContentValues values) {
		File result = getUniqueNewFile(convertContentUriToFile(uri));
		
		try {
			Uri sourceUri = Uri.parse(values.getAsString(Constants.SOURCE_URL));
			InputStream stream = getContext().getContentResolver().openInputStream(sourceUri);
			copyFile(result, stream);
			return Uri.parse(Constants.MEDIA_PROVIDER_ROOT + result.getName());
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			Toast.makeText(getContext(), e.toString(), Toast.LENGTH_LONG).show();
		}

		return Uri.parse(Constants.SIXTEENTH_KARMAPA_PNG);
	}

	private File getUniqueNewFile(File result) {
		Random rnd = new Random();

		File folder = getPicturesFolder();
		while (result.exists()) {
			result = new File(folder, String.valueOf(rnd.nextInt(50000)));
		}
		return result;
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
