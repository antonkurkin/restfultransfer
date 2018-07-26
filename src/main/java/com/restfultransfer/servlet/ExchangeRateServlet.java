package com.restfultransfer.servlet;

import java.util.Currency;
import java.util.Vector;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import com.restfultransfer.data.ExchangeRate;
import com.restfultransfer.data.ExchangeRateDAO;

@Path("/exchange")
@Produces(MediaType.APPLICATION_JSON)
public class ExchangeRateServlet {
    @GET
    @Path("/{CurrencyFrom}/{CurrencyTo}")
    public ExchangeRate Get(@PathParam("CurrencyFrom") String currencyFrom, @PathParam("CurrencyTo") String currencyTo) throws Exception {
    	return (new ExchangeRateDAO()).Get(Currency.getInstance(currencyFrom), Currency.getInstance(currencyTo));
    }
    
    @GET
    @Path("/list")
    public Vector<ExchangeRate> GetAll() throws Exception {
    	return (new ExchangeRateDAO()).GetAll();
    }
}
