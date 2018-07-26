package com.restfultransfer.data;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Vector;

import org.apache.commons.dbutils.DbUtils;

public abstract class LongIdObjectDAO<T> extends H2Connector {
	
	public abstract String TableName(); 
	
	protected abstract T ObjectByResultSet(ResultSet result) throws SQLException;
	
	protected T Get(Connection connection, long Id, boolean forUpdate) throws SQLException {
		PreparedStatement sqlStatement = null;
		ResultSet result = null;
		try {
			if (forUpdate)
				sqlStatement = connection.prepareStatement("SELECT * FROM " + TableName() + " WHERE Id = ? FOR UPDATE");
			else
				sqlStatement = connection.prepareStatement("SELECT * FROM " + TableName() + " WHERE Id = ?");
			sqlStatement.setLong(1, Id);
			result = sqlStatement.executeQuery();
			
			return ObjectByResultSet(result);
		} finally {
			DbUtils.closeQuietly(sqlStatement);
			DbUtils.closeQuietly(result);
		}
	}

	public T Get(long Id) throws Exception {
		Connection connection = null;
		try {
			connection = getConnection();
			return Get(connection, Id, false);
		} catch (SQLException e) {
			throw new Exception("Can't get object from table " + TableName(), e);
		} finally {
			DbUtils.closeQuietly(connection);
		}
	}

	protected Vector<T> GetAll(WhereField where) throws Exception {
		Connection connection = null;
		PreparedStatement sqlStatement = null;
		ResultSet result = null;
		try {
			connection = getConnection();
			if (where != null)
			{
				sqlStatement = connection.prepareStatement("SELECT * FROM " + TableName() + " WHERE " + where.FieldName + " = ?");
				where.setField(1, sqlStatement);
			}
			else
				sqlStatement = connection.prepareStatement("SELECT * FROM " + TableName());
			result = sqlStatement.executeQuery();
			
			Vector<T> objects = new Vector<T>();
			T obj = ObjectByResultSet(result);
			while (obj != null)
			{
				objects.add(obj);
				obj = ObjectByResultSet(result);
			}
			return objects;
		} catch (SQLException e) {
			throw new Exception("Can't get objects from table " + TableName(), e);
		} finally {
			DbUtils.closeQuietly(connection, sqlStatement, result);
		}
	}

	public Vector<T> GetAll() throws Exception {
		return GetAll(null);
	}
}
