package com.alicanhasirci.droidwhisperer.activity;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.UnknownHostException;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.view.KeyEvent;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;

import com.alicanhasirci.droidwhisperer.R;
import com.alicanhasirci.droidwhisperer.adapter.MessageListAdapter;
import com.alicanhasirci.droidwhisperer.listener.SessionListener;
import com.alicanhasirci.droidwhisperer.model.ChatSession;
import com.alicanhasirci.droidwhisperer.model.Message;
import com.alicanhasirci.droidwhisperer.model.User;
import com.alicanhasirci.droidwhisperer.service.ServerService;
import com.alicanhasirci.droidwhisperer.service.ServerService.LocalBinder;

public class ChatActivity extends Activity implements SessionListener {

	private static final String TAG = "ChatActivity";
	
	private ListView messageBoard;
	private EditText userMessage;
	private TextView username;
	private MessageListAdapter messageListAdapter;
	private ChatSession session;
	private User remoteUser;
	
	public boolean bound = false;
	private ServerService serverService;
	private ServiceConnection connection = new ServiceConnection() {
		
		@Override
		public void onServiceConnected(ComponentName className,
				IBinder service) {
			// We've bound to LocalService, cast the IBinder and get LocalService instance
			LocalBinder binder = (LocalBinder) service;
			serverService = binder.getService();
			bound = true;
		}
		
		@Override
		public void onServiceDisconnected(ComponentName arg0) {
			bound = false;
		}
	};
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		try{	
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_chat);
		
		messageBoard = (ListView) findViewById(R.id.messageboard);
		userMessage = (EditText) findViewById(R.id.usermessage);
		username = (TextView) findViewById(R.id.username);
		userMessage.setOnEditorActionListener(new SendMessage());		
		
		messageListAdapter = new MessageListAdapter(this, session.getMessageList());
		messageBoard.setAdapter(messageListAdapter);
		}catch(Exception ex){
			ex.printStackTrace();
		}
	}
	
	@Override
	protected void onStart() {
		// TODO Auto-generated method stub
		super.onStart();
    	
    	Intent intent = new Intent(this, ServerService.class);
        bindService(intent, connection, Context.BIND_AUTO_CREATE);
		
		String usernameIntent = (String) getIntent().getExtras().get("userid");
		username.setText(usernameIntent);
		
		for(User user : serverService.onlineUserList){
			if(user.getUsername().equals(usernameIntent)){
				remoteUser = user;
				break;
			}
		}
		for(ChatSession existingSession : serverService.ongoingChatSessionList){
			if(existingSession.getRemoteUser()==remoteUser)
				session = existingSession;
		}
		
		serverService.addSessionListener(this);
		
	}

	@Override
	public void onNewMessage(final Message newMessage) {
		runOnUiThread(new Runnable() {
			@Override
			public void run() {
				session.getMessageList().add(newMessage);
				messageListAdapter.setCurrentList(session.getMessageList());
			}
		});
	}

	@Override
	public ChatSession getChatSession() {
		return session;
	}

	class SendMessage implements OnEditorActionListener{
		@Override
		public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
			if(actionId==EditorInfo.IME_ACTION_SEND){
				Log.d(TAG, "Sending Message to "+session.getRemoteUser().getPublicIP()+":"+session.getRemoteUser().getPublicPort());
				final String messageToSend = userMessage.getText().toString();
				new AsyncTask<Void, Void, Void>() {
					@Override
					protected Void doInBackground(Void... params) {
						byte[] message = messageToSend.getBytes();
						try {
							DatagramPacket toSend = new DatagramPacket(message, message.length, InetAddress.getByName(session.getRemoteUser().getPublicIP()),session.getRemoteUser().getPublicPort());
							serverService.socket.send(toSend);
						} catch (UnknownHostException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						return null;
					}
					protected void onPostExecute(Void result) {
						onNewMessage(new Message(session.getCurrentUser(), messageToSend));
					};
				}.execute();
				userMessage.setText("");
				return true;
			}else
				return false;
				
		}
	}

}
