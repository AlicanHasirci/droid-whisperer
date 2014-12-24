package com.alicanhasirci.droidwhisperer.model;

public class User {
	private String username;
	private int publicPort,privatePort;
	private String publicIP,privateIP;
	
	public User(String username,String prIP,int prPort,String puIP,int puPort){
		this.username = username;
		this.privateIP = prIP;
		this.privatePort = prPort;
		this.publicIP = puIP;
		this.publicPort = puPort;
	}
	
	public void setUsername(String username) {
		this.username = username;
	}
	public String getUsername() {
		return username;
	}
	public void setPublicPort(int publicPort) {
		this.publicPort = publicPort;
	}
	public int getPublicPort() {
		return publicPort;
	}
	public void setPrivatePort(int privatePort) {
		this.privatePort = privatePort;
	}
	public int getPrivatePort() {
		return privatePort;
	}
	public void setPublicIP(String publicIP) {
		this.publicIP = publicIP;
	}
	public String getPublicIP() {
		return publicIP;
	}
	public void setPrivateIP(String privateIP) {
		this.privateIP = privateIP;
	}
	public String getPrivateIP() {
		return privateIP;
	}
}
