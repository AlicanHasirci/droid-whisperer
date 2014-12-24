package com.alicanhasirci.droidwhisperer.model;

import java.util.ArrayList;
import java.util.List;

import com.alicanhasirci.droidwhisperer.service.ServerService;

public class ChatSession {

	private User currentUser;
	private User remoteUser;
	private List<Message> messageList;
	
	public ChatSession(User remote,ServerService instance){
		this.currentUser = instance.thisUser;
		this.remoteUser = remote;
		messageList = new ArrayList<Message>();
	}
	
	public User getCurrentUser() {
		return currentUser;
	}
	public void setCurrentUser(User currentUser) {
		this.currentUser = currentUser;
	}
	public User getRemoteUser() {
		return remoteUser;
	}
	public void setRemoteUser(User remoteUser) {
		this.remoteUser = remoteUser;
	}
	public List<Message> getMessageList() {
		return messageList;
	}
}
