package com.restfultransfer.data;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.apache.commons.dbutils.DbUtils;

public class TransactionDAO extends H2Connector {

	private static Transaction TransactionByResultSet(ResultSet result) throws SQLException {
		if (!result.next())
			return null;
		return new Transaction(
				result.getLong("TransactionId"),
				result.getLong("AccountId"),
				result.getLong("AccountIdTo"),           //0 if NULL
				result.getBigDecimal("Amount"),
				result.getBigDecimal("AmountExchanged"), //0 if NULL
				result.getTimestamp("Created"),
				result.getInt("ResultCode")
				);
	}

	private static Transaction Get(Connection connection, long transactionId, boolean forUpdate) throws SQLException {
		PreparedStatement sqlStatement = null;
		ResultSet result = null;
		try {
			if (forUpdate)
				sqlStatement = connection.prepareStatement("SELECT * FROM Transactions WHERE TransactionId = ? FOR UPDATE");
			else
				sqlStatement = connection.prepareStatement("SELECT * FROM Transactions WHERE TransactionId = ?");
			sqlStatement.setLong(1, transactionId);
			result = sqlStatement.executeQuery();
			
			return TransactionByResultSet(result);
		} finally {
			DbUtils.closeQuietly(sqlStatement);
			DbUtils.closeQuietly(result);
		}
	}

	public static Transaction Get(long transactionId) throws Exception {
		Connection connection = null;
		try {
			connection = getConnection();
			return Get(connection, transactionId, false);
		} catch (SQLException e) {
			throw new Exception("Can't get account from DB", e);
		} finally {
			DbUtils.closeQuietly(connection);
		}
	}
	
	public static Transaction CreateExternalTransfer(Account account, BigDecimal amount) throws Exception {
		Connection connection = null;
		PreparedStatement sqlStatement = null;
		ResultSet result = null;
		try {
			connection = getConnection();
			sqlStatement = connection.prepareStatement("INSERT INTO Transactions (AccountId, Amount) VALUES (?, ?)");
			sqlStatement.setLong(1, account.AccountId());
			sqlStatement.setBigDecimal(2, amount);
			int rowCount = sqlStatement.executeUpdate();
			if (rowCount == 0)
				throw new Exception("External transfer wasn't created in DB");
			result = sqlStatement.getResultSet();
			return TransactionByResultSet(result);
		} catch (SQLException e) {
			throw new Exception("Can't create external transfer in DB", e);
		} finally {
			DbUtils.closeQuietly(connection, sqlStatement, result);
		}
	}
	
	public static Transaction CreateInternalTransfer(Account accountFrom, Account accountTo, BigDecimal amountFrom) throws Exception {
		Connection connection = null;
		PreparedStatement sqlStatement = null;
		ResultSet result = null;
		
		BigDecimal amountTo = amountFrom;
		BigDecimal amountFromNeg = amountFrom.negate();
		if (accountFrom.Currency() != accountTo.Currency())
			amountTo = ExchangeRateDAO.Get(accountFrom.Currency(), accountTo.Currency()).Exchange(amountTo);
		
		try {
			connection = getConnection();
			sqlStatement = connection.prepareStatement("INSERT INTO Transactions (AccountId, AccountIdTo, Amount, AmountTo) VALUES (?, ?, ?, ?)");
			sqlStatement.setLong(1, accountFrom.AccountId());
			sqlStatement.setLong(2, accountTo.AccountId());
			sqlStatement.setBigDecimal(3, amountFromNeg);
			sqlStatement.setBigDecimal(4, amountTo);
			int rowCount = sqlStatement.executeUpdate();
			if (rowCount == 0)
				throw new Exception("Internal transfer wasn't created in DB");
			result = sqlStatement.getResultSet();
			return TransactionByResultSet(result);
		} catch (SQLException e) {
			throw new Exception("Can't create internal transfer in DB", e);
		} finally {
			DbUtils.closeQuietly(connection, sqlStatement, result);
		}
	}
}
