package com.restfultransfer;

import static org.junit.jupiter.api.Assertions.*;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.Vector;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.util.EntityUtils;
import org.junit.jupiter.api.Test;

import com.fasterxml.jackson.core.type.TypeReference;
import com.restfultransfer.data.Account;
import com.restfultransfer.data.Transaction;

class TransactionServletTests extends RESTBeforeTest {

    protected String Address(String path) {
    	return super.Address("transaction/" + path);
    }
    
    @Test
    public void GetAll() {
    	try {
			HttpResponse response = httpClient.execute(new HttpGet(Address("list")));
	        assertEquals(200, response.getStatusLine().getStatusCode());
	        
	    	Vector<Transaction> transactions =
	    			mapper.readValue(EntityUtils.toString(response.getEntity()), new TypeReference<Vector<Transaction>>() {});

			int[] amounts = {3000 ,300 ,-100 ,100 ,10 ,7000 ,-700 ,-10 ,-100};
			assertEquals(amounts.length, transactions.size());
			for (int i = 0; i < transactions.size(); i++)
				assertEquals(0, transactions.get(i).Amount().compareTo(BigDecimal.valueOf(amounts[i])));
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
	        
	    	Transaction transaction = mapper.readValue(EntityUtils.toString(response.getEntity()), new TypeReference<Transaction>() {});

			assertEquals(1, transaction.Id());
			assertEquals(1, transaction.AccountId());
			assertEquals(0, transaction.AccountIdTo());
			assertEquals(0, transaction.Amount().compareTo(BigDecimal.valueOf(3000)));
			assertEquals(0, transaction.AmountTo().compareTo(BigDecimal.ZERO));
			assertNotEquals(new Timestamp(0), transaction.Created());

			response = httpClient.execute(new HttpGet(Address("0")));
	        assertEquals(null, response.getEntity());
	        assertEquals(204, response.getStatusLine().getStatusCode());
		} catch (Exception e) {
			e.printStackTrace();
			fail("Unexpected exception");
		}
    }
    
	@Test
	public void CreateExternalTransaction() {
		try {
			HttpResponse response = httpClient.execute(new HttpPost(Address("newExt/2,100")));
	        assertEquals(201, response.getStatusLine().getStatusCode());
	        assertEquals(Address("10"), response.getLastHeader("Location").getValue());
	        
			response = httpClient.execute(new HttpGet(response.getLastHeader("Location").getValue()));
	        assertEquals(200, response.getStatusLine().getStatusCode());
	    	Transaction transaction = mapper.readValue(EntityUtils.toString(response.getEntity()), new TypeReference<Transaction>() {});
			assertEquals(10, transaction.Id());
			assertEquals(2, transaction.AccountId());
			assertEquals(0, transaction.AccountIdTo());
			assertEquals(0, transaction.Amount().compareTo(BigDecimal.valueOf(100)));
			assertEquals(0, transaction.AmountTo().compareTo(BigDecimal.ZERO));
			
			response = httpClient.execute(new HttpGet(Address("1")));
	        assertEquals(200, response.getStatusLine().getStatusCode());
	    	Transaction transactionGet = mapper.readValue(EntityUtils.toString(response.getEntity()), new TypeReference<Transaction>() {});
			assertNotEquals(transactionGet.Created(), transaction.Created());

			response = httpClient.execute(new HttpPost(Address("newExt/99,100")));
	        assertEquals(404, response.getStatusLine().getStatusCode());

			response = httpClient.execute(new HttpPost(Address("newExt/99,0")));
	        assertEquals(404, response.getStatusLine().getStatusCode());
		} catch (Exception e) {
			e.printStackTrace();
			fail("Unexpected exception");
		}
	}

	@Test
	public void CreateInternalTransaction() {
		try {
			HttpResponse response = httpClient.execute(new HttpPost(Address("newInt/2,4,30")));
	        assertEquals(201, response.getStatusLine().getStatusCode());
	        assertEquals(Address("10"), response.getLastHeader("Location").getValue());
	        
			response = httpClient.execute(new HttpGet(response.getLastHeader("Location").getValue()));
	        assertEquals(200, response.getStatusLine().getStatusCode());
	    	Transaction transaction = mapper.readValue(EntityUtils.toString(response.getEntity()), new TypeReference<Transaction>() {});
			assertEquals(2, transaction.AccountId());
			assertEquals(4, transaction.AccountIdTo());
			assertEquals(0, transaction.Amount().compareTo(BigDecimal.valueOf(-30)));
			assertEquals(0, transaction.AmountTo().compareTo(BigDecimal.valueOf(25.71428571428571))); //30.0 * 60 / 70

			response = httpClient.execute(new HttpGet(Address("1")));
	        assertEquals(200, response.getStatusLine().getStatusCode());
	    	Transaction transactionGet = mapper.readValue(EntityUtils.toString(response.getEntity()), new TypeReference<Transaction>() {});
			assertNotEquals(transactionGet.Created(), transaction.Created());

			response = httpClient.execute(new HttpPost(Address("newInt/99,1,10")));
	        assertEquals(404, response.getStatusLine().getStatusCode());
	        
			response = httpClient.execute(new HttpPost(Address("newInt/1,-2,10")));
	        assertEquals(404, response.getStatusLine().getStatusCode());
		} catch (Exception e) {
			e.printStackTrace();
			fail("Unexpected exception");
		}
	}

	@Test
	public void CreateInternalTransactionFail() {
		try {
			HttpResponse response = httpClient.execute(new HttpDelete(super.Address("exchange/delete/USD,RUB")));
	        assertEquals(204, response.getStatusLine().getStatusCode());
			
			response = httpClient.execute(new HttpPost(Address("newInt/2,1,30"))); //no exchange rate
	        assertEquals(404, response.getStatusLine().getStatusCode());
	        
			response = httpClient.execute(new HttpPost(Address("newInt/1,2,-1")));
	        assertEquals(404, response.getStatusLine().getStatusCode());
	        
		} catch (Exception e) {
			e.printStackTrace();
			fail("Unexpected exception");
		}
	}

	@Test
	public void CreateInternalTransactionNoExchange() {
		try {
			HttpResponse response = httpClient.execute(new HttpPost(Address("newInt/2,3,30")));
	        assertEquals(201, response.getStatusLine().getStatusCode());
	        assertEquals(Address("10"), response.getLastHeader("Location").getValue());
	        
			response = httpClient.execute(new HttpGet(response.getLastHeader("Location").getValue()));
	        assertEquals(200, response.getStatusLine().getStatusCode());
	    	Transaction transaction = mapper.readValue(EntityUtils.toString(response.getEntity()), new TypeReference<Transaction>() {});
			assertEquals(2, transaction.AccountId());
			assertEquals(3, transaction.AccountIdTo());
			assertEquals(0, transaction.Amount().compareTo(BigDecimal.valueOf(-30)));
			assertEquals(0, transaction.AmountTo().compareTo(BigDecimal.valueOf(30)));
		} catch (Exception e) {
			e.printStackTrace();
			fail("Unexpected exception");
		}
	}

	@Test
	public void Execute() {
		try {
			HttpResponse response = httpClient.execute(new HttpPost(Address("newInt/2,3,10")));
	        assertEquals(201, response.getStatusLine().getStatusCode());
			response = httpClient.execute(new HttpGet(response.getLastHeader("Location").getValue()));
	    	Transaction transaction = mapper.readValue(EntityUtils.toString(response.getEntity()), new TypeReference<Transaction>() {});
	    	
			response = httpClient.execute(new HttpGet(super.Address("account/" + transaction.AccountId())));
	    	Account accountFrom = mapper.readValue(EntityUtils.toString(response.getEntity()), new TypeReference<Account>() {});
			response = httpClient.execute(new HttpGet(super.Address("account/" + transaction.AccountIdTo())));
	    	Account accountTo = mapper.readValue(EntityUtils.toString(response.getEntity()), new TypeReference<Account>() {});

	    	BigDecimal beforeFrom = accountFrom.Balance();
	    	BigDecimal beforeTo = accountTo.Balance();

			response = httpClient.execute(new HttpPut(Address(transaction.Id() + "/execute")));
	        assertEquals(204, response.getStatusLine().getStatusCode());

			response = httpClient.execute(new HttpGet(Address("" + transaction.Id())));
	    	transaction = mapper.readValue(EntityUtils.toString(response.getEntity()), new TypeReference<Transaction>() {});
	        assertEquals(0, transaction.ResultCode());

			response = httpClient.execute(new HttpGet(super.Address("account/" + transaction.AccountId())));
	    	accountFrom = mapper.readValue(EntityUtils.toString(response.getEntity()), new TypeReference<Account>() {});
			response = httpClient.execute(new HttpGet(super.Address("account/" + transaction.AccountIdTo())));
	    	accountTo = mapper.readValue(EntityUtils.toString(response.getEntity()), new TypeReference<Account>() {});
	    	
	        assertEquals(0, accountFrom.Balance().compareTo(beforeFrom.subtract(BigDecimal.TEN)));
	        assertEquals(0, accountTo.Balance().compareTo(beforeTo.add(BigDecimal.TEN)));
	    	
		} catch (Exception e) {
			e.printStackTrace();
			fail("Unexpected exception");
		}
	}
}
