package com.restfultransfer.data;

import java.lang.String;
import java.sql.Timestamp;

public class Client {

	private long clientId;
	private String name;
	private boolean active;
	private Timestamp created;
	
	public Client(
			long clientId,
			String name,
			boolean active,
			Timestamp created) {
		this.clientId = clientId;
		this.name = name;
		this.active = active;
		this.created = created;
	}
	
	public long ClientId()     { return clientId; }
	public String Name()       { return name; }
	public boolean isActive()  { return active; }
	public Timestamp Created() { return created; }
}
