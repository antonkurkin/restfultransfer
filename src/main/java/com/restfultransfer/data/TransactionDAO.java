package com.restfultransfer.data;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import java.util.Vector;
import java.math.BigDecimal;

import org.apache.commons.dbutils.DbUtils;

public class TransactionDAO extends LongIdObjectDAO<Transaction> {
	@Override
	public String TableName() { return "Transactions"; }

	@Override
	protected Transaction ObjectByResultSet(ResultSet result) throws SQLException {
		if (!result.next())
			return null;
		return new Transaction(
				result.getLong("Id"),
				result.getLong("AccountId"),
				result.getLong("AccountIdTo"),    //0 if NULL
				result.getBigDecimal("Amount"),
				result.getBigDecimal("AmountTo"),
				result.getTimestamp("Created"),
				result.getInt("ResultCode")
				);
	}
	
	public Vector<Transaction> GetAllByAccount(long accountId) throws Exception {
		WhereLong whereAccount = new WhereLong("AccountId", accountId);
		Vector<Transaction> transactions = GetAll(whereAccount);
		WhereLong whereAccountTo = new WhereLong("AccountIdTo", accountId);
		transactions.addAll(GetAll(whereAccountTo));
		return transactions;
	}
	
	public Transaction CreateExternalTransfer(Account account, BigDecimal amount) throws Exception {
		Connection connection = null;
		PreparedStatement sqlStatement = null;
		ResultSet result = null;
		try {
			connection = getConnection();
			sqlStatement = connection.prepareStatement("INSERT INTO Transactions (AccountId, Amount) VALUES (?, ?)");
			sqlStatement.setLong(1, account.Id());
			sqlStatement.setBigDecimal(2, amount);
			int rowCount = sqlStatement.executeUpdate();
			if (rowCount == 0)
				throw new Exception("External transfer wasn't created in DB");
			result = sqlStatement.getResultSet();
			return ObjectByResultSet(result);
		} catch (SQLException e) {
			throw new Exception("Can't create external transfer in DB", e);
		} finally {
			DbUtils.closeQuietly(connection, sqlStatement, result);
		}
	}
	
	public Transaction CreateInternalTransfer(Account accountFrom, Account accountTo, BigDecimal amountFrom) throws Exception {
		Connection connection = null;
		PreparedStatement sqlStatement = null;
		ResultSet result = null;
		
		BigDecimal amountTo = amountFrom;
		BigDecimal amountFromNeg = amountFrom.negate();
		if (accountFrom.Currency() != accountTo.Currency())
			amountTo = (new ExchangeRateDAO()).Get(accountFrom.Currency(), accountTo.Currency()).Exchange(amountTo);
		
		try {
			connection = getConnection();
			sqlStatement = connection.prepareStatement("INSERT INTO Transactions (AccountId, AccountIdTo, Amount, AmountTo) VALUES (?, ?, ?, ?)");
			sqlStatement.setLong(1, accountFrom.Id());
			sqlStatement.setLong(2, accountTo.Id());
			sqlStatement.setBigDecimal(3, amountFromNeg);
			sqlStatement.setBigDecimal(4, amountTo);
			int rowCount = sqlStatement.executeUpdate();
			if (rowCount == 0)
				throw new Exception("Internal transfer wasn't created in DB");
			result = sqlStatement.getResultSet();
			return ObjectByResultSet(result);
		} catch (SQLException e) {
			throw new Exception("Can't create internal transfer in DB", e);
		} finally {
			DbUtils.closeQuietly(connection, sqlStatement, result);
		}
	}
}
