package com.restfultransfer;

import com.restfultransfer.data.*;

import static org.junit.jupiter.api.Assertions.*;

import java.util.Vector;

import org.junit.Test;
import org.junit.Before;
import org.junit.BeforeClass;

public class DBTests {

	@BeforeClass
	public static void InitDatabase() {
    	try {
			H2Connector.LoadSQLFile("src/main/database.sql");
		} catch (Exception e) {
			e.printStackTrace();
			fail("Database loading failed");
		}
	}
	
	@Before
	public void InitDataset() {
    	try {
	    	H2Connector.LoadSQLFile("src/test/dataset.sql");
		} catch (Exception e) {
			e.printStackTrace();
			fail("Dataset loading failed");
		}
	}
	
	@Test
	public void TestInitDataset() {
		try {
			Vector<Client> clients = (new ClientDAO()).GetAll();
			assertEquals(4, clients.size());
			for (int i = 0; i < clients.size(); i++)
				assertEquals(i+1, clients.get(i).Id());
			Vector<Account> accounts = (new AccountDAO()).GetAll();
			assertEquals(8, accounts.size());
			for (int i = 0; i < accounts.size(); i++)
				assertEquals(i+1, accounts.get(i).Id());
			Vector<Transaction> transactions = (new TransactionDAO()).GetAll();
			assertEquals(9, transactions.size());
			for (int i = 0; i < transactions.size(); i++)
				assertEquals(i+1, transactions.get(i).Id());
			Vector<ExchangeRate> exchangeRates = (new ExchangeRateDAO()).GetAll();
			assertEquals(6, exchangeRates.size());
		} catch (Exception e) {
			e.printStackTrace();
			fail("SQL exception");
		}
	}
}
