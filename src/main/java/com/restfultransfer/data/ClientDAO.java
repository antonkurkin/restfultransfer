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
	
	public Client Create(String name) throws Exception {
		Connection connection = null;
		PreparedStatement sqlStatement = null;
		ResultSet result = null;
		try {
			connection = getConnection();
			sqlStatement = connection.prepareStatement("INSERT INTO Clients (Name) VALUES (?)");
			sqlStatement.setString(1, name);
			int rowCount = sqlStatement.executeUpdate();
			if (rowCount == 0)
				throw new Exception("Client wasn't created in DB");
			result = sqlStatement.getResultSet();
			return ObjectByResultSet(result);
		} catch (SQLException e) {
			throw new Exception("Can't create client in DB", e);
		} finally {
			DbUtils.closeQuietly(connection, sqlStatement, result);
		}
	}

	public void ChangeName(Client client, String newName) throws Exception {
		Connection connection = null;
		PreparedStatement sqlStatement = null;
		ResultSet result = null;
		try {
			connection = getConnection();
			sqlStatement = connection.prepareStatement("UPDATE Clients SET Name = ? WHERE Id = ?");
			sqlStatement.setString(1, newName);
			sqlStatement.setLong(2, client.Id());
			int rowCount = sqlStatement.executeUpdate();
			if (rowCount == 0)
				throw new Exception("Client name wasn't changed");
		} catch (SQLException e) {
			throw new Exception("Can't change Client's name in DB", e);
		} finally {
			DbUtils.closeQuietly(connection, sqlStatement, result);
		}
	}
	
	public void SetActive(Client client, boolean active) throws Exception {
		Connection connection = null;
		PreparedStatement sqlStatement = null;
		ResultSet result = null;
		try {
			connection = getConnection();
			sqlStatement = connection.prepareStatement("UPDATE Clients SET Active = ? WHERE Id = ?");
			sqlStatement.setBoolean(1, active);
			sqlStatement.setLong(2, client.Id());
			int rowCount = sqlStatement.executeUpdate();
			if (rowCount == 0)
				throw new Exception("Client's active state wasn't changed in DB");
		} catch (SQLException e) {
			throw new Exception("Can't change active state of Client in DB", e);
		} finally {
			DbUtils.closeQuietly(connection, sqlStatement, result);
		}
		
		AccountDAO accDAO = new AccountDAO();
		if (!active)
		{
			WhereLong whereClient = new WhereLong("ClientId", client.Id());
			Vector<Account> accounts = accDAO.GetAll(whereClient);
			for (Account account : accounts)
				accDAO.SetActive(account, active);
		}
	}

	public class WhereString extends WhereField {
		String s;
		public WhereString(String FieldName, String s) {
			super(FieldName);
			this.s = s;
		}
		void setField(int n, PreparedStatement sqlStatement) throws SQLException { sqlStatement.setString(n, s); }
	}

	public Vector<Client> GetAllByName(String name) throws Exception {
		WhereString whereName = new WhereString("Name", name);
		return GetAll(whereName);
	}
}
