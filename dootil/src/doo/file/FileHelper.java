package doo.file;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import android.os.Environment;

public class FileHelper {
	public static void copy(File src, File dst) throws IOException {
		InputStream in = new FileInputStream(src);
		OutputStream out = new FileOutputStream(dst);
		// Transfer bytes from in to out
		byte[] buf = new byte[1024];
		int len;
		while ((len = in.read(buf)) > 0) {
			out.write(buf, 0, len);
		}
		in.close();
		out.flush();
		out.close();
	}

	public static void quickWriteToFile(File name, String text) throws IOException{
		FileWriter out = new FileWriter(name);
		out.write(text);
		
		out.flush();
		out.close();
	}
	
	public static void quickWriteToSDRoot(String fileName, String text) throws IOException {
		quickWriteToFile(new File(Environment.getExternalStorageDirectory() + File.separator + fileName), text);
	}
}
