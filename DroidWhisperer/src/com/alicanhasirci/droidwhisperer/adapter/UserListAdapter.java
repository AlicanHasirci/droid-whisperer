package com.alicanhasirci.droidwhisperer.adapter;

import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.alicanhasirci.droidwhisperer.R;
import com.alicanhasirci.droidwhisperer.activity.ChatActivity;
import com.alicanhasirci.droidwhisperer.model.User;

public class UserListAdapter extends BaseAdapter {
	
	private static final String TAG = "UserListAdapter";
	
	private LayoutInflater inflater;
	private List<User> userList;
	private Context context;

	// private OnItemSelectedListener listener;

	public UserListAdapter(Context context, List<User> list) {
		this.context = context;
		this.userList = list;
		this.inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}

	public void setCurrentList(List<User> list) {
		this.userList = list;
		notifyDataSetChanged();
	}

	@Override
	public int getCount() {
		if (userList == null) {
			return 0;
		}
		return userList.size();
	}

	@Override
	public Object getItem(int position) {
		return userList.get(position);
	}

	@Override
	public long getItemId(int position) {
		return 0;
	}

	@Override
	public View getView(final int position, View view, ViewGroup parent) {

		final User user = userList.get(position);

		if (view == null)
			view = inflater.inflate(R.layout.user_list_item, null);

		ImageView logo = (ImageView) view.findViewById(R.id.logo);
		TextView name = (TextView) view.findViewById(R.id.name);
		name.setText(user.getUsername());
		logo.setImageResource(R.drawable.ic_launcher);

		view.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				Intent chatIntent = new Intent(context,ChatActivity.class);
				Log.d(TAG,"Clicked to user:" + user.getUsername());
				chatIntent.putExtra("userid", user.getUsername());
				context.startActivity(chatIntent);
			}
		});
		return view;
	}
}