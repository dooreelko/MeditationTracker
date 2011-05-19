package com.meditationtracker.persistence.helpers;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.Column;
import javax.persistence.Id;

import android.provider.BaseColumns;

public class ReflectionHelper {

	public static Map<String, Field> getPersistenceColumnNamesForClass(Class<?> clazz) {
		Map<String, Field> result = new HashMap<String, Field>();
		for (Field f : getPersistenceColumnsForClass(clazz))
		{
			Column columnAnn = f.getAnnotation(Column.class);
			if (columnAnn != null)
				result.put(columnAnn.name(), f);
			else
				if (f.getAnnotation(Id.class) != null)
					result.put(BaseColumns._ID, f);
		}
		
		return result;
	}
	
	protected static List<Field> getPersistenceColumnsForClass(Class<?> clazz) {
		List<Field> result = new ArrayList<Field>();
		for (Field f : clazz.getFields()) {
			if (f.getAnnotation(Column.class) != null || f.getAnnotation(Id.class) != null)
			{
				result.add(f);
			}
		}
		
		return result;
	}

}
