package com.restfultransfer.data;

import java.lang.String;
import java.sql.Timestamp;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Client {

    @JsonProperty()
	private long clientId;
    @JsonProperty()
	private String name;
    @JsonProperty()
	private boolean active;
    @JsonProperty()
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
