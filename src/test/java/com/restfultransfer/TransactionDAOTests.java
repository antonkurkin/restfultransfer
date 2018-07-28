package com.restfultransfer;

import static org.junit.jupiter.api.Assertions.*;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.Vector;

import org.junit.jupiter.api.Test;

import com.restfultransfer.data.Transaction;
import com.restfultransfer.data.TransactionDAO;

class TransactionDAOTests extends DBBeforeLoad{

	TransactionDAO transactionDAO = new TransactionDAO();
	
	@Test
	public void GetAccount() {
		try {
			Transaction transaction = transactionDAO.Get(1);
			assertEquals(1, transaction.Id());
			assertEquals(1, transaction.AccountId());
			assertEquals(0, transaction.AccountIdTo());
			assertEquals(0, transaction.Amount().compareTo(BigDecimal.valueOf(3000)));
			assertEquals(0, transaction.AmountTo().compareTo(BigDecimal.ZERO));
			assertNotEquals(new Timestamp(0), transaction.Created());
			
			transaction = transactionDAO.Get(10);
			assertEquals(null, transaction);
		} catch (Exception e) {
			e.printStackTrace();
			fail("SQL exception");
		}
	}

	@Test
	public void GetAccountsByClient() {
		try {
			Vector<Transaction> transactions = transactionDAO.GetAllByAccount(2);
			assertEquals(3, transactions.size());
			
			transactions = transactionDAO.GetAllByAccount(6);
			assertEquals(2, transactions.size());
			
			transactions = transactionDAO.GetAllByAccount(-2);
			assertEquals(0, transactions.size());
		} catch (Exception e) {
			e.printStackTrace();
			fail("SQL exception");
		}
	}

	@Test
	public void CreateExternalTransaction() {
		try {
			Transaction transaction = transactionDAO.CreateExternal(2, BigDecimal.valueOf(100));
			assertEquals(10, transaction.Id());
			assertEquals(2, transaction.AccountId());
			assertEquals(0, transaction.AccountIdTo());
			assertEquals(0, transaction.Amount().compareTo(BigDecimal.valueOf(100)));
			assertEquals(0, transaction.AmountTo().compareTo(BigDecimal.ZERO));
			
			Transaction transactionGet = transactionDAO.Get(transaction.Id());
			assertEquals(transactionGet.AccountId(), transaction.AccountId());
			assertEquals(transactionGet.AccountIdTo(), transaction.AccountIdTo());

			transactionGet = transactionDAO.Get(1);
			assertNotEquals(transactionGet.Created(), transaction.Created());
			
			transaction = transactionDAO.CreateExternal(99, BigDecimal.ZERO);
			assertEquals(null, transaction);
		} catch (Exception e) {
			e.printStackTrace();
			fail("SQL exception");
		}
	}

	@Test
	public void CreateInternalTransaction() {
		try {
			Transaction transaction = transactionDAO.CreateInternal(2, 3, BigDecimal.valueOf(-100), BigDecimal.valueOf(300));
			assertEquals(10, transaction.Id());
			assertEquals(2, transaction.AccountId());
			assertEquals(3, transaction.AccountIdTo());
			assertEquals(0, transaction.Amount().compareTo(BigDecimal.valueOf(-100)));
			assertEquals(0, transaction.AmountTo().compareTo(BigDecimal.valueOf(300)));
			
			Transaction transactionGet = transactionDAO.Get(transaction.Id());
			assertEquals(transactionGet.AccountId(), transaction.AccountId());
			assertEquals(transactionGet.AccountIdTo(), transaction.AccountIdTo());

			transactionGet = transactionDAO.Get(1);
			assertNotEquals(transactionGet.Created(), transaction.Created());
		} catch (Exception e) {
			e.printStackTrace();
			fail("SQL exception");
		}
	}

	@Test
	public void CreateFailedTransaction() { //check database constraints
		try {
			Transaction transaction = transactionDAO.CreateExternal(99, BigDecimal.valueOf(10));
			assertEquals(null, transaction);
			
			transaction = transactionDAO.CreateExternal(1, BigDecimal.ZERO);
			assertEquals(null, transaction);
			
			transaction = transactionDAO.CreateInternal(-1, 2, BigDecimal.valueOf(-10), BigDecimal.valueOf(10));
			assertEquals(null, transaction);
			
			transaction = transactionDAO.CreateInternal(1, 99, BigDecimal.valueOf(-10), BigDecimal.valueOf(10));
			assertEquals(null, transaction);
			
			transaction = transactionDAO.CreateInternal(1, 2, BigDecimal.valueOf(100), BigDecimal.valueOf(10));
			assertEquals(null, transaction);
			
			transaction = transactionDAO.CreateInternal(1, 2, BigDecimal.valueOf(-10), BigDecimal.valueOf(-100));
			assertEquals(null, transaction);
			
			transaction = transactionDAO.CreateInternal(1, 2, BigDecimal.ZERO, BigDecimal.valueOf(10));
			assertEquals(null, transaction);
			
			transaction = transactionDAO.CreateInternal(1, 2, BigDecimal.valueOf(-10), BigDecimal.ZERO);
			assertEquals(null, transaction);
		} catch (Exception e) {
			e.printStackTrace();
			fail("SQL exception");
		}
	}
}
