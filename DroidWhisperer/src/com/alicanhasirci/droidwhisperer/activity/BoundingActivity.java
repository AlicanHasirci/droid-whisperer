package com.alicanhasirci.droidwhisperer.activity;

import com.alicanhasirci.droidwhisperer.service.ServerService;
import com.alicanhasirci.droidwhisperer.service.ServerService.LocalBinder;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;

public class BoundingActivity extends Activity{
	
	public boolean bound = false;
	public ServerService serverService;
	
	@Override
	protected void onStart() {
		// TODO Auto-generated method stub
		super.onStart();
		if(bindService(new Intent(this, ServerService.class), connection, Context.BIND_AUTO_CREATE))
			Log.d("Bounding Activity", "Service Bound");
		else
			Log.d("Bounding Activity","Service could not be bound!");
			

	}
	@Override
	protected void onStop() {
		// TODO Auto-generated method stub
		super.onStop();
		if(bound)
			unbindService(connection);
	}
	
	
	public ServiceConnection connection = new ServiceConnection() {
		ServerService boundService;
        @Override
        public void onServiceConnected(ComponentName className,
                IBinder service) {
            // We've bound to ServerService, cast the IBinder and get LocalService instance
            LocalBinder binder = (LocalBinder) service;
            serverService = binder.getService();
            bound = true;
        }
        @Override
        public void onServiceDisconnected(ComponentName arg0) {
            bound = false;
        }
    };    
}
