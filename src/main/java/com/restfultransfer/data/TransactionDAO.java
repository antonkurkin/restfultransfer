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
	
	public Vector<Transaction> GetAllByAccount(long accountId) throws SQLException {
		WhereLong whereAccount = new WhereLong("AccountId", accountId);
		Vector<Transaction> transactions = GetAll(whereAccount);
		WhereLong whereAccountTo = new WhereLong("AccountIdTo", accountId);
		transactions.addAll(GetAll(whereAccountTo));
		return transactions;
	}

	class ValuesFieldsExtenal extends ValuesFields {
		final long accountId;
		final BigDecimal amount;
		
		public ValuesFieldsExtenal(long accountId, BigDecimal amount)
		{
			this.accountId = accountId;
			this.amount = amount;
		}

		@Override
		public String GetRequestSuffix() { return "(AccountId, Amount) VALUES (?, ?)"; }
		
		@Override
		void SetValues(PreparedStatement sqlStatement) throws SQLException {
			sqlStatement.setLong(1, accountId);
			sqlStatement.setBigDecimal(2, amount);
		}
	}
	
	public long CreateExternal(long accountId, BigDecimal amount) throws SQLException {
		return Create(new ValuesFieldsExtenal(accountId, amount));
	}

	class ValuesFieldsIntenal extends ValuesFields {
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
		public String GetRequestSuffix() { return "(AccountId, AccountIdTo, Amount, AmountTo) VALUES (?, ?, ?, ?)"; }
		
		@Override
		void SetValues(PreparedStatement sqlStatement) throws SQLException {
			sqlStatement.setLong(1, accountIdFrom);
			sqlStatement.setLong(2, accountIdTo);
			sqlStatement.setBigDecimal(3, amountFrom);
			sqlStatement.setBigDecimal(4, amountTo);
		}
	}
	
	public long CreateInternal(long accountIdFrom, long accountIdTo, BigDecimal amountFrom, BigDecimal amountTo) throws SQLException {
		return Create(new ValuesFieldsIntenal(accountIdFrom, accountIdTo, amountFrom, amountTo));
	}

	private class WhereInt extends WhereField {
		int i;
		public WhereInt(String FieldName, int i) {
			super(FieldName);
			this.i = i;
		}
		void SetField(int n, PreparedStatement sqlStatement) throws SQLException { sqlStatement.setInt(n, i); }
	}

	private int SetResultCode(Connection connection, Transaction transaction, Transaction.State result) throws SQLException {
		WhereInt resultSetter = new WhereInt("ResultCode", result.Code());
		return ChangeFieldById(connection, transaction.Id(), resultSetter);
	}
	
	public enum ExecutionResult {
		TRANSACTION_NOT_FOUND,
		TRANSACTION_FAILED,
		TRANSACTION_ALREADY_EXECUTED,
		TRANSACTION_OK
	}
	
	public ExecutionResult Execute(long transactionId) throws SQLException {
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
	            if (result != Transaction.State.TRANSACTION_OK)
		            return ExecutionResult.TRANSACTION_FAILED;
	    		return ExecutionResult.TRANSACTION_OK;
	        } catch (SQLException e) {
	            connection.rollback();
	            throw e;
	        }
		} catch (SQLException e) {
			if (connection != null)
				connection.rollback();
			throw new SQLException("Can't execute transaction in DB", e);
		} finally {
			DbUtils.closeQuietly(connection);
		}
	}
}
