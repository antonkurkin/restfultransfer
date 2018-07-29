package com.restfultransfer.servlet;

import com.restfultransfer.data.Account;
import com.restfultransfer.data.AccountDAO;
import com.restfultransfer.data.Transaction;
import com.restfultransfer.data.TransactionDAO;

import java.net.URI;
import java.net.URISyntaxException;
import java.sql.SQLException;
import java.util.Currency;
import java.util.Vector;

import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path("/account")
@Produces(MediaType.APPLICATION_JSON)
public class AccountServlet {
    @GET
    @Path("/list")
    public Vector<Account> GetAll() throws SQLException {
    	return (new AccountDAO()).GetAll();
    }
    
    @GET
    @Path("/{accountId}")
    public Account Get(@PathParam("accountId") long accountId) throws SQLException {
    	return (new AccountDAO()).Get(accountId);
    }
    
    @GET
    @Path("/{accountId}/transactions")
    public Vector<Transaction> GetTransactions(@PathParam("accountId") long accountId) throws SQLException {
    	return (new TransactionDAO()).GetAllByAccount(accountId);
    }

    @POST
    @Path("/new/{clientId},{currency}")
    public Response Create(@PathParam("clientId") long clientId, @PathParam("currency") String currency) throws SQLException, URISyntaxException {
    	//right now it is possible to create active account for inactive client
    	//I planned to force it by database constraint, but didn't get to it
    	long accountId = (new AccountDAO()).Create(clientId, Currency.getInstance(currency));
    	if (accountId == 0)
    		return Response.status(Response.Status.NOT_FOUND).build();
    	URI accountURI = new URI("account/" + accountId);
        return Response.created(accountURI).build();
    }
    
    public Response SetActive(long accountId, boolean active) throws SQLException {
    	//right now it is possible to create active account for inactive client
    	//I planned to force it by database constraint, but didn't get to it
    	int changed = (new AccountDAO()).SetActive(accountId, active);
    	switch (changed)
    	{
    	case  1: return Response.status(Response.Status.NO_CONTENT).build();
    	case  0: return Response.status(Response.Status.NOT_FOUND).build();
    	default: return Response.status(Response.Status.BAD_REQUEST).build();
    	}
    }
    
    @PUT
    @Path("/{accountId}/deactivate")
    public Response Deactivate(@PathParam("accountId") long accountId) throws SQLException {
    	return SetActive(accountId, false);
    }

    @PUT
    @Path("/{accountId}/activate")
    public Response Activate(@PathParam("accountId") long accountId) throws SQLException {
    	return SetActive(accountId, true);
    }
    
}
