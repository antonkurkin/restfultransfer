package com.restfultransfer.data;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.apache.commons.dbutils.DbUtils;

import java.lang.String;

import java.util.Vector;

public class ClientDAO extends H2Connector {
	
	private static Client ClientByResultSet(ResultSet result) throws SQLException
	{
		if (!result.next())
			return null;
		return new Client(
				result.getLong("ClientId"),
				result.getString("Name"),
				result.getBoolean("Active"),
				result.getTimestamp("Created")
				);
	}
	
	static Client Get(long clientId) throws Exception {
		Connection connection = null;
		PreparedStatement sqlStatement = null;
		ResultSet result = null;
		try {
			connection = getConnection();
			sqlStatement = connection.prepareStatement("SELECT * FROM Clients WHERE ClientId = ?");
			sqlStatement.setLong(1, clientId);
			result = sqlStatement.executeQuery();
			
			return ClientByResultSet(result);
		} catch (SQLException e) {
			throw new Exception("Can't get account from DB", e);
		} finally {
			DbUtils.closeQuietly(connection, sqlStatement, result);
		}
	}
	
	public static Client Create(String name) throws Exception {
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
			return ClientByResultSet(result);
		} catch (SQLException e) {
			throw new Exception("Can't create client in DB", e);
		} finally {
			DbUtils.closeQuietly(connection, sqlStatement, result);
		}
	}

	public static void ChangeName(Client client, String newName) throws Exception {
		Connection connection = null;
		PreparedStatement sqlStatement = null;
		ResultSet result = null;
		try {
			connection = getConnection();
			sqlStatement = connection.prepareStatement("UPDATE Clients SET Name = ? WHERE ClientId = ?");
			sqlStatement.setString(1, newName);
			sqlStatement.setLong(2, client.ClientId());
			int rowCount = sqlStatement.executeUpdate();
			if (rowCount == 0)
				throw new Exception("Client name wasn't changed");
		} catch (SQLException e) {
			throw new Exception("Can't change Client's name in DB", e);
		} finally {
			DbUtils.closeQuietly(connection, sqlStatement, result);
		}
	}
	
	public static void SetActive(Client client, boolean active) throws Exception {
		Connection connection = null;
		PreparedStatement sqlStatement = null;
		ResultSet result = null;
		try {
			connection = getConnection();
			sqlStatement = connection.prepareStatement("UPDATE Clients SET Active = ? WHERE ClientId = ?");
			sqlStatement.setBoolean(1, active);
			sqlStatement.setLong(2, client.ClientId());
			int rowCount = sqlStatement.executeUpdate();
			if (rowCount == 0)
				throw new Exception("Client's active state wasn't changed in DB");
		} catch (SQLException e) {
			throw new Exception("Can't change active state of Client in DB", e);
		} finally {
			DbUtils.closeQuietly(connection, sqlStatement, result);
		}
		
		if (!active)
		{
			Vector<Account> accounts = AccountDAO.GetAll(client);
			for (Account account : accounts)
				AccountDAO.SetActive(account, active);
		}
	}
}
