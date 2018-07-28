package com.restfultransfer;

import static org.junit.jupiter.api.Assertions.fail;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;

import com.restfultransfer.data.H2Connector;

public class DBBeforeLoad {
	
	@BeforeAll
	public static void InitDatabase() {
    	try {
			H2Connector.LoadSQLFile("src/main/database.sql");
		} catch (Exception e) {
			e.printStackTrace();
			fail("Database loading failed");
		}
	}
	
	@BeforeEach
	public void InitDataset() {
    	try {
	    	H2Connector.LoadSQLFile("src/test/dataset.sql");
		} catch (Exception e) {
			e.printStackTrace();
			fail("Dataset loading failed");
		}
	}
}
