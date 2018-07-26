package com.restfultransfer.data;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import java.util.Vector;
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
}
