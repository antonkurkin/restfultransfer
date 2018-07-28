package com.restfultransfer.data;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.apache.commons.dbutils.DbUtils;

import java.lang.String;
import java.util.Vector;

public class ClientDAO extends LongIdObjectDAO<Client> {
	@Override
	public String TableName() { return "Clients"; }
	
	@Override
	protected Client ObjectByResultSet(ResultSet result) throws SQLException {
		if (!result.next())
			return null;
		return new Client(
				result.getLong("Id"),
				result.getString("Name"),
				result.getBoolean("Active"),
				result.getTimestamp("Created")
				);
	}

	class ValuesFieldsClient extends ValuesFields {
		final String[] fieldNames = {"Name"};
		final String name;
		
		public ValuesFieldsClient(String name)
		{
			this.name = name;
		}
		
		@Override
		String[] FieldNames() { return fieldNames; }
		
		@Override
		void SetValues(PreparedStatement sqlStatement) throws SQLException {
			sqlStatement.setString(1, name);
		}
	}
	
	public Client Create(String name) throws SQLException {
		return Create(new ValuesFieldsClient(name));
	}

	private class WhereString extends WhereField {
		String s;
		public WhereString(String FieldName, String s) {
			super(FieldName);
			this.s = s;
		}
		void SetField(int n, PreparedStatement sqlStatement) throws SQLException { sqlStatement.setString(n, s); }
	}

	public int ChangeName(long clientId, String newName) throws SQLException {
		Connection connection = null;
		try {
			connection = getConnection();
			WhereString newNameSetter = new WhereString("Name", newName);
			return ChangeField(connection, clientId, newNameSetter);
		} finally {
			DbUtils.closeQuietly(connection);
		}
	}

	public int SetActive(long clientId, boolean active) throws SQLException {
		Connection connection = null;
		try {
			connection = getConnection();
			WhereBoolean activeSetter = new WhereBoolean("Active", active);
			return ChangeField(connection, clientId, activeSetter);
		} finally {
			DbUtils.closeQuietly(connection);
		}
	}

	public Vector<Client> GetAllByName(String name) throws SQLException {
		WhereString whereName = new WhereString("Name", name);
		return GetAll(whereName);
	}
}
