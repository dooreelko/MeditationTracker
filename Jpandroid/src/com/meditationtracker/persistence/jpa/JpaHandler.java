package com.meditationtracker.persistence.jpa;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.apache.commons.dbutils.ResultSetHandler;


public class JpaHandler<T extends Object> implements ResultSetHandler<T>{

	public T handle(ResultSet row) throws SQLException {
		Entity info = EntityManager.getEntityInfo(row);
		
		T result;
		try {
			result = info.createInstance(row);
			
			return result;
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			
			return null;
		} 

	}

}
