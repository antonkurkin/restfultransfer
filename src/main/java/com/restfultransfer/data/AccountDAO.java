package com.restfultransfer.data;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import org.apache.commons.dbutils.DbUtils;

import java.math.BigDecimal;

import java.util.Currency;
import java.util.Vector;

public class AccountDAO extends LongIdObjectDAO<Account> {
	@Override
	public String TableName() { return "Accounts"; }

	@Override
	protected Account ObjectByResultSet(ResultSet result) throws SQLException {
		if (!result.next())
			return null;
		return new Account(
				result.getLong("Id"),
				result.getLong("ClientId"),
				Currency.getInstance(result.getString("Currency")),
				result.getBigDecimal("Balance"),
				result.getBoolean("Active"),
				result.getTimestamp("Created")
				);
	}
	
	public Account Create(long clientId, Currency currency) throws Exception {
		Connection connection = null;
		PreparedStatement sqlStatement = null;
		ResultSet result = null;
		try {
			connection = getConnection();
			sqlStatement = connection.prepareStatement("INSERT INTO " + TableName() + " (ClientId, Currency) VALUES (?, ?)", Statement.RETURN_GENERATED_KEYS);
			sqlStatement.setLong(1, clientId);
			sqlStatement.setString(2, currency.getCurrencyCode());
			int rowCount = sqlStatement.executeUpdate();
			if (rowCount == 0)
				throw new Exception("Account wasn't created in DB");
			result = sqlStatement.getGeneratedKeys();
			if (!result.next())
				throw new Exception("Can't receive account Id from DB");
			return Get(connection, result.getLong(1), false);
		} catch (SQLException e) {
			throw new Exception("Can't create account in DB", e);
		} finally {
			DbUtils.closeQuietly(connection, sqlStatement, result);
		}
	}
	
	public void SetActive(Account account, boolean active) throws Exception {
		Connection connection = null;
		PreparedStatement sqlStatement = null;
		ResultSet result = null;
		try {
			connection = getConnection();
			sqlStatement = connection.prepareStatement("UPDATE Accounts SET Active = ? WHERE Id = ?");
			sqlStatement.setBoolean(1, active);
			sqlStatement.setLong(2, account.Id());
			int rowCount = sqlStatement.executeUpdate();
			if (rowCount == 0)
				throw new Exception("Account wasn't disabled in DB");
		} catch (SQLException e) {
			throw new Exception("Can't disabled Account in DB", e);
		} finally {
			DbUtils.closeQuietly(connection, sqlStatement, result);
		}
	}

	private void UpdateBalance(Account account, BigDecimal newBalance) throws Exception {
		Connection connection = null;
		PreparedStatement sqlStatement = null;
		ResultSet result = null;
		try {
			connection = getConnection();
			sqlStatement = connection.prepareStatement("UPDATE Accounts SET Balance = ? WHERE Id = ?");
			sqlStatement.setBigDecimal(1, newBalance);
			sqlStatement.setLong(2, account.Id());
			int rowCount = sqlStatement.executeUpdate();
			if (rowCount == 0)
				throw new Exception("Account's balance wasn't changed");
		} catch (SQLException e) {
			throw new Exception("Can't change Account's balance in DB", e);
		} finally {
			DbUtils.closeQuietly(connection, sqlStatement, result);
		}
	}
	
	public int ExecuteTransaction(Transaction transaction) throws Exception {
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
	
	public Vector<Account> GetAllByClient(long clientId) throws Exception {
		WhereLong whereClient = new WhereLong("ClientId", clientId);
		return GetAll(whereClient);
	}
}
