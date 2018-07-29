package com.restfultransfer;

import static org.junit.jupiter.api.Assertions.*;

import java.io.IOException;
import java.util.Vector;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.util.EntityUtils;
import org.junit.jupiter.api.Test;

import com.fasterxml.jackson.core.type.TypeReference;
import com.restfultransfer.data.ExchangeRate;

class ExchangeRateServletTests extends RESTBeforeTest {

    protected String Address(String path) {
    	return super.Address("exchange/" + path);
    }
    

	@Test
	public void Get()
	{
		try {
			HttpResponse response = httpClient.execute(new HttpGet(Address("USD/RUB")));
	        assertEquals(200, response.getStatusLine().getStatusCode());
	        
	    	ExchangeRate rate = mapper.readValue(EntityUtils.toString(response.getEntity()), new TypeReference<ExchangeRate>() {});
	        assertEquals("USD", rate.CurrencyFrom().getCurrencyCode());
	        assertEquals("RUB", rate.CurrencyTo().getCurrencyCode());
	        assertEquals(60.0, rate.Rate());

			response = httpClient.execute(new HttpGet(Address("CNY/RUB")));
	        assertEquals(null, response.getEntity());
	        assertEquals(204, response.getStatusLine().getStatusCode());
		} catch (Exception e) {
			e.printStackTrace();
			fail("Unexpected exception");
		}
	}
	
	@Test
	public void GetAll()
	{
		try {
			HttpResponse response = httpClient.execute(new HttpGet(Address("list")));
	        assertEquals(200, response.getStatusLine().getStatusCode());
	        
	        Vector<ExchangeRate> rates =
	        		mapper.readValue(EntityUtils.toString(response.getEntity()), new TypeReference<Vector<ExchangeRate>>() {});

			String[] from = {"RUB", "USD", "RUB", "EUR", "USD", "EUR"};
			String[] to = {"USD", "RUB", "EUR", "RUB", "EUR", "USD"};

			assertEquals(from.length, rates.size());
			for (int i = 0; i < rates.size(); i++)
			{
				assertEquals(from[i], rates.get(i).CurrencyFrom().getCurrencyCode());
				assertEquals(to[i], rates.get(i).CurrencyTo().getCurrencyCode());
			}
		} catch (Exception e) {
			e.printStackTrace();
			fail("Unexpected Exception");
		}
	}

    int GetExchangeCount() throws ClientProtocolException, IOException {
		HttpResponse response = httpClient.execute(new HttpGet(Address("list")));
    	Vector<ExchangeRate> rates = mapper.readValue(EntityUtils.toString(response.getEntity()), new TypeReference<Vector<ExchangeRate>>() {});
		return rates.size();
    }
    
	@Test
	public void Create()
	{
		try {
			int before = GetExchangeCount();
			HttpResponse response = httpClient.execute(new HttpPost(Address("new/CNY,RUB,9.2")));
	        assertEquals(201, response.getStatusLine().getStatusCode());
	        assertEquals(Address("CNY/RUB"), response.getLastHeader("Location").getValue());
	        
			response = httpClient.execute(new HttpGet(response.getLastHeader("Location").getValue()));
	        ExchangeRate rate = mapper.readValue(EntityUtils.toString(response.getEntity()), new TypeReference<ExchangeRate>() {});
	        assertEquals("CNY", rate.CurrencyFrom().getCurrencyCode());
	        assertEquals("RUB", rate.CurrencyTo().getCurrencyCode());
	        assertEquals(9.2, rate.Rate());

	        int after = GetExchangeCount();
	        assertEquals(before + 1, after);
	        
			response = httpClient.execute(new HttpPost(Address("new/CNY,RUB,1000")));
	        assertEquals(404, response.getStatusLine().getStatusCode());
	        assertEquals(after, GetExchangeCount());
		} catch (Exception e) {
			e.printStackTrace();
			fail("Unexpected Exception");
		}
	}
	
	@Test
	public void Delete()
	{
		try {
			int before = GetExchangeCount();
			HttpResponse response = httpClient.execute(new HttpDelete(Address("delete/RUB,USD")));
	        assertEquals(204, response.getStatusLine().getStatusCode());
	        assertEquals(null, response.getEntity());

	        int after = GetExchangeCount();
	        assertEquals(before - 1, after);

			response = httpClient.execute(new HttpDelete(Address("delete/RUB,USD")));
	        assertEquals(404, response.getStatusLine().getStatusCode());
	        assertEquals(after, GetExchangeCount());
		} catch (Exception e) {
			e.printStackTrace();
			fail("Unexpected Exception");
		}
	}
}
