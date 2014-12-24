package com.alicanhasirci.droidwhisperer.service;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.telephony.TelephonyManager;
import android.util.Log;

import com.alicanhasirci.droidwhisperer.listener.SessionListener;
import com.alicanhasirci.droidwhisperer.listener.UserListListener;
import com.alicanhasirci.droidwhisperer.model.ChatSession;
import com.alicanhasirci.droidwhisperer.model.Message;
import com.alicanhasirci.droidwhisperer.model.User;
import com.alicanhasirci.droidwhisperer.util.Constant;
import com.alicanhasirci.droidwhisperer.util.Utils;

public class ServerService extends Service {
	private static final String TAG = "ServerService";
	private final ExecutorService executor = Executors
			.newCachedThreadPool();

	public  User thisUser;
	
	private final IBinder binder = new LocalBinder();
	
	public volatile List<User> onlineUserList = new ArrayList<User>();
	public volatile List<ChatSession> ongoingChatSessionList = new ArrayList<ChatSession>();
	public  DatagramSocket socket;
	private String deviceId;
	private List<SessionListener> chatSessionListenerList = new ArrayList<SessionListener>();

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		synchronized (this) {
			executor.execute(new Runnable(){
				@Override
				public void run() {
					if (socket == null)
						try {
							socket = new DatagramSocket(Constant.PORT);
						} catch (SocketException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
				}
			});
			while(socket==null){
				//Wait until socket is opened.
			}
			executor.execute(listen);
			return Service.START_NOT_STICKY;
		}
	}

	Runnable listen = new Runnable() {
		@Override
		public void run() {
			
			TelephonyManager tm = (TelephonyManager) getApplicationContext()
					.getSystemService(Context.TELEPHONY_SERVICE);
			deviceId = tm.getDeviceId();
			
			Log.i(TAG, "Openning connection to :" + Constant.SERVER_IP
					+ ":" + Constant.PORT);
			try {
				String privateEndpoint = "{\"Operation\":\"Connect\","
						+ "\"IP\":\"" + Utils.getIPAddress(true)
						+ "\"," + "\"Port\":\"" + Constant.PORT + "\","
						+ "\"Username\":\"" + deviceId + "\"}";

				DatagramPacket handshake = new DatagramPacket(
						privateEndpoint.getBytes(), privateEndpoint
								.getBytes().length, InetAddress
								.getByName(Constant.SERVER_IP),
						Constant.PORT);
				socket.send(handshake);
				Log.d(TAG, "Connection Request has been sent to: "
						+ Constant.SERVER_IP);
				thisUser = new User(deviceId, Utils.getIPAddress(true),
						Constant.PORT, "", 0);
			} catch (Exception e) {
				e.printStackTrace();
			}
			
			while (true) {
				try {
					byte[] response = new byte[512];
					DatagramPacket packet = new DatagramPacket(response,
							response.length);
					socket.receive(packet);
					Log.d(TAG, "Packet Recieved From Server:" +new String(packet.getData()));
					if (packet.getAddress().equals(
							InetAddress.getByName(Constant.SERVER_IP))) {

						JSONObject serverResponse = new JSONObject(new String(
								packet.getData()));
						JSONArray userList = serverResponse.getJSONArray("UserList");
						onlineUserList.clear();
						for (int i = 0; i < userList.length(); i++) {
							JSONObject co = userList.getJSONObject(i);
							if (!deviceId.equals(co.getString("Username")))
								onlineUserList.add(new User(co.getString("Username"), 
										co.getString("PrivateIP"), 
										co.getInt("PrivatePort"), 
										co.getString("PublicIP"), 
										co.getInt("PublicPort")));
						}
						
						ongoingChatSessionList.clear();

						for (User user : onlineUserList)
							ongoingChatSessionList.add(new ChatSession(user,ServerService.this));
					} else {
						// case where the packet is from another user
						Log.i(TAG, "Package recieved from unkown source!!!!!");
						User sender = null;
						for (ChatSession session : ongoingChatSessionList) {
							if (session.getRemoteUser().getPublicIP()
									.equals(packet.getAddress()))
								sender = session.getRemoteUser();
						}
						for (SessionListener sessionListener : chatSessionListenerList) {
							if (sessionListener.getChatSession()
									.getRemoteUser().equals(sender)) {
								sessionListener.onNewMessage(new Message(
										sender, new String(packet.getData())));
								break;
							}
						}
					}
				} catch (IOException e) {
					Log.e(TAG, "A problem occured during listening!");
					e.printStackTrace();
				} catch (JSONException e) {
					Log.e(TAG, "A problem occured during JSON parsing!");
					e.printStackTrace();
				}
			}
		}
	};

	@Override
	public IBinder onBind(Intent arg0) {
		return binder;
	}

	public class LocalBinder extends Binder {
		public ServerService getService() {
			return ServerService.this;
		}
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		socket.close();
	}

	public void addSessionListener(SessionListener arg0) {
		chatSessionListenerList.add(arg0);
	}
}
