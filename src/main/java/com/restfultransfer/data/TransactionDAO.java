package com.restfultransfer.data;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import java.util.Vector;

import org.apache.commons.dbutils.DbUtils;

import java.math.BigDecimal;

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

	class ValuesFieldsExtenal extends ValuesFields {
		final String[] fieldNames = {"AccountId", "Amount"};
		final long accountId;
		final BigDecimal amount;
		
		public ValuesFieldsExtenal(long accountId, BigDecimal amount)
		{
			this.accountId = accountId;
			this.amount = amount;
		}
		
		@Override
		String[] FieldNames() { return fieldNames; }
		
		@Override
		void SetValues(PreparedStatement sqlStatement) throws SQLException {
			sqlStatement.setLong(1, accountId);
			sqlStatement.setBigDecimal(2, amount);
		}
	}
	
	public Transaction CreateExternal(long accountId, BigDecimal amount) throws Exception {
		return Create(new ValuesFieldsExtenal(accountId, amount));
	}

	class ValuesFieldsIntenal extends ValuesFields {
		final String[] fieldNames = {"AccountId", "AccountIdTo", "Amount", "AmountTo"};
		final long accountIdFrom, accountIdTo;
		final BigDecimal amountFrom, amountTo;
		
		public ValuesFieldsIntenal(long accountIdFrom, long accountIdTo, BigDecimal amountFrom, BigDecimal amountTo)
		{
			this.accountIdFrom = accountIdFrom;
			this.accountIdTo = accountIdTo;
			this.amountFrom = amountFrom;
			this.amountTo = amountTo;
		}
		
		@Override
		String[] FieldNames() { return fieldNames; }
		
		@Override
		void SetValues(PreparedStatement sqlStatement) throws SQLException {
			sqlStatement.setLong(1, accountIdFrom);
			sqlStatement.setLong(2, accountIdTo);
			sqlStatement.setBigDecimal(3, amountFrom);
			sqlStatement.setBigDecimal(4, amountTo);
		}
	}
	
	public Transaction CreateInternal(long accountIdFrom, long accountIdTo, BigDecimal amountFrom, BigDecimal amountTo) throws Exception {
		return Create(new ValuesFieldsIntenal(accountIdFrom, accountIdTo, amountFrom, amountTo));
	}

	private int SetResultCode(Connection connection, Transaction transaction, Transaction.State result) throws SQLException {
		PreparedStatement sqlStatement = null;
		try {
			sqlStatement = connection.prepareStatement("UPDATE Transactions SET ResultCode = ? WHERE Id = ?");
			sqlStatement.setInt(1, result.Code());
			sqlStatement.setLong(2, transaction.Id());
			return sqlStatement.executeUpdate();
		} finally {
			DbUtils.closeQuietly(sqlStatement);
		}
	}
	
	public enum ExecutionResult {
		TRANSACTION_NOT_FOUND,
		TRANSACTION_FAILED,
		TRANSACTION_ALREADY_EXECUTED,
		TRANSACTION_OK
	}
	
	public ExecutionResult Execute(long transactionId) throws Exception {
		Connection connection = null;
		try
		{
			connection = getConnection();
			connection.setAutoCommit(false);
			try {
				Transaction transaction = Get(connection, transactionId, true);
				if (transaction == null) {
		            connection.rollback();
					return ExecutionResult.TRANSACTION_NOT_FOUND;
				}
				if (transaction.ResultCode() != Transaction.State.TRANSACTION_PENDING.Code()) {
		            connection.rollback();
					return ExecutionResult.TRANSACTION_ALREADY_EXECUTED;
				}
				Transaction.State result = (new AccountDAO()).ExecuteTransaction(connection, transaction);
				int changed = SetResultCode(connection, transaction, result);
				if (changed != 1)
				{
		            connection.rollback();
		            return ExecutionResult.TRANSACTION_FAILED;
				}
	            connection.commit();
	    		return ExecutionResult.TRANSACTION_OK;
	        } catch (SQLException e) {
	            connection.rollback();
	            throw e;
	        }
		} catch (SQLException e) {
			if (connection != null)
				connection.rollback();
			throw new Exception("Can't execute transaction in DB", e);
		} finally {
			DbUtils.closeQuietly(connection);
		}
	}
}
