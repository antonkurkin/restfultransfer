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

	protected abstract class WhereField {
		public final String FieldName;
		public WhereField(String FieldName) {
			this.FieldName = FieldName;
		}
		abstract void SetField(int n, PreparedStatement sqlStatement) throws SQLException;
	}

	protected class WhereLong extends WhereField {
		long l;
		public WhereLong(String FieldName, long l) {
			super(FieldName);
			this.l = l;
		}
		void SetField(int n, PreparedStatement sqlStatement) throws SQLException { sqlStatement.setLong(n, l); }
	}

	protected class WhereBoolean extends WhereField {
		boolean b;
		public WhereBoolean(String FieldName, boolean b) {
			super(FieldName);
			this.b = b;
		}
		void SetField(int n, PreparedStatement sqlStatement) throws SQLException { sqlStatement.setBoolean(n, b); }
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

	protected int ChangeField(Connection connection, long id, WhereField change) throws SQLException {
		PreparedStatement sqlStatement = null;
		ResultSet result = null;
		try {
			connection = getConnection();
			sqlStatement = connection.prepareStatement("UPDATE " + TableName() + " SET " + change.FieldName + " = ? WHERE Id = ?");
			change.SetField(1, sqlStatement);
			sqlStatement.setLong(2, id);
			return sqlStatement.executeUpdate();
		} catch (SQLException e) {
			if (	e.getErrorCode() == org.h2.api.ErrorCode.REFERENTIAL_INTEGRITY_VIOLATED_PARENT_MISSING_1 ||
					e.getErrorCode() == org.h2.api.ErrorCode.CHECK_CONSTRAINT_VIOLATED_1 )
				return 0;
			throw new SQLException("Can't change field " + change.FieldName + " for object in table " + TableName() , e);
		} finally {
			DbUtils.closeQuietly(sqlStatement);
			DbUtils.closeQuietly(result);
		}
	}

	abstract class ValuesFields {
		public String GetRequest() { //build string "(Field1, Field2, ... FieldN) VALUES (?, ?, ... ?)"
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
			if (!result.next())
				return null;
			return Get(connection, result.getLong(1), false);
		} catch (SQLException e) {
			if (	e.getErrorCode() == org.h2.api.ErrorCode.REFERENTIAL_INTEGRITY_VIOLATED_PARENT_MISSING_1 ||
					e.getErrorCode() == org.h2.api.ErrorCode.CHECK_CONSTRAINT_VIOLATED_1 )
				return null;
			throw new SQLException("Can't create object in table " + TableName() , e);
		} finally {
			DbUtils.closeQuietly(connection, sqlStatement, result);
		}
	}
}
