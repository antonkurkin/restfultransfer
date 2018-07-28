package com.restfultransfer;

import static org.junit.jupiter.api.Assertions.*;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.Currency;
import java.util.Vector;

import org.junit.jupiter.api.Test;

import com.restfultransfer.data.Account;
import com.restfultransfer.data.AccountDAO;

class AccountDAOTests extends DBBeforeLoad{

	AccountDAO accountDAO = new AccountDAO();
	
	@Test
	public void GetAccount() {
		try {
			Account account = accountDAO.Get(4);
			assertEquals(4, account.Id());
			assertEquals(2, account.ClientId());
			assertEquals("EUR", account.Currency().getCurrencyCode());
			assertEquals(0, account.Balance().compareTo(BigDecimal.valueOf(100)));
			assertEquals(true, account.isActive());
			assertNotEquals(new Timestamp(0), account.Created());
			
			account = accountDAO.Get(-1);
			assertEquals(null, account);
		} catch (Exception e) {
			e.printStackTrace();
			fail("SQL exception");
		}
	}

	@Test
	public void GetAccountsByClient() {
		try {
			Vector<Account> accounts = accountDAO.GetAllByClient(2);
			assertEquals(3, accounts.size());
			
			accounts = accountDAO.GetAllByClient(3);
			assertEquals(2, accounts.size());
			
			accounts = accountDAO.GetAllByClient(99);
			assertEquals(0, accounts.size());
		} catch (Exception e) {
			e.printStackTrace();
			fail("SQL exception");
		}
	}

	@Test
	public void ActivateAccount() {
		try {
			Account account = accountDAO.Get(1);
			assertEquals(true, account.isActive());

			int changed = accountDAO.SetActive(account.Id(), false);
			assertEquals(1, changed);
			account = accountDAO.Get(account.Id());
			assertEquals(false, account.isActive());

			changed = accountDAO.SetActive(account.Id(), true);
			assertEquals(1, changed);
			account = accountDAO.Get(account.Id());
			assertEquals(true, account.isActive());

			changed = accountDAO.SetActive(99, true);
			assertEquals(0, changed);
		} catch (Exception e) {
			e.printStackTrace();
			fail("SQL exception");
		}
	}
	
	@Test
	public void CreateAccount() {
		try {
			int before = accountDAO.GetAll().size();
			Account accountNew = accountDAO.Create(3, Currency.getInstance("RUB"));
			assertEquals(9, accountNew.Id());
			assertEquals(3, accountNew.ClientId());
			assertEquals("RUB", accountNew.Currency().getCurrencyCode());
			assertEquals(before + 1, accountDAO.GetAll().size());
			
			Account accountGet = accountDAO.Get(accountNew.Id());
			assertEquals(accountNew.Currency(), accountGet.Currency());
			assertEquals(accountNew.Created(), accountGet.Created());

			before = accountDAO.GetAll().size();
			
			accountGet = accountDAO.Get(1);
			assertNotEquals(accountGet.Created(), accountNew.Created());
			
			accountNew = accountDAO.Create(99, Currency.getInstance("CNY"));
			assertEquals(null, accountNew);
			
			assertEquals(before, accountDAO.GetAll().size());
		} catch (Exception e) {
			e.printStackTrace();
			fail("SQL exception");
		}
	}

	@Test
	public void GetAllAccounts() {
		try {
			String[] currencies = {"RUB", "USD", "USD", "EUR", "EUR", "RUB", "EUR", "RUB"};
			Vector<Account> accounts = accountDAO.GetAll();
			
			assertEquals(currencies.length, accounts.size());
			for (int i = 0; i < accounts.size(); i++)
				assertEquals(currencies[i], accounts.get(i).Currency().getCurrencyCode());
		} catch (Exception e) {
			e.printStackTrace();
			fail("SQL exception");
		}
	}
}
