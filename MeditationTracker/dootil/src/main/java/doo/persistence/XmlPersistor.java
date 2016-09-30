package doo.persistence;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;

import org.simpleframework.xml.core.Persister;

public class XmlPersistor {
	public static <T> T loadFrom(Class<? extends T> clazz, String fileName) throws Exception {
		InputStream is = new FileInputStream(fileName);
		return loadFrom(clazz, is);
	}

	public static <T> T loadFrom(Class<? extends T> clazz, InputStream openFileInput) throws Exception {
		Persister ser = new Persister();
		return ser.read(clazz, openFileInput);
	}

	public static <T> void saveTo(T instance, String fileName) throws Exception {
		OutputStream os = new FileOutputStream(fileName);
		saveTo(instance, os);
	}

	public static <T> void saveTo(T instance, OutputStream os) throws Exception {
		Persister ser = new Persister();
		ser.write(instance, os);
	}
}
