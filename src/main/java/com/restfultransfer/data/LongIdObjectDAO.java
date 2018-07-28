package com.restfultransfer.data;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
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

	public T Get(long Id) throws SQLException {
		Connection connection = null;
		try {
			connection = getConnection();
			return Get(connection, Id, false);
		} catch (SQLException e) {
			throw new SQLException("Can't get object from table " + TableName(), e);
		} finally {
			DbUtils.closeQuietly(connection);
		}
	}

	protected Vector<T> GetAll(WhereField where) throws SQLException {
		Connection connection = null;
		PreparedStatement sqlStatement = null;
		ResultSet result = null;
		try {
			connection = getConnection();
			if (where != null)
			{
				sqlStatement = connection.prepareStatement("SELECT * FROM " + TableName() + " WHERE " + where.FieldName + " = ?");
				where.SetField(1, sqlStatement);
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
			throw new SQLException("Can't get objects from table " + TableName(), e);
		} finally {
			DbUtils.closeQuietly(connection, sqlStatement, result);
		}
	}

	public Vector<T> GetAll() throws SQLException {
		return GetAll(null);
	}

	abstract class ValuesFields {
		public String GetRequest() {
			StringBuilder builderNames = new StringBuilder();
			StringBuilder builderValues = new StringBuilder();
			builderNames.append('(');
			builderValues.append('(');
			String separator = "";
			for (String field : FieldNames())
			{
				builderNames.append(separator);
				builderValues.append(separator);
				builderNames.append(field);
				builderValues.append('?');
				separator = ", ";
			}
			builderNames.append(')');
			builderValues.append(')');
			builderNames.append(" VALUES ");
			builderNames.append(builderValues);
			return builderNames.toString();
		}

		abstract String[] FieldNames();
		abstract void SetValues(PreparedStatement sqlStatement) throws SQLException;
	}
	
	protected T Create(ValuesFields fields) throws SQLException {
		Connection connection = null;
		PreparedStatement sqlStatement = null;
		ResultSet result = null;
		try {
			connection = getConnection();
			sqlStatement = connection.prepareStatement("INSERT INTO " + TableName() + " " + fields.GetRequest(), Statement.RETURN_GENERATED_KEYS);
			fields.SetValues(sqlStatement);
			int rowCount = sqlStatement.executeUpdate();
			if (rowCount == 0)
				return null;
			result = sqlStatement.getGeneratedKeys();
			return Get(connection, result.getLong(1), false);
		} catch (SQLException e) {
			throw new SQLException("Can't create object in table " + TableName() , e);
		} finally {
			DbUtils.closeQuietly(connection, sqlStatement, result);
		}
	}
}
