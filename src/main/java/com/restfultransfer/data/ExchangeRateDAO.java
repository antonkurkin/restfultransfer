package com.restfultransfer.data;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.apache.commons.dbutils.DbUtils;

import java.util.Currency;

public class ExchangeRateDAO extends H2Connector {

	private static ExchangeRate ExchangeRateByResultSet(ResultSet result) throws SQLException
	{
		if (!result.next())
			return null;
		return new ExchangeRate(
				Currency.getInstance(result.getString("CurrencyFrom")),
				Currency.getInstance(result.getString("CurrencyTo")),
				result.getDouble("Rate")
				);
	}
	
	static ExchangeRate Get(Currency currencyFrom, Currency currencyTo) throws Exception {
		Connection connection = null;
		PreparedStatement sqlStatement = null;
		ResultSet result = null;
		try {
			connection = getConnection();
			sqlStatement = connection.prepareStatement("SELECT * FROM ExchangeRates WHERE CurrencyFrom = ? AND CurrencyTo = ?");
			sqlStatement.setString(1, currencyFrom.getCurrencyCode());
			sqlStatement.setString(2, currencyTo.getCurrencyCode());
			result = sqlStatement.executeQuery();
			
			return ExchangeRateByResultSet(result);
		} catch (SQLException e) {
			throw new Exception("Can't get account from DB", e);
		} finally {
			DbUtils.closeQuietly(connection, sqlStatement, result);
		}
	}
}
