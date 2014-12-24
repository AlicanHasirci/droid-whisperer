package com.alicanhasirci.droidwhisperer.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import com.alicanhasirci.droidwhisperer.R;
import com.alicanhasirci.droidwhisperer.service.ServerService;

public class SplashActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splash_activity);
    }

    @Override
    protected void onResume() {
        super.onResume();
        runOnUiThread(new Runnable(){
			@Override
			public void run() {
				try {
					startService(new Intent(getApplicationContext(),ServerService.class));
					Thread.sleep(5000);
					startActivity(new Intent(SplashActivity.this, UserListActivity.class));
					finish();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}	
			}}
        );
    }
}
