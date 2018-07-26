package com.restfultransfer.servlet;

import com.restfultransfer.data.Account;
import com.restfultransfer.data.AccountDAO;
import com.restfultransfer.data.Transaction;
import com.restfultransfer.data.TransactionDAO;

import java.util.Currency;
import java.util.Vector;

import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

@Path("/account")
@Produces(MediaType.APPLICATION_JSON)
public class AccountServlet {
    @GET
    @Path("/list")
    public Vector<Account> GetAll() throws Exception {
    	return (new AccountDAO()).GetAll();
    }
    
    @GET
    @Path("/{accountId}")
    public Account Get(@PathParam("accountId") long accountId) throws Exception {
    	return (new AccountDAO()).Get(accountId);
    }
    
    @GET
    @Path("/{accountId}/transactions")
    public Vector<Transaction> GetTransactions(@PathParam("accountId") long accountId) throws Exception {
    	return (new TransactionDAO()).GetAllByAccount(accountId);
    }

    @POST
    @Path("/new/{clientId},{currency}")
    public Account Create(@PathParam("clientId") long clientId, @PathParam("currency") String currency) throws Exception {
    	return (new AccountDAO()).Create(clientId, Currency.getInstance(currency));
    }
}
