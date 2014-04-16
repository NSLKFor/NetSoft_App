package com.netsoft.netsms;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.List;

import android.R.integer;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class SmsAdapter extends BaseAdapter{

	private final Context context;
	private final List<SmsItem> smsItems;
	private static LayoutInflater inflater=null;
	
	public SmsAdapter(Context context1, List<SmsItem> smsItems) {
		this.context = context1;
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
		View view = convertView;
		ViewHolder holder = new ViewHolder();
		
		if(view == null){
			view = inflater.inflate(R.layout.list_sms_row, parent,false);
			holder.body = (TextView) view.findViewById(R.id.body);
			holder.time = (TextView) view.findViewById(R.id.type);
			holder.list_sms = (RelativeLayout)view.findViewById(R.id.list_sms);
			
			view.setTag(holder);
		}else{
			holder = (ViewHolder) view.getTag();
		}
		
		SmsItem item  = smsItems.get(position);
		holder.position = position;
				
		holder.body.setText(item.body.toString());
		
		SimpleDateFormat sdf = new SimpleDateFormat(" HH:mm, MMM dd");
		//tviTime.setText(DateFormat.getInstance().format( listContactItem.time));
		holder.time.setText(sdf.format(item.date));
		
				
		RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.WRAP_CONTENT,
                RelativeLayout.LayoutParams.WRAP_CONTENT);
		
		RelativeLayout.LayoutParams timeParams = new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.WRAP_CONTENT,
                RelativeLayout.LayoutParams.WRAP_CONTENT);
				
		timeParams.addRule(RelativeLayout.BELOW, R.id.body);
				
		if(item.type == 2){
		params.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
		timeParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);		
		holder.body.setLayoutParams(params);		
		holder.time.setLayoutParams(timeParams);		
		holder.list_sms.setBackgroundColor(Color.parseColor("#A9E2F3"));
	}
	else{
		params.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
		timeParams.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
		
		holder.body.setLayoutParams(params);
		holder.time.setLayoutParams(timeParams);
		
		holder.list_sms.setBackgroundColor(Color.parseColor("#E0F8F7"));
		
	}		 
		return view;
	}

}

class ViewHolder {
	  TextView body;
	  TextView time;
	  RelativeLayout list_sms;
	  int position;
	}
