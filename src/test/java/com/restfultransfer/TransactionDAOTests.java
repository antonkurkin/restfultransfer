package com.restfultransfer;

import static org.junit.jupiter.api.Assertions.*;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.Vector;

import org.junit.jupiter.api.Test;

import com.restfultransfer.data.Account;
import com.restfultransfer.data.AccountDAO;
import com.restfultransfer.data.Transaction;
import com.restfultransfer.data.TransactionDAO;
import com.restfultransfer.data.TransactionDAO.ExecutionResult;

class TransactionDAOTests extends DBBeforeTest{

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
			int before = transactionDAO.GetAll().size();
			long transactionId = transactionDAO.CreateExternal(2, BigDecimal.valueOf(100));
			assertEquals(10, transactionId);
			Transaction transaction = transactionDAO.Get(transactionId);
			assertEquals(2, transaction.AccountId());
			assertEquals(0, transaction.AccountIdTo());
			assertEquals(0, transaction.Amount().compareTo(BigDecimal.valueOf(100)));
			assertEquals(0, transaction.AmountTo().compareTo(BigDecimal.ZERO));

			Transaction transactionGet = transactionDAO.Get(1);
			assertNotEquals(transactionGet.Created(), transaction.Created());

			int after = transactionDAO.GetAll().size();
			assertEquals(before + 1, after);
			
			transactionId = transactionDAO.CreateExternal(99, BigDecimal.ZERO);
			assertEquals(0, transactionId);
			
			assertEquals(after, transactionDAO.GetAll().size());
		} catch (Exception e) {
			e.printStackTrace();
			fail("SQL exception");
		}
	}

	@Test
	public void CreateInternalTransaction() {
		try {
			int before = transactionDAO.GetAll().size();
			long transactionId = transactionDAO.CreateInternal(2, 3, BigDecimal.valueOf(-100), BigDecimal.valueOf(300));
			assertEquals(10, transactionId);
			Transaction transaction = transactionDAO.Get(transactionId);
			assertEquals(2, transaction.AccountId());
			assertEquals(3, transaction.AccountIdTo());
			assertEquals(0, transaction.Amount().compareTo(BigDecimal.valueOf(-100)));
			assertEquals(0, transaction.AmountTo().compareTo(BigDecimal.valueOf(300)));

			Transaction transactionGet = transactionDAO.Get(1);
			assertNotEquals(transactionGet.Created(), transaction.Created());

			int after = transactionDAO.GetAll().size();
			assertEquals(before + 1, after);
			
			transactionId = transactionDAO.CreateInternal(99, 1, BigDecimal.ZERO, BigDecimal.TEN);
			assertEquals(0, transactionId);
			
			assertEquals(after, transactionDAO.GetAll().size());
		} catch (Exception e) {
			e.printStackTrace();
			fail("SQL exception");
		}
	}

	@Test
	public void CreateFailedTransaction() { //check database constraints
		try {
			int before = transactionDAO.GetAll().size();
			
			long transaction = transactionDAO.CreateExternal(99, BigDecimal.valueOf(10));
			assertEquals(0, transaction);
			
			transaction = transactionDAO.CreateExternal(1, BigDecimal.ZERO);
			assertEquals(0, transaction);
			
			transaction = transactionDAO.CreateInternal(-1, 2, BigDecimal.valueOf(-10), BigDecimal.valueOf(10));
			assertEquals(0, transaction);
			
			transaction = transactionDAO.CreateInternal(1, 99, BigDecimal.valueOf(-10), BigDecimal.valueOf(10));
			assertEquals(0, transaction);
			
			transaction = transactionDAO.CreateInternal(1, 2, BigDecimal.valueOf(100), BigDecimal.valueOf(10));
			assertEquals(0, transaction);
			
			transaction = transactionDAO.CreateInternal(1, 2, BigDecimal.valueOf(-10), BigDecimal.valueOf(-100));
			assertEquals(0, transaction);
			
			transaction = transactionDAO.CreateInternal(1, 2, BigDecimal.ZERO, BigDecimal.valueOf(10));
			assertEquals(0, transaction);
			
			transaction = transactionDAO.CreateInternal(1, 2, BigDecimal.valueOf(-10), BigDecimal.ZERO);
			assertEquals(0, transaction);
			
			assertEquals(before, transactionDAO.GetAll().size());
		} catch (Exception e) {
			e.printStackTrace();
			fail("SQL exception");
		}
	}
	
	@Test
	public void GetAllTrasactions() {
		try {
			int[] amounts = {3000 ,300 ,-100 ,100 ,10 ,7000 ,-700 ,-10 ,-100};
			Vector<Transaction> transactions = transactionDAO.GetAll();
			
			assertEquals(amounts.length, transactions.size());
			for (int i = 0; i < transactions.size(); i++)
				assertEquals(0, transactions.get(i).Amount().compareTo(BigDecimal.valueOf(amounts[i])));
		} catch (Exception e) {
			e.printStackTrace();
			fail("SQL exception");
		}
	}
	
	@Test
	public void TransactionExecutedAlready() {
		try {
			Transaction transaction = transactionDAO.Get(1);
			Account account = (new AccountDAO()).Get(transaction.AccountId());
			int beforeResult = transaction.ResultCode();
			BigDecimal beforeBalance = account.Balance();
			ExecutionResult result = transactionDAO.Execute(transaction.Id());
			assertEquals(ExecutionResult.TRANSACTION_ALREADY_EXECUTED, result);

			transaction = transactionDAO.Get(transaction.Id());
			account = (new AccountDAO()).Get(transaction.AccountId());
			assertEquals(beforeResult, transaction.ResultCode());
			assertEquals(0, beforeBalance.compareTo(account.Balance()));
			
		} catch (Exception e) {
			e.printStackTrace();
			fail("SQL exception");
		}
	}

	@Test
	public void TransactionNotEnough() {
		try {
			long transactionId = transactionDAO.CreateInternal(1, 2, BigDecimal.valueOf(-999999), BigDecimal.valueOf(300));
			
			Transaction transaction = transactionDAO.Get(transactionId);
			Account account = (new AccountDAO()).Get(transaction.AccountId());
			Account accountTo = (new AccountDAO()).Get(transaction.AccountIdTo());
			BigDecimal beforeBalance = account.Balance();
			BigDecimal beforeBalanceTo = accountTo.Balance();
			ExecutionResult result = transactionDAO.Execute(transaction.Id());
			assertEquals(ExecutionResult.TRANSACTION_FAILED, result);

			transaction = transactionDAO.Get(transaction.Id());
			account = (new AccountDAO()).Get(transaction.AccountId());
			accountTo = (new AccountDAO()).Get(transaction.AccountIdTo());
			assertEquals(0, beforeBalance.compareTo(account.Balance()));
			assertEquals(0, beforeBalanceTo.compareTo(accountTo.Balance()));
			
			assertEquals(Transaction.State.TRANSACTION_NOT_ENOUGH.Code(), transaction.ResultCode());
		} catch (Exception e) {
			e.printStackTrace();
			fail("SQL exception");
		}
	}
	
	@Test
	public void TransactionNotFound() {
		try {
			ExecutionResult result = transactionDAO.Execute(999);
			assertEquals(ExecutionResult.TRANSACTION_NOT_FOUND, result);
		} catch (Exception e) {
			e.printStackTrace();
			fail("SQL exception");
		}
	}
	
	@Test
	public void TransactionOk() {
		try {
			BigDecimal From = BigDecimal.valueOf(-100);
			BigDecimal To = BigDecimal.valueOf(300);

			long transactionId = transactionDAO.CreateInternal(2, 3, From, To);
			Transaction transaction = transactionDAO.Get(transactionId);
			assertEquals(Transaction.State.TRANSACTION_PENDING.Code(), transaction.ResultCode());
			
			AccountDAO accountDAO = new AccountDAO();
			Account accountFrom = accountDAO.Get(transaction.AccountId());
			Account accountTo = accountDAO.Get(transaction.AccountIdTo());
			
			BigDecimal beforeFrom = accountFrom.Balance();
			BigDecimal beforeTo = accountTo.Balance();
			
			ExecutionResult result = transactionDAO.Execute(transactionId);
			assertEquals(ExecutionResult.TRANSACTION_OK, result);

			accountFrom = accountDAO.Get(transaction.AccountId());
			accountTo = accountDAO.Get(transaction.AccountIdTo());
			
			assertEquals(0, beforeFrom.add(From).compareTo(accountFrom.Balance()));
			assertEquals(0, beforeTo.add(To).compareTo(accountTo.Balance()));

			transaction = transactionDAO.Get(transactionId);
			assertEquals(Transaction.State.TRANSACTION_OK.Code(), transaction.ResultCode());
		} catch (Exception e) {
			e.printStackTrace();
			fail("SQL exception");
		}
	}

	@Test
	public void TransactionInactive() {
		try {
			long transactionId = transactionDAO.CreateInternal(1, 2, BigDecimal.valueOf(-100), BigDecimal.valueOf(300));
			Transaction transaction = transactionDAO.Get(transactionId);
			(new AccountDAO()).SetActive(transaction.AccountId(), false);
			
			Account account = (new AccountDAO()).Get(transaction.AccountId());
			Account accountTo = (new AccountDAO()).Get(transaction.AccountIdTo());
			BigDecimal beforeBalance = account.Balance();
			BigDecimal beforeBalanceTo = accountTo.Balance();
			ExecutionResult result = transactionDAO.Execute(transaction.Id());
			assertEquals(ExecutionResult.TRANSACTION_FAILED, result);

			transaction = transactionDAO.Get(transaction.Id());
			account = (new AccountDAO()).Get(transaction.AccountId());
			accountTo = (new AccountDAO()).Get(transaction.AccountIdTo());
			assertEquals(0, beforeBalance.compareTo(account.Balance()));
			assertEquals(0, beforeBalanceTo.compareTo(accountTo.Balance()));
			
			assertEquals(Transaction.State.TRANSACTION_ACCOUNT_INACTIVE.Code(), transaction.ResultCode());
		} catch (Exception e) {
			e.printStackTrace();
			fail("SQL exception");
		}
	}

	@Test
	public void TransactionInactive2() {
		try {
			long transactionId = transactionDAO.CreateInternal(1, 2, BigDecimal.valueOf(-100), BigDecimal.valueOf(300));
			Transaction transaction = transactionDAO.Get(transactionId);
			(new AccountDAO()).SetActive(transaction.AccountIdTo(), false);
			
			Account account = (new AccountDAO()).Get(transaction.AccountId());
			Account accountTo = (new AccountDAO()).Get(transaction.AccountIdTo());
			BigDecimal beforeBalance = account.Balance();
			BigDecimal beforeBalanceTo = accountTo.Balance();
			ExecutionResult result = transactionDAO.Execute(transaction.Id());
			assertEquals(ExecutionResult.TRANSACTION_FAILED, result);

			transaction = transactionDAO.Get(transaction.Id());
			account = (new AccountDAO()).Get(transaction.AccountId());
			accountTo = (new AccountDAO()).Get(transaction.AccountIdTo());
			assertEquals(0, beforeBalance.compareTo(account.Balance()));
			assertEquals(0, beforeBalanceTo.compareTo(accountTo.Balance()));
			
			assertEquals(Transaction.State.TRANSACTION_ACCOUNT2_INACTIVE.Code(), transaction.ResultCode());
		} catch (Exception e) {
			e.printStackTrace();
			fail("SQL exception");
		}
	}
}
