package com.restfultransfer;

import static org.junit.jupiter.api.Assertions.fail;

import java.util.concurrent.TimeUnit;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;

import com.restfultransfer.data.H2Connector;

public class DBBeforeTest {
	
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
	    	TimeUnit.MILLISECONDS.sleep(1); // to be sure that timestamp differ on creation
		} catch (Exception e) {
			e.printStackTrace();
			fail("Dataset loading failed");
		}
	}
}
