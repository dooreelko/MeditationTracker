package com.meditationtracker.persistence.jpa;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import com.meditationtracker.persistence.helpers.ReflectionHelper;
import doo.util.functional.Collection;
import doo.util.functional.Folder;

public class Entity {
	private String hash;
	private Class<?> clazz = null;
	private Map<String, Field> columns;
	
	public Entity(Class<?> clazz) {
		this.clazz = clazz;
		hash = buildHash(clazz);
	}

	@SuppressWarnings("unchecked")
	public <T> T createInstance(ResultSet row) throws IllegalArgumentException, SecurityException, InstantiationException, IllegalAccessException, InvocationTargetException, NoSuchMethodException, SQLException {
		if (clazz == null)
			throw new IllegalArgumentException("Entity was initialized without a class, cannot create one.");
	
		return fillObject((T)clazz.getConstructor((Class[])null).newInstance(), row);
	}
	
	private <T> T fillObject(T obj, ResultSet row) throws SQLException, IllegalArgumentException, IllegalAccessException {
		ResultSetMetaData metaData = row.getMetaData();
		for (int x=0; x<metaData.getColumnCount(); x++) {
			Field f = columns.get(metaData.getColumnLabel(x));
			
			f.setAccessible(true);
			f.set(obj, row.getObject(x));
		}
		
		return obj;
	}
	
	@Override
	public int hashCode() {
		return hash.hashCode();
	}

	public static String buildHash(ResultSet row) throws SQLException {
		List<String> columns = new ArrayList<String>();
		
		ResultSetMetaData rowMetaData = row.getMetaData();
		for(int x = 0; x<rowMetaData.getColumnCount(); x++) {
			columns.add(rowMetaData.getColumnName(x));
		}
		
		return sortAndJoinForHash(columns);		
	}

	public String buildHash(Class<?> clazz) {
		columns = ReflectionHelper.getPersistenceColumnNamesForClass(clazz);
		List<String> colNames = new ArrayList<String>(columns.keySet());
		
		return sortAndJoinForHash(colNames);
	}
	
	private static String sortAndJoinForHash(List<String> list) {
		Collections.sort(list);
		return Collection.fold(list, "|", new Folder<String, String>() {
			public String fold(String accumulator, String current) {
				return accumulator += current + "|";
			}
	
		});
	}

}
