package com.netsoft.netsms;

import java.util.List;

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
			holder.type = (TextView) view.findViewById(R.id.type);
			holder.list_sms = (RelativeLayout)view.findViewById(R.id.list_sms);
			
			view.setTag(holder);
		}else{
			holder = (ViewHolder) view.getTag();
		}
		
		SmsItem item  = smsItems.get(position);
		holder.position = position;
				
		holder.body.setText(item.body.toString());
				
		holder.type.setText(Integer.toString(item.type));
				
		RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.WRAP_CONTENT,
                RelativeLayout.LayoutParams.WRAP_CONTENT);
		
		if(item.type == 2){
		params.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
		holder.body.setLayoutParams(params);
		holder.list_sms.setBackgroundColor(Color.parseColor("#2ECCFA"));
	}
	else{
		params.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
		holder.body.setLayoutParams(params);
		holder.list_sms.setBackgroundColor(Color.parseColor("#A9F5F2"));
	}
		
        
//		TextView tviBody = (TextView) view.findViewById(R.id.body);
//		TextView tviType = (TextView) view.findViewById(R.id.type);
//                		
//		tviBody.setText(item.body.toString());
//		tviType.setText( Integer.toString(item.type));
//		
//		if(item.type == 2){
//			RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams)tviBody.getLayoutParams();
//			params.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
//			tviBody.setLayoutParams(params);
//		}
//		else{
//			RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams)tviBody.getLayoutParams();
//			params.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
//			tviBody.setLayoutParams(params);	
//		}
		
		 
		return view;
	}

}

class ViewHolder {
	  TextView body;
	  TextView type;
	  RelativeLayout list_sms;
	  int position;
	}
