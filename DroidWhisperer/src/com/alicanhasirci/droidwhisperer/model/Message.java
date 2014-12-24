package com.alicanhasirci.droidwhisperer.model;

import java.util.Date;

public class Message {
	
	private User sender;
	private Date timestamp;
	private String message;
	
	public Message(User sender, String message){
		this.sender = sender;
		this.timestamp = new Date();
		this.message = message;
	}
	
	public User getSender() {
		return sender;
	}
	public Date getTimestamp() {
		return timestamp;
	}
	public String getMessage() {
		return message;
	}
}
