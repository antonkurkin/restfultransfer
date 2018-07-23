package com.restfultransfer.data;

import java.util.Currency;
import java.math.BigDecimal;
import java.sql.Timestamp;

public class Account {
	private long accountId;
	private long clientId;
	private Currency currency;
	private BigDecimal balance;
	private boolean active;
	private Timestamp created;
	
	public Account(
			long accountId,
			long clientId,
			Currency currency,
			BigDecimal balance,
			boolean active,
			Timestamp created) {
		this.accountId = accountId;
		this.clientId = clientId;
		this.currency = currency;
		this.balance = balance;
		this.active = active;
		this.created = created;
	}
	
	public long AccountId()     { return accountId; }
	public long ClientId()      { return clientId; }
	public Currency Currency()  { return currency; }
	public BigDecimal Balance() { return balance; }
	public boolean isActive()   { return active; }
	public Timestamp Created()  { return created; }
}
