package com.alicanhasirci.rendezvous.main;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.json.JSONException;
import org.json.JSONObject;

import com.alicanhasirci.rendezvous.model.User;
import com.alicanhasirci.rendezvous.util.Logger;

public class Server implements Runnable {

	public static final String VERSION = "1.1"; 
	public static final Logger logger = new Logger();
	public static final int PORT = 5111;
	private final static ExecutorService pool = Executors.newCachedThreadPool();
	public static List<User> onlineUserList;
	public static DatagramSocket socket;
	public DatagramPacket packet;
	public byte[] buffer;

	public static void main(String[] args) {
		pool.execute(new Server());

	}

	public Server() {
		onlineUserList = new ArrayList<User>();
		try {
			User d1 = new User("DUMMY1", InetAddress.getByName("78.180.76.43"),
					5111, InetAddress.getByName("78.180.76.43"), 5111);
			User d2 = new User("DUMMY2", InetAddress.getByName("78.180.76.43"),
					5111, InetAddress.getByName("78.180.76.43"), 5111);
			onlineUserList.add(d1);
			onlineUserList.add(d2);
			socket = new DatagramSocket(PORT);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void run() {
		while (true) {
			try {
				logger.info("Server is idle.");
				buffer = new byte[socket.getReceiveBufferSize()];
				packet = new DatagramPacket(buffer, buffer.length);
				socket.receive(packet);
				logger.info("A packet recieved.");
				JSONObject request = new JSONObject(
						new String(packet.getData()));
				if (request.getString("Operation").equals("Connect")) {
					logger.info("User connected: "
							+ request.getString("Username"));
					User user = new User(request.getString("Username"),
							packet.getAddress(), packet.getPort(),
							InetAddress.getByName(request.getString("IP")),
							request.getInt("Port"));
					boolean alreadyExists = false;
					for (User currentUser : onlineUserList) {
						if (currentUser.getUsername()
								.equals(user.getUsername())) {
							alreadyExists = true;
							break;
						}
					}
					if (!alreadyExists) {
						Server.onlineUserList.add(user);
						logger.info("New user has been added to OnlineUserList. Total Online Users:"
								+ onlineUserList.size());
					}
					Broadcast();
				}
			} catch (IOException e) {
				logger.error("An IOException occured.");
				e.printStackTrace();
			} catch (JSONException e) {
				logger.error("Un parsable message from \""+packet.getAddress().toString()+":"+packet.getPort()+"\":\n"+new String(packet.getData()));
				e.printStackTrace();
			} catch (Exception e) {
				logger.error("An unkown error occured.");
				e.printStackTrace();
			}
		}
	}

	public void Broadcast() {
		logger.info("Broadcasting has started.");
		String jsonReturn = "";
		for (User user : Server.onlineUserList) {
			jsonReturn += "{\"Username\":\"" + user.getUsername() + "\","
					+ "\"PrivateIP\":\"" + user.getPrivateIP().getHostAddress()
					+ "\"," + "\"PrivatePort\":\"" + user.getPrivatePort()
					+ "\"," + "\"PublicIP\":\""
					+ user.getPublicIP().getHostAddress() + "\","
					+ "\"PublicPort\":\"" + user.getPublicPort() + "\"},";
		}
		jsonReturn = "[" + jsonReturn.substring(0, jsonReturn.length() - 1)
				+ "]";
		int successful = 0, error = 0;
		for (User user : onlineUserList) {
			DatagramPacket packet = new DatagramPacket(jsonReturn.getBytes(),
					jsonReturn.getBytes().length, user.getPublicIP(),
					user.getPublicPort());
			try {
				Server.socket.send(packet);
				successful++;
			} catch (IOException e) {
				error++;
			}
		}
		logger.info("Broadcasting has finished: Successful=" + successful
				+ " Error=" + error);
	}

}
