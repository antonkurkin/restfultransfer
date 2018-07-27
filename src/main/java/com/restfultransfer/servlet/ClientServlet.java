package com.restfultransfer.servlet;

import java.util.Vector;

import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import com.restfultransfer.data.Client;
import com.restfultransfer.data.ClientDAO;
import com.restfultransfer.data.Account;
import com.restfultransfer.data.AccountDAO;

@Path("/client")
@Produces(MediaType.APPLICATION_JSON)
public class ClientServlet {
    @GET
    @Path("/list")
    public Vector<Client> GetAll() throws Exception {
    	return (new ClientDAO()).GetAll();
    }
    
    @GET
    @Path("/{clientId}")
    public Client Get(@PathParam("clientId") long clientId) throws Exception {
    	return (new ClientDAO()).Get(clientId);
    }
    
    @GET
    @Path("/{clientId}/accounts")
    public Vector<Account> GetAccounts(@PathParam("clientId") long clientId) throws Exception {
    	return (new AccountDAO()).GetAllByClient(clientId);
    }

    @GET
    @Path("/byName/{name}")
    public Vector<Client> GetAll(@PathParam("name") String name) throws Exception {
    	return (new ClientDAO()).GetAllByName(name);
    }
    
    @POST
    @Path("/new/{name}")
    public Client Create(@PathParam("name") String name) throws Exception {
    	return (new ClientDAO()).Create(name);
    }

    @PUT
    @Path("/{clientId}/setName/{newName}")
    public Response SetName(@PathParam("clientId") long clientId, @PathParam("newName") String newName) throws Exception {
    	ClientDAO clientDAO = new ClientDAO();
    	Client client = clientDAO.Get(clientId);
    	if (client == null)
        	return Response.status(Response.Status.NOT_FOUND).build();
    	int changed = clientDAO.ChangeName(client, newName);
    	if (changed != 1)
        	return Response.status(Response.Status.NOT_FOUND).build();
    	return Response.status(Response.Status.NO_CONTENT).build();
    }
    
    @PUT
    @Path("/{clientId}/deactivate")
    public Response Deactivate(@PathParam("clientId") long clientId) throws Exception {
    	int changed = (new ClientDAO()).SetActive(clientId, false);
    	if (changed != 1)
        	return Response.status(Response.Status.NOT_FOUND).build();
		AccountDAO accountDAO = new AccountDAO();
		for (Account account : accountDAO.GetAllByClient(clientId))
			accountDAO.SetActive(account.Id(), false);
    	return Response.status(Response.Status.NO_CONTENT).build();
    }

    @PUT
    @Path("/{clientId}/activate")
    public Response Activate(@PathParam("clientId") long clientId) throws Exception {
    	int changed = (new ClientDAO()).SetActive(clientId, true);
    	if (changed != 1)
        	return Response.status(Response.Status.NOT_FOUND).build();
    	return Response.status(Response.Status.NO_CONTENT).build();
    }
}
