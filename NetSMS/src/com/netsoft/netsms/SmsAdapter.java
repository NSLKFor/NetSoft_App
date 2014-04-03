package com.netsoft.netsms;

import java.util.List;







import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class SmsAdapter extends BaseAdapter{

	private final Context context;
	private final List<SmsItem> smsItems;
	private static LayoutInflater inflater=null;
	
	public SmsAdapter(Context context, List<SmsItem> smsItems) {
		this.context = context;
		this.smsItems = smsItems;
		inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		}
	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return smsItems.size();
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return smsItems.get(position);
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		
		SmsItem item  = smsItems.get(position);
        if(convertView==null)
        	convertView = inflater.inflate(R.layout.list_sms_row, null);
        
		TextView tviBody = (TextView) convertView.findViewById(R.id.body);
		TextView tviType = (TextView) convertView.findViewById(R.id.type);
		
		
	
		
		
		if(item.type == 2){
			RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams)tviBody.getLayoutParams();
			params.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
			tviBody.setLayoutParams(params);
		}
		else{
			RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams)tviBody.getLayoutParams();
			params.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
			tviBody.setLayoutParams(params);	
		}
		
		tviBody.setText(item.body.toString());
		tviType.setText( Integer.toString(item.type));
		
		return convertView;
	}

}
