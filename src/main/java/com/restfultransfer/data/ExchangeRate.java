package com.restfultransfer.data;

import java.util.Currency;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.math.BigDecimal;

public class ExchangeRate {

    @JsonProperty()
	private Currency currencyFrom;
    @JsonProperty()
	private Currency currencyTo;
    @JsonProperty()
	private double   rate;

	public ExchangeRate(
			Currency currencyFrom,
			Currency currencyTo,
			double rate) {
		this.currencyFrom = currencyFrom;
		this.currencyTo = currencyTo;
		this.rate = rate;
	}

	public Currency CurrencyFrom() { return currencyFrom; }
	public Currency CurrencyTo()   { return currencyTo; }
	public double Rate()           { return rate; }
	
	public BigDecimal Exchange(BigDecimal amount) {
		return amount.multiply(amount);
	}
}
