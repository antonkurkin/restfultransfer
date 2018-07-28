package com.restfultransfer.servlet;

import java.sql.SQLException;
import java.util.Currency;
import java.util.Vector;

import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import com.restfultransfer.data.ExchangeRate;
import com.restfultransfer.data.ExchangeRateDAO;

@Path("/exchange")
@Produces(MediaType.APPLICATION_JSON)
public class ExchangeRateServlet {
    @GET
    @Path("/{currencyFrom}/{currencyTo}")
    public ExchangeRate Get(@PathParam("currencyFrom") String currencyFrom, @PathParam("currencyTo") String currencyTo) throws SQLException {
    	return (new ExchangeRateDAO()).Get(Currency.getInstance(currencyFrom), Currency.getInstance(currencyTo));
    }
    
    @GET
    @Path("/list")
    public Vector<ExchangeRate> GetAll() throws SQLException {
    	return (new ExchangeRateDAO()).GetAll();
    }

    @POST
    @Path("/new/{currencyFrom},{currencyTo},{rate}")
    public ExchangeRate Create(@PathParam("currencyFrom") String currencyFrom, @PathParam("currencyTo") String currencyTo, @PathParam("rate") Double rate) throws SQLException {
    	return (new ExchangeRateDAO()).Create(Currency.getInstance(currencyFrom), Currency.getInstance(currencyTo), rate);
    }

    @DELETE
    @Path("/delete/{currencyFrom},{currencyTo}")
    public Response Delete(@PathParam("currencyFrom") String currencyFrom, @PathParam("currencyTo") String currencyTo) throws SQLException {
    	int deleted = (new ExchangeRateDAO()).Delete(Currency.getInstance(currencyFrom), Currency.getInstance(currencyTo));
    	if (deleted > 0)
    		return Response.status(Response.Status.OK).build();
    	return Response.status(Response.Status.NO_CONTENT).build();
    }
}
