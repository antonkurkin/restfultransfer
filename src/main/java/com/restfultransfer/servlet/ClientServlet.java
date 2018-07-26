package com.restfultransfer.servlet;

import java.util.Vector;

import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

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
}
