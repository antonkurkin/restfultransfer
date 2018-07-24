package com.restfultransfer.data;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import org.apache.commons.dbutils.DbUtils;

public class H2Connector {
	H2Connector()
	{
		DbUtils.loadDriver("org.h2.Driver");
	}
	
	public static Connection getConnection() throws ClassNotFoundException, SQLException {
		return DriverManager.getConnection("jdbc:h2:mem:restfultransfer;DB_CLOSE_DELAY=-1");
	}
	
	public static void CloseConnection(Connection connection) {
		DbUtils.closeQuietly(connection);
	}
}
