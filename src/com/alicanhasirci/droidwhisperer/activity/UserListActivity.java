package com.alicanhasirci.droidwhisperer.activity;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.view.Menu;
import android.widget.ListView;
import android.widget.Toast;

import com.alicanhasirci.droidwhisperer.R;
import com.alicanhasirci.droidwhisperer.adapter.UserListAdapter;
import com.alicanhasirci.droidwhisperer.listener.UserListListener;
import com.alicanhasirci.droidwhisperer.model.Message;
import com.alicanhasirci.droidwhisperer.model.User;
import com.alicanhasirci.droidwhisperer.service.ServerService;
import com.alicanhasirci.droidwhisperer.service.ServerService.LocalBinder;

public class UserListActivity extends Activity{
	
	private UserListActivity context;
	private List<User> displayedUserList = new ArrayList<User>();
	private UserListAdapter userListAdapter;
	private ListView userListView;
	
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
			listGrabber.execute(serverService);
		}
		
		@Override
		public void onServiceDisconnected(ComponentName arg0) {
			bound = false;
		}
	};
	
	private UserListGrabber listGrabber = new UserListGrabber();
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = this;
        setContentView(R.layout.activity_user_list);
        
    }
    
    @Override
    protected void onStart() {
    	super.onStart();
    	
    	Intent intent = new Intent(this, ServerService.class);
        bindService(intent, connection, Context.BIND_AUTO_CREATE);

    }
    
    @Override
    protected void onResume() {
    	super.onResume();
    	displayedUserList = new ArrayList<User>();
    	userListAdapter = new UserListAdapter(this, displayedUserList);
        userListView = (ListView) findViewById(R.id.userlistView);
        userListView.setAdapter(userListAdapter);
    }
    
    @Override
    protected void onStop() {
    	// TODO Auto-generated method stub
    	super.onStop();
    	if(bound)
    		listGrabber.updating=false;
			unbindService(connection);
    }
    
    @Override
    protected void onDestroy() {
    	super.onDestroy();
    }
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_user_list, menu);
        return true;
    }
    

    
    class UserListGrabber extends AsyncTask<ServerService, List<User>, Void> {

    	boolean updating=true;
    	
		@Override
		protected Void doInBackground(ServerService... params) {
			while(updating){
				Log.d("AsyncTask", "Updating User List");
				publishProgress(params[0].onlineUserList);
				try {
					Thread.sleep(30*1000);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			return null;
		}
		protected void onProgressUpdate(java.util.List<User>... values) {
			for(User user : values[0]){
				Log.d("AsyncTask","Added user to list:"+user.getUsername());
			}
			userListAdapter.setCurrentList(values[0]);
			Log.d("AsyncTask","new user has been set.");			
		};
	}
}
