package com.restfultransfer.data;

import java.math.BigDecimal;
import java.sql.Timestamp;

public class Transaction {

	private long transactionId;
	private long accountId;
	private long accountIdTo;     //0 for external transfers
	private BigDecimal amount;
	private BigDecimal amountExchanged;
	private Timestamp created;
	private int resultCode;

	public Transaction(
			long transactionId,
			long accountId,
			long accountIdTo,
			BigDecimal amount,
			BigDecimal amountExchanged,
			Timestamp created,
			int resultCode) {
		this.transactionId = transactionId;
		this.accountId = accountId;
		this.accountIdTo = accountIdTo;
		this.amount = amount;
		this.amountExchanged = amountExchanged;
		this.created = created;
		this.resultCode = resultCode;
	}
	
	public long TransactionId()         { return transactionId; }
	public long AccountId()             { return accountId; }
	public long AccountIdTo()           { return accountIdTo; }
	public BigDecimal Amount()          { return amount; }
	public BigDecimal AmountExchanged() { return amountExchanged; }
	public Timestamp Created()          { return created; }
	public int ResultCode()             { return resultCode; }
	
}
