package com.restfultransfer;

import com.restfultransfer.data.*;

import static org.junit.jupiter.api.Assertions.*;

import java.util.Vector;

import org.junit.jupiter.api.Test;

public class DatasetTest extends DBBeforeLoad {

	ClientDAO clientDAO = new ClientDAO();
	AccountDAO accountDAO = new AccountDAO();
	TransactionDAO transactionDAO = new TransactionDAO();
	ExchangeRateDAO exchangeRateDAO = new ExchangeRateDAO();
	
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
}
