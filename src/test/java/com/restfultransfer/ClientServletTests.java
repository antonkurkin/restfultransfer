package com.restfultransfer;

import static org.junit.jupiter.api.Assertions.*;

import java.io.IOException;
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
			fail("Unexpected exception");
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
			fail("Unexpected Exception");
		}
	}

	@Test
	public void GetAccountsByClient()
	{
		try {
			HttpResponse response = httpClient.execute(new HttpGet(Address("2/accounts")));
	        assertEquals(200, response.getStatusLine().getStatusCode());
	        
	        Vector<Account> accounts =
	        		mapper.readValue(EntityUtils.toString(response.getEntity()), new TypeReference<Vector<Account>>() {});
			assertEquals(3, accounts.size());

			response = httpClient.execute(new HttpGet(Address("99/accounts")));
	        assertEquals(null, response.getEntity());
	        assertEquals(204, response.getStatusLine().getStatusCode());
		} catch (Exception e) {
			e.printStackTrace();
			fail("Unexpected Exception");
		}
	}

	@Test
	public void GetAllByName()
	{
		try {
			HttpResponse response = httpClient.execute(new HttpGet(Address("byName/anton")));
	        assertEquals(200, response.getStatusLine().getStatusCode());
	        
	        Vector<Client> clients =
	        		mapper.readValue(EntityUtils.toString(response.getEntity()), new TypeReference<Vector<Client>>() {});

			assertEquals(1, clients.size());
			for (int i = 0; i < clients.size(); i++)
				assertEquals("anton", clients.get(i).Name());

			httpClient.execute(new HttpPut(Address("2/setName/anton")));
			
			response = httpClient.execute(new HttpGet(Address("byName/anton")));
	        assertEquals(200, response.getStatusLine().getStatusCode());
	        
	        clients =
	        		mapper.readValue(EntityUtils.toString(response.getEntity()), new TypeReference<Vector<Client>>() {});

			assertEquals(2, clients.size());
			for (int i = 0; i < clients.size(); i++)
				assertEquals("anton", clients.get(i).Name());
			
			response = httpClient.execute(new HttpGet(Address("byName/john")));
	        assertEquals(204, response.getStatusLine().getStatusCode());
	        assertEquals(null, response.getEntity());
		} catch (Exception e) {
			e.printStackTrace();
			fail("Unexpected Exception");
		}
	}

	@Test
	public void CreateClient() {
		try {
			HttpResponse response = httpClient.execute(new HttpPost(Address("new/dmitry")));
	        assertEquals(201, response.getStatusLine().getStatusCode());
	        assertEquals(Address("5"), response.getLastHeader("Location").getValue());

			response = httpClient.execute(new HttpGet(response.getLastHeader("Location").getValue()));
	        assertEquals(200, response.getStatusLine().getStatusCode());
	        Client clientNew =
	        		mapper.readValue(EntityUtils.toString(response.getEntity()), new TypeReference<Client>() {});
	        
			assertEquals("dmitry", clientNew.Name());
			assertEquals(5, clientNew.Id());

			response = httpClient.execute(new HttpGet(Address(clientNew.Id() + "")));
	        assertEquals(200, response.getStatusLine().getStatusCode());
	    	Client clientGet =
	    			mapper.readValue(EntityUtils.toString(response.getEntity()), new TypeReference<Client>() {});
	    	
			assertEquals(clientNew.Name(), clientGet.Name());
			assertEquals(clientNew.Created(), clientGet.Created());

			response = httpClient.execute(new HttpGet(Address("1")));
	        assertEquals(200, response.getStatusLine().getStatusCode());
	    	clientGet =
	    			mapper.readValue(EntityUtils.toString(response.getEntity()), new TypeReference<Client>() {});
			assertNotEquals(clientGet.Created(), clientNew.Created());

		} catch (Exception e) {
			e.printStackTrace();
			fail("Unexpected Exception");
		}
	}
	
	@Test
	public void ChangeName()
	{
		try {
			HttpResponse response = httpClient.execute(new HttpPut(Address("2/setName/dmitry")));
	        assertEquals(204, response.getStatusLine().getStatusCode());
	        assertEquals(null, response.getEntity());

			response = httpClient.execute(new HttpPut(Address("99/setName/dmitry")));
	        assertEquals(404, response.getStatusLine().getStatusCode());
		} catch (Exception e) {
			e.printStackTrace();
			fail("Unexpected Exception");
		}
	}

	public void CheckAccounts(Client client, boolean active) throws ClientProtocolException, IOException {
		HttpResponse response = httpClient.execute(new HttpGet(Address(client.Id() + "/accounts")));
        assertEquals(200, response.getStatusLine().getStatusCode());
        Vector<Account> accounts =
        		mapper.readValue(EntityUtils.toString(response.getEntity()), new TypeReference<Vector<Account>>() {});
		assertEquals(3, accounts.size());
		for (Account account : accounts)
			assertEquals(active, account.isActive());
	}
	
	@Test
	public void ClientActivate()
	{
		try {
			HttpResponse responseGet = httpClient.execute(new HttpGet(Address("2")));
	    	Client client = mapper.readValue(EntityUtils.toString(responseGet.getEntity()), new TypeReference<Client>() {});
	    	assertEquals(true, client.isActive());
	    	
	    	CheckAccounts(client, true);
			HttpResponse response = httpClient.execute(new HttpPut(Address(client.Id() + "/deactivate")));
	        assertEquals(204, response.getStatusLine().getStatusCode());
	        assertEquals(null, response.getEntity());

	        responseGet = httpClient.execute(new HttpGet(Address(client.Id() + "")));
	    	client = mapper.readValue(EntityUtils.toString(responseGet.getEntity()), new TypeReference<Client>() {});
	    	assertEquals(false, client.isActive());
	    	CheckAccounts(client, false);

			response = httpClient.execute(new HttpPut(Address(client.Id() + "/activate")));
	        assertEquals(204, response.getStatusLine().getStatusCode());
	        assertEquals(null, response.getEntity());
	    	CheckAccounts(client, false);
	        
	        responseGet = httpClient.execute(new HttpGet(Address(client.Id() + "")));
	    	client = mapper.readValue(EntityUtils.toString(responseGet.getEntity()), new TypeReference<Client>() {});
	    	assertEquals(true, client.isActive());
	        
			response = httpClient.execute(new HttpPut(Address("99/deactivate")));
	        assertEquals(404, response.getStatusLine().getStatusCode());
	        
			response = httpClient.execute(new HttpPut(Address("0/activate")));
	        assertEquals(404, response.getStatusLine().getStatusCode());
		} catch (Exception e) {
			e.printStackTrace();
			fail("Unexpected Exception");
		}
	}
}
