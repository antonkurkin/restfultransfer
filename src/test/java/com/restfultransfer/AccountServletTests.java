package com.restfultransfer;

import static org.junit.jupiter.api.Assertions.*;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.Vector;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.util.EntityUtils;
import org.junit.jupiter.api.Test;

import com.fasterxml.jackson.core.type.TypeReference;
import com.restfultransfer.data.Account;
import com.restfultransfer.data.Transaction;

class AccountServletTests extends RESTBeforeTest{

    protected String Address(String path) {
    	return super.Address("account/" + path);
    }
    
    @Test
    public void GetAll() {
    	try {
			HttpResponse response = httpClient.execute(new HttpGet(Address("list")));
	        assertEquals(200, response.getStatusLine().getStatusCode());
	        
	    	Vector<Account> accounts =
	    			mapper.readValue(EntityUtils.toString(response.getEntity()), new TypeReference<Vector<Account>>() {});

			String[] currencies = {"RUB", "USD", "USD", "EUR", "EUR", "RUB", "EUR", "RUB"};
			assertEquals(currencies.length, accounts.size());
			for (int i = 0; i < accounts.size(); i++)
				assertEquals(currencies[i], accounts.get(i).Currency().getCurrencyCode());
		} catch (Exception e) {
			e.printStackTrace();
			fail("Unexpected exception");
		}
    }
    
    @Test
    public void Get() {
		try {
			HttpResponse response = httpClient.execute(new HttpGet(Address("1")));
	        assertEquals(200, response.getStatusLine().getStatusCode());
	        
	    	Account account =
	    			mapper.readValue(EntityUtils.toString(response.getEntity()), new TypeReference<Account>() {});
	        assertEquals(1, account.Id());
	        assertEquals(1, account.ClientId());
	        assertEquals("RUB", account.Currency().getCurrencyCode());
	        assertEquals(0, account.Balance().compareTo(BigDecimal.valueOf(10000)));

			response = httpClient.execute(new HttpGet(Address("0")));
	        assertEquals(null, response.getEntity());
	        assertEquals(204, response.getStatusLine().getStatusCode());
		} catch (Exception e) {
			e.printStackTrace();
			fail("Unexpected exception");
		}
    }

    @Test
    public void GetTransactionsByAccount() {
		try {
			HttpResponse response = httpClient.execute(new HttpGet(Address("2/transactions")));
	        assertEquals(200, response.getStatusLine().getStatusCode());
	        
	    	Vector<Transaction> transactions =
	    			mapper.readValue(EntityUtils.toString(response.getEntity()), new TypeReference<Vector<Transaction>>() {});
	        assertEquals(3, transactions.size());

			response = httpClient.execute(new HttpGet(Address("-99/transactions")));
	        assertEquals(204, response.getStatusLine().getStatusCode());
	        assertEquals(null, response.getEntity());
		} catch (Exception e) {
			e.printStackTrace();
			fail("Unexpected exception");
		}
    }
    
    int GetAccountCount() throws ClientProtocolException, IOException {
		HttpResponse response = httpClient.execute(new HttpGet(Address("list")));
    	Vector<Account> accounts =
    			mapper.readValue(EntityUtils.toString(response.getEntity()), new TypeReference<Vector<Account>>() {});
		return accounts.size();
    }
    
    @Test
	public void CreateAccount() {
		try {
			int before = GetAccountCount();
			
			HttpResponse response = httpClient.execute(new HttpPost(Address("new/1,USD")));
	        assertEquals(201, response.getStatusLine().getStatusCode());
	        assertEquals(Address("9"), response.getLastHeader("Location").getValue());

			response = httpClient.execute(new HttpGet(response.getLastHeader("Location").getValue()));
	        assertEquals(200, response.getStatusLine().getStatusCode());
	        Account accountNew =
	        		mapper.readValue(EntityUtils.toString(response.getEntity()), new TypeReference<Account>() {});

			assertEquals(9, accountNew.Id());
			assertEquals(1, accountNew.ClientId());
			assertEquals("USD", accountNew.Currency().getCurrencyCode());
			assertEquals(0, accountNew.Balance().compareTo(BigDecimal.ZERO));

			response = httpClient.execute(new HttpGet(Address("1")));
	        assertEquals(200, response.getStatusLine().getStatusCode());
	    	Account accountGet =
	    			mapper.readValue(EntityUtils.toString(response.getEntity()), new TypeReference<Account>() {});
			assertNotEquals(accountGet.Created(), accountNew.Created());

			int after = GetAccountCount();
	        assertEquals(before + 1, after);
	    	
			response = httpClient.execute(new HttpPost(Address("new/99,USD")));
	        assertEquals(404, response.getStatusLine().getStatusCode());

	        assertEquals(after, GetAccountCount());
		} catch (Exception e) {
			e.printStackTrace();
			fail("Unexpected Exception");
		}
	}
    
	@Test
	public void AccountActivate()
	{
		try {
			HttpResponse responseGet = httpClient.execute(new HttpGet(Address("2")));
	    	Account account = mapper.readValue(EntityUtils.toString(responseGet.getEntity()), new TypeReference<Account>() {});
	    	assertEquals(true, account.isActive());
	    	
			HttpResponse response = httpClient.execute(new HttpPut(Address(account.Id() + "/deactivate")));
	        assertEquals(204, response.getStatusLine().getStatusCode());
	        assertEquals(null, response.getEntity());

	        responseGet = httpClient.execute(new HttpGet(Address(account.Id() + "")));
	        account = mapper.readValue(EntityUtils.toString(responseGet.getEntity()), new TypeReference<Account>() {});
	    	assertEquals(false, account.isActive());

			response = httpClient.execute(new HttpPut(Address(account.Id() + "/activate")));
	        assertEquals(204, response.getStatusLine().getStatusCode());
	        assertEquals(null, response.getEntity());
	        
	        responseGet = httpClient.execute(new HttpGet(Address(account.Id() + "")));
	        account = mapper.readValue(EntityUtils.toString(responseGet.getEntity()), new TypeReference<Account>() {});
	    	assertEquals(true, account.isActive());
	        
			response = httpClient.execute(new HttpPut(Address("-1/deactivate")));
	        assertEquals(404, response.getStatusLine().getStatusCode());
	        
			response = httpClient.execute(new HttpPut(Address("0/activate")));
	        assertEquals(404, response.getStatusLine().getStatusCode());
		} catch (Exception e) {
			e.printStackTrace();
			fail("Unexpected Exception");
		}
	}
}
