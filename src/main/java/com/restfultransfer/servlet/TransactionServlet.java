package com.restfultransfer.servlet;

import java.math.BigDecimal;
import java.util.Vector;

import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import com.restfultransfer.data.Account;
import com.restfultransfer.data.AccountDAO;
import com.restfultransfer.data.ExchangeRateDAO;
import com.restfultransfer.data.Transaction;
import com.restfultransfer.data.TransactionDAO;

@Path("/transaction")
@Produces(MediaType.APPLICATION_JSON)
public class TransactionServlet {
    @GET
    @Path("/{transactionId}")
    public Transaction Get(@PathParam("transactionId") long clientId) throws Exception {
    	return (new TransactionDAO()).Get(clientId);
    }
    
    @GET
    @Path("/list")
    public Vector<Transaction> GetAll() throws Exception {
    	return (new TransactionDAO()).GetAll();
    }

    @POST
    @Path("/newExt/{accountId},{amount}")
    public Transaction CreateExternal(@PathParam("accountId") long accountId, @PathParam("amount") BigDecimal amount) throws Exception {
    	return (new TransactionDAO()).CreateExternal(accountId, amount);
    }
    
    @POST
    @Path("/newInt/{accountIdFrom},{accountIdTo},{amount}")
    public Transaction CreateInternal(@PathParam("accountIdFrom") long accountIdFrom, @PathParam("accountIdTo") long accountIdTo, @PathParam("amount") BigDecimal amount) throws Exception {
		Account accountFrom = (new AccountDAO()).Get(accountIdFrom);
		Account accountTo = (new AccountDAO()).Get(accountIdTo);
		BigDecimal amountTo;
		if (accountFrom.Currency() != accountTo.Currency())
			amountTo = (new ExchangeRateDAO()).Get(accountFrom.Currency(), accountTo.Currency()).Exchange(amount);
		else
			amountTo = amount;
		BigDecimal amountFrom = amount.negate();
		
    	return (new TransactionDAO()).CreateInternal(accountIdFrom, accountIdTo, amountFrom, amountTo);
    }
}
