package com.restfultransfer.servlet;

import java.util.Vector;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import com.restfultransfer.data.Client;
import com.restfultransfer.data.ClientDAO;

@Path("/client")
@Produces(MediaType.APPLICATION_JSON)
public class ClientServlet {
    @GET
    @Path("/{clientId}")
    public Client Get(@PathParam("clientId") long clientId) throws Exception {
    	return ClientDAO.Get(clientId);
    }
    
    @GET
    @Path("/list")
    public Vector<Client> GetAll() throws Exception {
    	return ClientDAO.GetAll();
    }
}
