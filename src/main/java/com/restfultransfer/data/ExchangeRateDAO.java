package com.restfultransfer.data;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import org.apache.commons.dbutils.DbUtils;

import java.util.Currency;
import java.util.Vector;

public class ExchangeRateDAO extends H2Connector {

	private ExchangeRate ObjectByResultSet(ResultSet result) throws SQLException
	{
		if (!result.next())
			return null;
		return new ExchangeRate(
				Currency.getInstance(result.getString("CurrencyFrom")),
				Currency.getInstance(result.getString("CurrencyTo")),
				result.getDouble("Rate")
				);
	}
	
	public ExchangeRate Get(Currency currencyFrom, Currency currencyTo) throws SQLException {
		Connection connection = null;
		PreparedStatement sqlStatement = null;
		ResultSet result = null;
		try {
			connection = getConnection();
			sqlStatement = connection.prepareStatement("SELECT * FROM ExchangeRates WHERE CurrencyFrom = ? AND CurrencyTo = ?");
			sqlStatement.setString(1, currencyFrom.getCurrencyCode());
			sqlStatement.setString(2, currencyTo.getCurrencyCode());
			result = sqlStatement.executeQuery();
			
			return ObjectByResultSet(result);
		} catch (SQLException e) {
			throw new SQLException("Can't get exchange rate from DB", e);
		} finally {
			DbUtils.closeQuietly(connection, sqlStatement, result);
		}
	}

	public Vector<ExchangeRate> GetAll() throws SQLException {
		Connection connection = null;
		PreparedStatement sqlStatement = null;
		ResultSet result = null;
		try {
			connection = getConnection();
			sqlStatement = connection.prepareStatement("SELECT * FROM ExchangeRates");
			result = sqlStatement.executeQuery();
			
			Vector<ExchangeRate> objects = new Vector<ExchangeRate>();
			ExchangeRate obj = ObjectByResultSet(result);
			while (obj != null)
			{
				objects.add(obj);
				obj = ObjectByResultSet(result);
			}
			return objects;
		} catch (SQLException e) {
			throw new SQLException("Can't get objects from table ExchangeRates", e);
		} finally {
			DbUtils.closeQuietly(connection, sqlStatement, result);
		}
	}

	public ExchangeRate Create(Currency currencyFrom, Currency currencyTo, double rate) throws SQLException {
		Connection connection = null;
		PreparedStatement sqlStatement = null;
		try {
			connection = getConnection();
			sqlStatement = connection.prepareStatement("INSERT INTO ExchangeRates (CurrencyFrom, CurrencyTo, Rate) VALUES (?, ?, ?)", Statement.RETURN_GENERATED_KEYS);
			sqlStatement.setString(1, currencyFrom.getCurrencyCode());
			sqlStatement.setString(2, currencyTo.getCurrencyCode());
			sqlStatement.setDouble(3, rate);
			int rowCount = sqlStatement.executeUpdate();
			if (rowCount == 0)
				return null;
			
			return Get(currencyFrom, currencyTo);
		} catch (SQLException e) {
			if (e.getErrorCode() == org.h2.api.ErrorCode.DUPLICATE_KEY_1)
				return null;
			throw new SQLException("Can't create exchange rate in DB", e);
		} finally {
			DbUtils.closeQuietly(connection);
			DbUtils.closeQuietly(sqlStatement);
		}
	}

	public int Delete(Currency currencyFrom, Currency currencyTo) throws SQLException {
		Connection connection = null;
		PreparedStatement sqlStatement = null;
		ResultSet result = null;
		try {
			connection = getConnection();
			sqlStatement = connection.prepareStatement("DELETE FROM ExchangeRates WHERE CurrencyFrom = ? AND CurrencyTo = ?", Statement.RETURN_GENERATED_KEYS);
			sqlStatement.setString(1, currencyFrom.getCurrencyCode());
			sqlStatement.setString(2, currencyTo.getCurrencyCode());
			return sqlStatement.executeUpdate();
		} catch (SQLException e) {
			throw new SQLException("Can't delete exchange rate from DB", e);
		} finally {
			DbUtils.closeQuietly(connection, sqlStatement, result);
		}
	}
}
