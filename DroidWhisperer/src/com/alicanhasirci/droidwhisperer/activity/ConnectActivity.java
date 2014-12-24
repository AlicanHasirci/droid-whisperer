package com.alicanhasirci.droidwhisperer.activity;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.alicanhasirci.droidwhisperer.R;
import com.alicanhasirci.droidwhisperer.model.User;



public class ConnectActivity extends Activity implements OnClickListener {
	private static final ExecutorService pool= Executors.newSingleThreadExecutor();
	public static EditText usernameText;
	public static Button connectButton;
	public static List<User> userList;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_connect);
        usernameText = (EditText) findViewById(R.id.username_text);
        connectButton = (Button) findViewById(R.id.connect_button);
        connectButton.setOnClickListener(this);
    }

	@Override
	public void onClick(View v) {
		if(v.getId()==R.id.connect_button){
			if(usernameText.equals("")||usernameText.getText().toString().trim().length()==0){
				Toast.makeText(this, "You should fill the username field.", Toast.LENGTH_LONG).show();
			}else{
//				pool.execute(server);
			}
		}
	}
	
	@Override
	protected void onStop() {
//		Connection.socket.close();
		pool.shutdownNow();
		super.onStop();
	}
}
