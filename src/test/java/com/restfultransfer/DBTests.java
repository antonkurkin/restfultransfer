package com.restfultransfer;

import com.restfultransfer.data.*;
import com.restfultransfer.data.H2Connector;

import java.util.Vector;

import org.junit.Test;
import org.junit.Before;
import org.junit.Assert;

public class DBTests {
	
	@Before
	public void Init() {
    	try {
			H2Connector.LoadSQLFile("src/main/database.sql");
	    	H2Connector.LoadSQLFile("src/test/dataset.sql");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	@Test
	public void TestInitDataset() {
		try {
			Vector<Client> clients = (new ClientDAO()).GetAll();
			Assert.assertEquals(4, clients.size());
			for (int i = 0; i < clients.size(); i++)
				Assert.assertEquals(i+1, clients.get(i).Id());
			Vector<Account> accounts = (new AccountDAO()).GetAll();
			Assert.assertEquals(8, accounts.size());
			for (int i = 0; i < accounts.size(); i++)
				Assert.assertEquals(i+1, accounts.get(i).Id());
			Vector<Transaction> transactions = (new TransactionDAO()).GetAll();
			Assert.assertEquals(9, transactions.size());
			for (int i = 0; i < transactions.size(); i++)
				Assert.assertEquals(i+1, transactions.get(i).Id());
			Vector<ExchangeRate> exchangeRates = (new ExchangeRateDAO()).GetAll();
			Assert.assertEquals(6, exchangeRates.size());
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			
		}
	}
}
