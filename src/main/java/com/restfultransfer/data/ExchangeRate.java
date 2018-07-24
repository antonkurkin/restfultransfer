package com.restfultransfer.data;

import java.util.Currency;

import java.math.BigDecimal;

public class ExchangeRate {

	private Currency currencyFrom;
	private Currency currencyTo;
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
