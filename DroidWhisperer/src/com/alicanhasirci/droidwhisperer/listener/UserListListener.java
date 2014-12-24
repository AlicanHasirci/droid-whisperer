package com.alicanhasirci.droidwhisperer.listener;

import java.util.List;

import com.alicanhasirci.droidwhisperer.model.User;

public interface UserListListener {

	public void onListChange(List<User> newList);
	
}
