package com.restfultransfer.data;

import java.lang.String;
import java.sql.Timestamp;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Client {

    @JsonProperty()
	private long Id;
    @JsonProperty()
	private String name;
    @JsonProperty()
	private boolean active;
    @JsonProperty()
	private Timestamp created;
	
    @SuppressWarnings("unused")
	private Client() {} //for json deserialization
    
	public Client(
			long Id,
			String name,
			boolean active,
			Timestamp created) {
		this.Id = Id;
		this.name = name;
		this.active = active;
		this.created = created;
	}
	
	public long      Id()       { return Id; }
	public String    Name()     { return name; }
	public boolean   isActive() { return active; }
	public Timestamp Created()  { return created; }
	
}
