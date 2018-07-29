package com.restfultransfer.servlet;

import java.math.BigDecimal;
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

import com.restfultransfer.data.Account;
import com.restfultransfer.data.AccountDAO;
import com.restfultransfer.data.ExchangeRate;
import com.restfultransfer.data.ExchangeRateDAO;
import com.restfultransfer.data.Transaction;
import com.restfultransfer.data.TransactionDAO;
import com.restfultransfer.data.TransactionDAO.ExecutionResult;

@Path("/transaction")
@Produces(MediaType.APPLICATION_JSON)
public class TransactionServlet {	
    @GET
    @Path("/{transactionId}")
    public Transaction Get(@PathParam("transactionId") long transactionId) throws SQLException {
    	return (new TransactionDAO()).Get(transactionId);
    }
    
    @GET
    @Path("/list")
    public Vector<Transaction> GetAll() throws SQLException {
    	return (new TransactionDAO()).GetAll();
    }

    @POST
    @Path("/newExt/{accountId},{amount}")
    public Transaction CreateExternal(@PathParam("accountId") long accountId, @PathParam("amount") BigDecimal amount) throws SQLException {
    	return (new TransactionDAO()).CreateExternal(accountId, amount);
    }
    
    @POST
    @Path("/newInt/{accountIdFrom},{accountIdTo},{amount}")
    public Transaction CreateInternal(@PathParam("accountIdFrom") long accountIdFrom, @PathParam("accountIdTo") long accountIdTo, @PathParam("amount") BigDecimal amount) throws SQLException {
		Account accountFrom = (new AccountDAO()).Get(accountIdFrom);
		Account accountTo = (new AccountDAO()).Get(accountIdTo);
		if (accountFrom == null || accountTo == null)
			return null;
		BigDecimal amountTo;
		if (accountFrom.Currency() != accountTo.Currency())
		{
			ExchangeRate rate = (new ExchangeRateDAO()).Get(accountFrom.Currency(), accountTo.Currency());
			if (rate == null)
				return null;
			amountTo = rate.Exchange(amount);
		}
		else
			amountTo = amount;
		BigDecimal amountFrom = amount.negate();
		
    	return (new TransactionDAO()).CreateInternal(accountIdFrom, accountIdTo, amountFrom, amountTo);
    }
    
    @PUT
    @Path("/{transactionId}/execute")
    public Response Execute(@PathParam("transactionId") long transactionId) throws SQLException {
    	ExecutionResult result = (new TransactionDAO()).Execute(transactionId);
    	switch (result)
    	{
    	case TRANSACTION_NOT_FOUND:
        	return Response.status(Response.Status.NOT_FOUND).build();
    	case TRANSACTION_ALREADY_EXECUTED:
        	return Response.status(Response.Status.NOT_MODIFIED).build();
    	case TRANSACTION_FAILED:
    		return Response.status(Response.Status.BAD_REQUEST).build();
		default:
	    	return Response.status(Response.Status.NO_CONTENT).build();
    	}
    }
}
