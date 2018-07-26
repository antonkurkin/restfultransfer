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
	
	public Client Create(String name) throws Exception {
		return Create(new ValuesFieldsClient(name));
	}

	public int ChangeName(Client client, String newName) throws Exception {
		Connection connection = null;
		PreparedStatement sqlStatement = null;
		ResultSet result = null;
		try {
			connection = getConnection();
			sqlStatement = connection.prepareStatement("UPDATE Clients SET Name = ? WHERE Id = ?");
			sqlStatement.setString(1, newName);
			sqlStatement.setLong(2, client.Id());
			return sqlStatement.executeUpdate();
		} catch (SQLException e) {
			throw new Exception("Can't change Client's name in DB", e);
		} finally {
			DbUtils.closeQuietly(connection, sqlStatement, result);
		}
	}
	
	public int SetActive(long clientId, boolean active) throws Exception {
		Connection connection = null;
		PreparedStatement sqlStatement = null;
		ResultSet result = null;
		try {
			connection = getConnection();
			sqlStatement = connection.prepareStatement("UPDATE Clients SET Active = ? WHERE Id = ?");
			sqlStatement.setBoolean(1, active);
			sqlStatement.setLong(2, clientId);
			return sqlStatement.executeUpdate();
		} catch (SQLException e) {
			throw new Exception("Can't change active state of Client in DB", e);
		} finally {
			DbUtils.closeQuietly(connection, sqlStatement, result);
		}
	}

	public class WhereString extends WhereField {
		String s;
		public WhereString(String FieldName, String s) {
			super(FieldName);
			this.s = s;
		}
		void SetField(int n, PreparedStatement sqlStatement) throws SQLException { sqlStatement.setString(n, s); }
	}

	public Vector<Client> GetAllByName(String name) throws Exception {
		WhereString whereName = new WhereString("Name", name);
		return GetAll(whereName);
	}
}
