package com.restfultransfer.data;

import java.math.BigDecimal;
import java.sql.Timestamp;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Transaction {

    @JsonProperty()
	private long transactionId;
    @JsonProperty()
	private long accountId;
    @JsonProperty()
	private long accountIdTo;     //0 for external transfers
    @JsonProperty()
	private BigDecimal amount;
    @JsonProperty()
	private BigDecimal amountTo;
    @JsonProperty()
	private Timestamp created;
    @JsonProperty()
	private int resultCode;

	public Transaction(
			long transactionId,
			long accountId,
			long accountIdTo,
			BigDecimal amount,
			BigDecimal amountTo,
			Timestamp created,
			int resultCode) {
		if (accountIdTo != 0 && (amount.signum() >= 0 || amountTo.signum() <= 0))
			throw new IllegalArgumentException("Can't create internal transfer with negative or zero amount");
		
		this.transactionId = transactionId;
		this.accountId = accountId;
		this.accountIdTo = accountIdTo;
		this.amount = amount;
		this.amountTo = amountTo;
		this.created = created;
		this.resultCode = resultCode;
	}
	
	public long TransactionId()  { return transactionId; }
	public long AccountId()      { return accountId; }
	public long AccountIdTo()    { return accountIdTo; }
	public BigDecimal Amount()   { return amount; }
	public BigDecimal AmountTo() { return amountTo; }
	public Timestamp Created()   { return created; }
	public int ResultCode()      { return resultCode; }
	
}
