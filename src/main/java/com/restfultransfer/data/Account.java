package com.restfultransfer.data;

import java.util.Currency;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.math.BigDecimal;
import java.sql.Timestamp;

public class Account {
    @JsonProperty()
	private long Id;
    @JsonProperty()
	private long clientId;
    @JsonProperty()
	private Currency currency;
    @JsonProperty()
	private BigDecimal balance;
    @JsonProperty()
	private boolean active;
    @JsonProperty()
	private Timestamp created;

    @SuppressWarnings("unused")
	private Account() {} //for json deserialization
    
	public Account(
			long Id,
			long clientId,
			Currency currency,
			BigDecimal balance,
			boolean active,
			Timestamp created) {
		this.Id = Id;
		this.clientId = clientId;
		this.currency = currency;
		this.balance = balance;
		this.active = active;
		this.created = created;
	}
	
	public long       Id()       { return Id; }
	public long       ClientId() { return clientId; }
	public Currency   Currency() { return currency; }
	public BigDecimal Balance()  { return balance; }
	public boolean    isActive() { return active; }
	public Timestamp  Created()  { return created; }
}
