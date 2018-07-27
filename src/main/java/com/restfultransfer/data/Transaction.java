package com.restfultransfer.data;

import java.math.BigDecimal;
import java.sql.Timestamp;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Transaction {
	public enum State
	{
		TRANSACTION_PENDING(-1),
		TRANSACTION_OK(0),
		TRANSACTION_ACCOUNT_NOT_FOUND(1),
		TRANSACTION_ACCOUNT2_NOT_FOUND(2),
		TRANSACTION_ACCOUNT_INACTIVE(3),
		TRANSACTION_ACCOUNT2_INACTIVE(4),
		TRANSACTION_NOT_ENOUGH(5),
		TRANSACTION_BALANCE_UPDATE_FAIL(6),
		TRANSACTION_DB_PROBLEM(7);

	    private final int code;
	    private State(int code) { this.code = code; }
	    public int Code() { return code; }
	}

    @JsonProperty()
	private long Id;
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
			long Id,
			long accountId,
			long accountIdTo,
			BigDecimal amount,
			BigDecimal amountTo,
			Timestamp created,
			int resultCode) {
		
		if (amount.signum() == 0)
			throw new IllegalArgumentException("Can't create transaction with zero amount");
		if (accountIdTo != 0) {
			if (amount.signum() > 0 || amountTo.signum() <= 0)
				throw new IllegalArgumentException("Can't create reversed internal transfer");
		} else if (amountTo.signum() != 0)
			throw new IllegalArgumentException("Can't create external transfer with negative or zero amount");
		
		this.Id = Id;
		this.accountId = accountId;
		this.accountIdTo = accountIdTo;
		this.amount = amount;
		this.amountTo = amountTo;
		this.created = created;
		this.resultCode = resultCode;
	}
	
	public long       Id()          { return Id; }
	public long       AccountId()   { return accountId; }
	public long       AccountIdTo() { return accountIdTo; }
	public BigDecimal Amount()      { return amount; }
	public BigDecimal AmountTo()    { return amountTo; }
	public Timestamp  Created()     { return created; }
	public int        ResultCode()  { return resultCode; }
	
}
