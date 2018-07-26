package com.restfultransfer.servlet;

import java.util.Vector;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

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
}
