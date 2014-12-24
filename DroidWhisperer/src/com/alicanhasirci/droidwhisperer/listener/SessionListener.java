package com.alicanhasirci.droidwhisperer.listener;

import com.alicanhasirci.droidwhisperer.model.ChatSession;
import com.alicanhasirci.droidwhisperer.model.Message;

public interface SessionListener {
	
	public void onNewMessage(Message newMessage);
	public ChatSession getChatSession();
}
