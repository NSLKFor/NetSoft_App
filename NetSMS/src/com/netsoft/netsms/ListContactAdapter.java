package com.netsoft.netsms;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class ListContactAdapter extends BaseAdapter{
	private  Context context;
	private  List<String> listContacts;
	private static LayoutInflater inflater=null;
	
	public ListContactAdapter(Context context, List<String> listItems) {
		this.context = context;
		this.listContacts = listItems;
		inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		}
	
	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return listContacts.size();
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return listContacts.get(position);
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		 if(convertView==null)
	        	convertView = inflater.inflate(R.layout.contact_row, null);
		 final String address = this.listContacts.get(position);
		 
		ImageView imgThum = (ImageView) convertView.findViewById(R.id.list_image);
		TextView tviAddress = (TextView)convertView.findViewById(R.id.address);
		TextView tviBody = (TextView)convertView.findViewById(R.id.body);
		TextView tviTime = (TextView) convertView.findViewById(R.id.time);
		 
		imgThum.setImageResource(R.drawable.ic_launcher);
		tviAddress.setText(address);
		tviBody.setText("hi hi  " + position);
		tviTime.setText("ho ho  " + position);
		
		return convertView;
	}

}
