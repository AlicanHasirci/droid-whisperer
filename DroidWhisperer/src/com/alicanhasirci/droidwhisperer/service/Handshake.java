package com.alicanhasirci.droidwhisperer.service;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

import android.app.Service;
import android.content.Context;
import android.telephony.TelephonyManager;
import android.util.Log;

import com.alicanhasirci.droidwhisperer.model.User;
import com.alicanhasirci.droidwhisperer.util.Constant;
import com.alicanhasirci.droidwhisperer.util.Utils;

public class Handshake implements Runnable { // HANDSHAKE
	
	public static final String TAG = "HANDSHAKE";
	
	private DatagramSocket socket;
	private String deviceId;
	private ServerService parent;
	
	public Handshake(ServerService parent, DatagramSocket socket){
		this.socket = socket;
		this.parent = parent;
		TelephonyManager tm = (TelephonyManager) parent.getApplicationContext()
				.getSystemService(Context.TELEPHONY_SERVICE);
		deviceId = tm.getDeviceId();
	}
	
	@Override
	public void run() {
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
			parent.thisUser = new User(deviceId, Utils.getIPAddress(true),
					Constant.PORT, "", 0);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
