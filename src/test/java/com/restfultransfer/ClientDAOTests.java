package com.restfultransfer;

import static org.junit.jupiter.api.Assertions.*;

import java.sql.Timestamp;
import java.util.Vector;

import org.junit.jupiter.api.Test;

import com.restfultransfer.data.Client;
import com.restfultransfer.data.ClientDAO;

class ClientDAOTests extends DBBeforeLoad {

	ClientDAO clientDAO = new ClientDAO();
	
	@Test
	public void GetClient() {
		try {
			Client client = clientDAO.Get(2);
			assertEquals(2, client.Id());
			assertEquals("alex", client.Name());
			assertEquals(true, client.isActive());
			assertNotEquals(new Timestamp(0), client.Created());

			client = clientDAO.Get(99);
			assertEquals(null, client);
		} catch (Exception e) {
			e.printStackTrace();
			fail("SQL exception");
		}
	}

	@Test
	public void ChangeClientName() {
		try {
			Client client = clientDAO.Get(1);
			assertEquals("anton", client.Name());
			int changed = clientDAO.ChangeName(client.Id(), "dmitry");
			assertEquals(1, changed);
			client = clientDAO.Get(client.Id());
			assertEquals("dmitry", client.Name());

			changed = clientDAO.ChangeName(99, "dmitry");
			assertEquals(0, changed);

			changed = clientDAO.ChangeName(2, "");
			assertEquals(0, changed);

			client = clientDAO.Get(2);
			assertEquals("alex", client.Name());
		} catch (Exception e) {
			e.printStackTrace();
			fail("SQL exception");
		}
	}

	@Test
	public void GetClientByName() {
		try {
			Vector<Client> clients = clientDAO.GetAllByName("dmitry");
			assertEquals(0, clients.size());
			
			clients = clientDAO.GetAllByName("anton");
			assertEquals(1, clients.size());
			clientDAO.ChangeName(1, "alex");
			clients = clientDAO.GetAllByName("alex");
			assertEquals(2, clients.size());
		} catch (Exception e) {
			e.printStackTrace();
			fail("SQL exception");
		}
	}

	@Test
	public void ActivateClient() {
		try {
			Client client = clientDAO.Get(1);
			assertEquals(true, client.isActive());

			int changed = clientDAO.SetActive(client.Id(), false);
			assertEquals(1, changed);
			client = clientDAO.Get(client.Id());
			assertEquals(false, client.isActive());

			changed = clientDAO.SetActive(client.Id(), true);
			assertEquals(1, changed);
			client = clientDAO.Get(client.Id());
			assertEquals(true, client.isActive());

			changed = clientDAO.SetActive(99, true);
			assertEquals(0, changed);
		} catch (Exception e) {
			e.printStackTrace();
			fail("SQL exception");
		}
	}

	@Test
	public void CreateClient() {
		try {
			Client clientNew = clientDAO.Create("dmitry");
			assertEquals("dmitry", clientNew.Name());
			
			Client clientGet = clientDAO.Get(clientNew.Id());
			assertEquals(clientNew.Name(), clientGet.Name());
			assertEquals(clientNew.Created(), clientGet.Created());

			clientGet = clientDAO.Get(1);
			assertNotEquals(clientGet.Created(), clientNew.Created());
			
			int before = clientDAO.GetAll().size();
			
			clientNew = clientDAO.Create("");
			assertEquals(null, clientNew);
			
			assertEquals(before, clientDAO.GetAll().size());
		} catch (Exception e) {
			e.printStackTrace();
			fail("SQL exception");
		}
	}

	@Test
	public void GetAllClients() {
		try {
			String[] names = {"anton", "alex", "peter", "anna"};
			Vector<Client> clients = clientDAO.GetAll();
			
			assertEquals(names.length, clients.size());
			for (int i = 0; i < clients.size(); i++)
				assertEquals(names[i], clients.get(i).Name());
		} catch (Exception e) {
			e.printStackTrace();
			fail("SQL exception");
		}
	}
}
