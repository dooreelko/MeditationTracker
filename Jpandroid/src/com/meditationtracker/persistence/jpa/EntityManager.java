package com.meditationtracker.persistence.jpa;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;


public class EntityManager {
	private static HashMap<Integer, Entity> entities;
	
	public static void registerEntity(Class<?> clazz) {
		Entity entity = new Entity(clazz);
		entities.put(entity.hashCode(), entity);
	}
	
	public static Entity getEntityInfo(ResultSet row) throws SQLException {
		return entities.get(Entity.buildHash(row));
	}
	
	

}
