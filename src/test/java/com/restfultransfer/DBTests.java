package com.restfultransfer;

import com.restfultransfer.data.*;

import static org.junit.jupiter.api.Assertions.*;

import java.sql.Timestamp;
import java.util.Vector;

import org.junit.Test;
import org.junit.Before;
import org.junit.BeforeClass;

public class DBTests {

	ClientDAO clientDAO = new ClientDAO();
	AccountDAO accountDAO = new AccountDAO();
	TransactionDAO transactionDAO = new TransactionDAO();
	ExchangeRateDAO exchangeRateDAO = new ExchangeRateDAO();
	
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
			Vector<Client> clients = clientDAO.GetAll();
			assertEquals(4, clients.size());
			for (int i = 0; i < clients.size(); i++)
				assertEquals(i+1, clients.get(i).Id());
			Vector<Account> accounts = accountDAO.GetAll();
			assertEquals(8, accounts.size());
			for (int i = 0; i < accounts.size(); i++)
				assertEquals(i+1, accounts.get(i).Id());
			Vector<Transaction> transactions = transactionDAO.GetAll();
			assertEquals(9, transactions.size());
			for (int i = 0; i < transactions.size(); i++)
				assertEquals(i+1, transactions.get(i).Id());
			Vector<ExchangeRate> exchangeRates = exchangeRateDAO.GetAll();
			assertEquals(6, exchangeRates.size());
		} catch (Exception e) {
			e.printStackTrace();
			fail("SQL exception");
		}
	}
	
	@Test
	public void GetClient() {
		try {
			Client client = clientDAO.Get(2);
			assertEquals(2, client.Id());
			assertEquals("alex", client.Name());
			assertEquals(true, client.isActive());
			assertNotEquals(new Timestamp(0), client.Created());
		} catch (Exception e) {
			e.printStackTrace();
			fail("SQL exception");
		}
	}

	@Test
	public void GetClientWrong() {
		try {
			Client client = clientDAO.Get(99);
			assertEquals(null, client);
		} catch (Exception e) {
			e.printStackTrace();
			fail("SQL exception");
		}
	}

	@Test
	public void ChangeClientName() {
		try {
			Client client = clientDAO.Get(1);
			assertEquals("anton", client.Name());
			int changed = clientDAO.ChangeName(client.Id(), "dmitry");
			assertEquals(1, changed);
			client = clientDAO.Get(client.Id());
			assertEquals("dmitry", client.Name());
		} catch (Exception e) {
			e.printStackTrace();
			fail("SQL exception");
		}
	}

	@Test
	public void ChangeClientNameWrong() {
		try {
			int changed = clientDAO.ChangeName(99, "dmitry");
			assertEquals(0, changed);
		} catch (Exception e) {
			e.printStackTrace();
			fail("SQL exception");
		}
	}
}
