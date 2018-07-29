package com.restfultransfer;

import static org.junit.jupiter.api.Assertions.*;

import java.util.Vector;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.util.EntityUtils;
import org.junit.jupiter.api.Test;

import com.fasterxml.jackson.core.type.TypeReference;
import com.restfultransfer.data.Client;

class ClientServletTests extends RESTBeforeTest{

    protected String Address(String path) {
    	return super.Address("client/" + path);
    }

	@Test
	public void Get()
	{
		try {
			HttpResponse response = httpClient.execute(new HttpGet(Address("1")));
	        assertEquals(200, response.getStatusLine().getStatusCode());
	        
	    	Client client =
	    			mapper.readValue(EntityUtils.toString(response.getEntity()), new TypeReference<Client>() {});
	        assertEquals(1, client.Id());
	        assertEquals("anton", client.Name());

			response = httpClient.execute(new HttpGet(Address("-1")));
	        assertEquals(null, response.getEntity());
	        assertEquals(204, response.getStatusLine().getStatusCode());
		} catch (Exception e) {
			e.printStackTrace();
			fail("SQL exception");
		}
	}
	
	@Test
	public void GetAll()
	{
		try {
			HttpResponse response = httpClient.execute(new HttpGet(Address("list")));
	        assertEquals(200, response.getStatusLine().getStatusCode());
	        
	        Vector<Client> clients =
	        		mapper.readValue(EntityUtils.toString(response.getEntity()), new TypeReference<Vector<Client>>() {});

			String[] names = {"anton", "alex", "peter", "anna"};
			assertEquals(names.length, clients.size());
			for (int i = 0; i < clients.size(); i++)
				assertEquals(names[i], clients.get(i).Name());
		} catch (Exception e) {
			e.printStackTrace();
			fail("SQL exception");
		}
	}

}
