package com.alicanhasirci.droidwhisperer.adapter;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.alicanhasirci.droidwhisperer.R;
import com.alicanhasirci.droidwhisperer.model.Message;

public class MessageListAdapter extends BaseAdapter {

	private LayoutInflater inflater;
	private List<Message> messageList;
//	private Context context;

	// private OnItemSelectedListener listener;

	public MessageListAdapter(Context context,List<Message> list) {
//		this.context = context;
		this.messageList = list;
		this.inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}

	public void setCurrentList(List<Message> list) {
		this.messageList = list;
		notifyDataSetChanged();
	}

	@Override
	public int getCount() {
		if (messageList == null) {
			return 0;
		}
		return messageList.size();
	}

	@Override
	public Object getItem(int position) {
		return messageList.get(position);
	}

	@Override
	public long getItemId(int position) {
		return 0;
	}

	@Override
	public View getView(final int position, View view, ViewGroup parent) {

		final Message message = messageList.get(position);

		if (view == null)
			view = inflater.inflate(R.layout.message_list_item, null);

		TextView usernameText = (TextView) view.findViewById(R.id.name);
		TextView timestampText = (TextView) view.findViewById(R.id.timestamp);
		TextView messageText = (TextView) view.findViewById(R.id.message);
		
		usernameText.setText(message.getSender().getUsername());
		timestampText.setText(message.getTimestamp().toString());
		messageText.setText(message.getMessage());
		
		return view;
	}
}