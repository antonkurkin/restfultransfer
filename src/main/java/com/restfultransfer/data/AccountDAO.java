package com.restfultransfer.data;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

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
	
	class ValuesFieldsAccount extends ValuesFields {
		final long clientId;
		final Currency currency;
		
		public ValuesFieldsAccount(long clientId, Currency currency)
		{
			this.clientId = clientId;
			this.currency = currency;
		}

		@Override
		public String GetRequestSuffix() { return "(ClientId, Currency) VALUES (?, ?)"; }
		
		@Override
		void SetValues(PreparedStatement sqlStatement) throws SQLException {
			sqlStatement.setLong(1, clientId);
			sqlStatement.setString(2, currency.getCurrencyCode());
		}
	}
	
	public Account Create(long clientId, Currency currency) throws SQLException {
		return Create(new ValuesFieldsAccount(clientId, currency));
	}

	public int SetActive(long accountId, boolean active) throws SQLException {
		Connection connection = null;
		try {
			connection = getConnection();
			WhereBoolean activeSetter = new WhereBoolean("Active", active);
			return ChangeFieldById(connection, accountId, activeSetter);
		} finally {
			DbUtils.closeQuietly(connection);
		}
	}

	public int DeactivateByClientId(long clientId) throws SQLException {
		Connection connection = null;
		try {
			connection = getConnection();
			WhereBoolean activeSetter = new WhereBoolean("Active", false);
			WhereLong clientSetter = new WhereLong("ClientId", clientId);
			return ChangeFieldWhere(connection, activeSetter, clientSetter);
		} finally {
			DbUtils.closeQuietly(connection);
		}
	}
	
	private class WhereBigDecimal extends WhereField {
		BigDecimal bd;
		public WhereBigDecimal(String FieldName, BigDecimal bd) {
			super(FieldName);
			this.bd = bd;
		}
		void SetField(int n, PreparedStatement sqlStatement) throws SQLException { sqlStatement.setBigDecimal(n, bd); }
	}

	private int UpdateBalance(Connection connection, Account account, BigDecimal newBalance) throws SQLException {
		WhereBigDecimal balanceSetter = new WhereBigDecimal("Balance", newBalance);
		return ChangeFieldById(connection, account.Id(), balanceSetter);
	}
	
	public Transaction.State ExecuteTransaction(Connection connection, Transaction transaction) throws SQLException {
		PreparedStatement sqlStatement = null;
		try {
			Account account = Get(connection, transaction.AccountId(), true);
			if (account == null)
				return Transaction.State.TRANSACTION_ACCOUNT_NOT_FOUND;
			if (account.Balance().compareTo(transaction.Amount().negate()) < 0)
				return Transaction.State.TRANSACTION_NOT_ENOUGH;
			if (!account.isActive())
				return Transaction.State.TRANSACTION_ACCOUNT_INACTIVE;
			int rowCount = 0;
			int mustUpdate = 1;
			if (transaction.AccountIdTo() != 0) {
				mustUpdate = 2;
				Account accountTo = Get(connection, transaction.AccountIdTo(), true);
				if (accountTo == null)
					return Transaction.State.TRANSACTION_ACCOUNT2_NOT_FOUND;
				if (!accountTo.isActive())
					return Transaction.State.TRANSACTION_ACCOUNT2_INACTIVE;
				rowCount += UpdateBalance(connection, accountTo, accountTo.Balance().add(transaction.AmountTo()));
			}
			rowCount += UpdateBalance(connection, account, account.Balance().add(transaction.Amount()));
			if (rowCount != mustUpdate)
				return Transaction.State.TRANSACTION_BALANCE_UPDATE_FAIL;
			return Transaction.State.TRANSACTION_OK;
		} finally {
			DbUtils.closeQuietly(sqlStatement);
		}
	}
	
	public Vector<Account> GetAllByClient(long clientId) throws SQLException {
		WhereLong whereClient = new WhereLong("ClientId", clientId);
		return GetAll(whereClient);
	}
}
