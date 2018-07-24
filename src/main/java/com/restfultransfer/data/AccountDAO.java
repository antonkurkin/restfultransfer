package com.restfultransfer.data;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.apache.commons.dbutils.DbUtils;

import java.math.BigDecimal;

import java.util.Currency;
import java.util.Vector;

public class AccountDAO extends H2Connector {

	private static Account AccountByResultSet(ResultSet result) throws SQLException
	{
		if (!result.next())
			return null;
		return new Account(
				result.getLong("AccountId"),
				result.getLong("ClientId"),
				Currency.getInstance(result.getString("CurrencyCode")),
				result.getBigDecimal("Balance"),
				result.getBoolean("Active"),
				result.getTimestamp("Created")
				);
	}
	
	private static Account Get(Connection connection, long accountId, boolean forUpdate) throws SQLException {
		PreparedStatement sqlStatement = null;
		ResultSet result = null;
		try {
			if (forUpdate)
				sqlStatement = connection.prepareStatement("SELECT * FROM Accounts WHERE AccountId = ? FOR UPDATE");
			else
				sqlStatement = connection.prepareStatement("SELECT * FROM Accounts WHERE AccountId = ?");
			sqlStatement.setLong(1, accountId);
			result = sqlStatement.executeQuery();
			
			return AccountByResultSet(result);
		} finally {
			DbUtils.closeQuietly(sqlStatement);
			DbUtils.closeQuietly(result);
		}
	}

	public static Account Get(long accountId) throws Exception {
		Connection connection = null;
		try {
			connection = getConnection();
			return Get(connection, accountId, false);
		} catch (SQLException e) {
			throw new Exception("Can't get account from DB", e);
		} finally {
			DbUtils.closeQuietly(connection);
		}
	}

	public static Vector<Account> GetAll(Client client) throws Exception {
		Connection connection = null;
		PreparedStatement sqlStatement = null;
		ResultSet result = null;
		try {
			connection = getConnection();
			sqlStatement = connection.prepareStatement("SELECT * FROM Accounts WHERE ClientId = ?");
			sqlStatement.setLong(1, client.ClientId());
			result = sqlStatement.executeQuery();
			
			Vector<Account> accounts = new Vector<Account>();
			Account account = AccountByResultSet(result);
			while (account != null)
			{
				accounts.add(account);
				account = AccountByResultSet(result);
			}
			return accounts;
		} catch (SQLException e) {
			throw new Exception("Can't get account from DB", e);
		} finally {
			DbUtils.closeQuietly(connection, sqlStatement, result);
		}
	}
	
	public static Account Create(Client client, Currency currency) throws Exception {
		Connection connection = null;
		PreparedStatement sqlStatement = null;
		ResultSet result = null;
		try {
			connection = getConnection();
			sqlStatement = connection.prepareStatement("INSERT INTO Accounts (ClientId, CurrencyCode, Balance) VALUES (?, ?, ?)");
			sqlStatement.setLong(1, client.ClientId());
			sqlStatement.setString(2, currency.getCurrencyCode());
			int rowCount = sqlStatement.executeUpdate();
			if (rowCount == 0)
				throw new Exception("Account wasn't created in DB");
			result = sqlStatement.getResultSet();
			return AccountByResultSet(result);
			
		} catch (SQLException e) {
			throw new Exception("Can't create account in DB", e);
		} finally {
			DbUtils.closeQuietly(connection, sqlStatement, result);
		}
	}
	
	public static void SetActive(Account account, boolean active) throws Exception {
		Connection connection = null;
		PreparedStatement sqlStatement = null;
		ResultSet result = null;
		try {
			connection = getConnection();
			sqlStatement = connection.prepareStatement("UPDATE Accounts SET Active = ? WHERE AccountId = ?");
			sqlStatement.setBoolean(1, active);
			sqlStatement.setLong(2, account.AccountId());
			int rowCount = sqlStatement.executeUpdate();
			if (rowCount == 0)
				throw new Exception("Account wasn't disabled in DB");
		} catch (SQLException e) {
			throw new Exception("Can't disabled Account in DB", e);
		} finally {
			DbUtils.closeQuietly(connection, sqlStatement, result);
		}
	}

	private static void UpdateBalance(Account account, BigDecimal newBalance) throws Exception {
		Connection connection = null;
		PreparedStatement sqlStatement = null;
		ResultSet result = null;
		try {
			connection = getConnection();
			sqlStatement = connection.prepareStatement("UPDATE Accounts SET Balance = ? WHERE AccountId = ?");
			sqlStatement.setBigDecimal(1, newBalance);
			sqlStatement.setLong(2, account.AccountId());
			int rowCount = sqlStatement.executeUpdate();
			if (rowCount == 0)
				throw new Exception("Account's balance wasn't changed");
		} catch (SQLException e) {
			throw new Exception("Can't change Account's balance in DB", e);
		} finally {
			DbUtils.closeQuietly(connection, sqlStatement, result);
		}
	}
	
	public static int ExecuteTransaction(Transaction transaction) throws Exception {
		Connection connection = null;
		PreparedStatement sqlStatement = null;
		ResultSet result = null;
		try {
			connection = getConnection();
			connection.setAutoCommit(false);
			
			Account account = Get(connection, transaction.AccountId(), true);
			if (account.Balance().compareTo(transaction.Amount().negate()) > 0)
			{
				connection.rollback();
				return 1;
			}
			if (!account.isActive())
			{
				connection.rollback();
				return 2;
			}
			if (transaction.AccountIdTo() != 0) {
				Account accountTo = Get(connection, transaction.AccountIdTo(), true);
				if (!accountTo.isActive())
				{
					connection.rollback();
					return 3;
				}
				UpdateBalance(accountTo, accountTo.Balance().add(transaction.AmountTo()));
			}
			UpdateBalance(account, account.Balance().add(transaction.Amount()));
			connection.commit();
			return 0;
		} catch (SQLException e) {
			if (connection != null)
				connection.rollback();
			throw new Exception("Can't execute transaction in DB", e);
		} finally {
			DbUtils.closeQuietly(connection, sqlStatement, result);
		}
	}
}
