package com.restfultransfer.servlet;

import com.restfultransfer.data.Account;
import com.restfultransfer.data.AccountDAO;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

@Path("/account")
@Produces(MediaType.APPLICATION_JSON)
public class AccountServlet {
    @GET
    @Path("/{accountId}")
    public Account Get(@PathParam("accountId") long accountId) throws Exception {
    	return AccountDAO.Get(accountId);
    }
}
