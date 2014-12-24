package com.alicanhasirci.rendezvous.model;

import java.io.Serializable;
import java.net.InetAddress;
import java.util.Random;

public class User implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private static Random random = new Random();
	
	private int userId;
	private String username;
	private InetAddress publicIP, privateIP;
	private int publicPort, privatePort;

	public User(String username, InetAddress publicIP, int publicPort,
			InetAddress privateIP, int privatePort) {
		this.userId = random.nextInt();
		this.username = username;
		this.publicIP = publicIP;
		this.publicPort = publicPort;
		this.privateIP = privateIP;
		this.privatePort = privatePort;
	}
	
	public String getUsername() {
		return username;
	}
	
	public void setUsername(String username) {
		this.username = username;
	}
	
	public InetAddress getPublicIP() {
		return publicIP;
	}
	
	public void setPublicIP(InetAddress publicIP) {
		this.publicIP = publicIP;
	}
	
	public InetAddress getPrivateIP() {
		return privateIP;
	}
	
	public void setPrivateIP(InetAddress privateIP) {
		this.privateIP = privateIP;
	}
	
	public int getPublicPort() {
		return publicPort;
	}
	
	public void setPublicPort(int publicPort) {
		this.publicPort = publicPort;
	}
	
	public int getPrivatePort() {
		return privatePort;
	}
	
	public void setPrivatePort(int privatePort) {
		this.privatePort = privatePort;
	}

	public int getUserId() {
		return userId;
	}

	public void setUserId(int userId) {
		this.userId = userId;
	}
}
