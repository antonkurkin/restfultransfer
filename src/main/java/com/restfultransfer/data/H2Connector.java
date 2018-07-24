package com.restfultransfer.data;

import java.io.FileReader;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import org.apache.commons.dbutils.DbUtils;

import org.h2.tools.RunScript;

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
	
	public static void LoadTestDBFile(String filename) throws Exception {
		Connection connection = null;
		try {
			connection = getConnection();
			FileReader reader = new FileReader(filename);
			RunScript.execute(connection, reader);
		} catch(Exception e) {
			throw new Exception("Problem while loading test database file", e);
		} finally {
			DbUtils.closeQuietly(connection);
		}
	}
}
