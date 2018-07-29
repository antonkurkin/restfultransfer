package com.restfultransfer.servlet;

import java.sql.SQLException;
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
    public Vector<Client> GetAll() throws SQLException {
    	return (new ClientDAO()).GetAll();
    }
    
    @GET
    @Path("/{clientId}")
    public Client Get(@PathParam("clientId") long clientId) throws SQLException {
    	return (new ClientDAO()).Get(clientId);
    }
    
    @GET
    @Path("/{clientId}/accounts")
    public Vector<Account> GetAccounts(@PathParam("clientId") long clientId) throws SQLException {
    	return (new AccountDAO()).GetAllByClient(clientId);
    }

    @GET
    @Path("/byName/{name}")
    public Vector<Client> GetAll(@PathParam("name") String name) throws SQLException {
    	return (new ClientDAO()).GetAllByName(name);
    }
    
    @POST
    @Path("/new/{name}")
    public Client Create(@PathParam("name") String name) throws SQLException {
    	return (new ClientDAO()).Create(name);
    }

    @PUT
    @Path("/{clientId}/setName/{newName}")
    public Response SetName(@PathParam("clientId") long clientId, @PathParam("newName") String newName) throws SQLException {
    	int changed = (new ClientDAO()).ChangeName(clientId, newName);
    	switch (changed)
    	{
    	case  1: return Response.status(Response.Status.NO_CONTENT).build();
    	case  0: return Response.status(Response.Status.NOT_FOUND).build();
    	default: return Response.status(Response.Status.BAD_REQUEST).build();
    	}
    }
    
    @PUT
    @Path("/{clientId}/deactivate")
    public Response Deactivate(@PathParam("clientId") long clientId) throws SQLException {
    	int changed = (new ClientDAO()).SetActive(clientId, false);
    	(new AccountDAO()).DeactivateByClientId(clientId);
    	switch (changed)
    	{
    	case  1: return Response.status(Response.Status.NO_CONTENT).build();
    	case  0: return Response.status(Response.Status.NOT_FOUND).build();
    	default: return Response.status(Response.Status.BAD_REQUEST).build();
    	}
    }

    @PUT
    @Path("/{clientId}/activate")
    public Response Activate(@PathParam("clientId") long clientId) throws SQLException {
    	int changed = (new ClientDAO()).SetActive(clientId, true);
    	switch (changed)
    	{
    	case  1: return Response.status(Response.Status.NO_CONTENT).build();
    	case  0: return Response.status(Response.Status.NOT_FOUND).build();
    	default: return Response.status(Response.Status.BAD_REQUEST).build();
    	}
    }
}
