package com.netsoft.netsms;

import java.util.ArrayList;

import android.content.ContentValues;
import android.content.Context;
import android.net.Uri;
import android.telephony.SmsManager;

public class SMSSender {
	public void sendSMSMessage(String numberPhone, String message){
		SmsManager sms = SmsManager.getDefault();
		sms.sendTextMessage(numberPhone, null, message, null, null);
	}
	
	public void sendSMSMessage(Context context,  SmsItem item){
		
		 SmsManager sms = SmsManager.getDefault();
		 ArrayList<String> messages = sms.divideMessage(item.body.toString());
		 sms.sendMultipartTextMessage(item.address.toString(), null, messages, null, null);
		    
		    
//		SmsManager sms = SmsManager.getDefault();
//		sms.sendTextMessage(item.address.toString(), null, item.body.toString(), null, null);
		
		 ContentValues values = new ContentValues();
	     values.put("address", item.address.toString());
	     values.put("body", item.body.toString());
	     values.put("read",Integer.toString(item.readStatus));
	     values.put("date", item.date);
	     values.put("type", Integer.toString(item.type));
//	     values.put("_id", Integer.toString(item.id));
	        
	     context.getContentResolver().insert(Uri.parse("content://sms/sent"), values);
	}
}
