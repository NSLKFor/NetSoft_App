package com.netsoft.netmms;

import java.util.ArrayList;
import java.util.List;

import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SqliteWrapper;
import android.net.Uri;
import android.provider.Telephony.Mms;
import android.provider.Telephony.Mms.Inbox;
import android.text.TextUtils;
import android.widget.Toast;

import com.android.mms.transaction.HttpUtils;
import com.android.mms.util.SendingProgressTokenManager;
import com.google.android.mms.pdu.GenericPdu;
import com.google.android.mms.pdu.PduHeaders;
import com.google.android.mms.pdu.PduParser;
import com.google.android.mms.pdu.PduPersister;
import com.google.android.mms.pdu.DeliveryInd;
import com.google.android.mms.pdu.RetrieveConf;
import com.netsoft.netmms.*;

public class MMSReceiver extends BroadcastReceiver {

	@Override
	public void onReceive(final Context context, Intent intent) {
		// TODO Auto-generated method stub
		
		 byte[] pushData = intent.getByteArrayExtra("data");
		    PduParser parser = new PduParser(pushData);
		    GenericPdu pdu = parser.parse();
		    
		    String messageId= "";
		    int type = pdu.getMessageType();
		    if(type == PduHeaders.MESSAGE_TYPE_DELIVERY_IND) {
		    	 messageId = new String(((DeliveryInd)pdu).getMessageId());
		        //Notify app that mms with messageId was delivered
		    }
		    
		    int status = ((DeliveryInd)pdu).getStatus();
		    if(status == PduHeaders.STATUS_RETRIEVED) {
		        //message delivered. update storage
		    }
		    
		
		String id = messageId;  // this is the id of your MMS message that you are going to search for
		Cursor locationQuery = context.getContentResolver().query(Uri.parse("content://mms/"), new String[] {"m_size", "exp", "ct_l", "_id"}, "_id=?", new String[]{id}, null);
		locationQuery.moveToFirst();
				  
		String exp = "1";
		String size = "1";
				  
		try
		{
			size = locationQuery.getString(locationQuery.getColumnIndex("m_size"));
		 	exp = locationQuery.getString(locationQuery.getColumnIndex("exp"));
		} catch (Exception f)
		{
		   		
		}
			  
		String location = locationQuery.getString(locationQuery.getColumnIndex("ct_l"));
		
		List<APN> apns = new ArrayList<APN>();
		SharedPreferences sharedPrefs = context.getSharedPreferences("netsoft_preferences", 0);
		
		try
		{
			APNHelper helper = new APNHelper(context);
			apns = helper.getMMSApns();
										
		} catch (Exception e)
		{
			APN apn = new APN(sharedPrefs.getString("mmsc_url", ""), sharedPrefs.getString("mms_port", ""), sharedPrefs.getString("mms_proxy", ""));
			apns.add(apn);
		}
		
		
		try {
			byte[] resp = HttpUtils.httpConnection(
			        context, SendingProgressTokenManager.NO_TOKEN,
			        location, null, HttpUtils.HTTP_GET_METHOD,
			        !TextUtils.isEmpty(apns.get(0).MMSProxy),
			        apns.get(0).MMSProxy,
			        Integer.parseInt(apns.get(0).MMSPort));
			
			RetrieveConf retrieveConf = (RetrieveConf) new PduParser(resp).parse();
			PduPersister persister = PduPersister.getPduPersister(context);
			Uri msgUri = persister.persist(retrieveConf, Inbox.CONTENT_URI);
			
			ContentValues values = new ContentValues(1);
		    	values.put(Mms.DATE, System.currentTimeMillis() / 1000L);
		   	SqliteWrapper.update(context, context.getContentResolver(),
		            msgUri, values, null, null);
		   	
		   	Toast.makeText(context, "Message Received", Toast.LENGTH_SHORT).show();
//		        SqliteWrapper.delete(context, context.getContentResolver(),
//		    		Uri.parse("content://mms/"), "thread_id=? and _id=?", new String[] {threadIds, msgId});
		    
//		    	((Activity) context).getWindow().getDecorView().findViewById(android.R.id.content).post(new Runnable() {
//				
//				@Override
//				public void run() {
//					Toast.makeText(context, "Message Received", Toast.LENGTH_SHORT).show();
//				}
//			});
		} catch (Exception e) {
			e.printStackTrace();
			
//			((Activity) context).getWindow().getDecorView().findViewById(android.R.id.content).post(new Runnable() {
//				
//				@Override
//				public void run() {
//					Toast.makeText(context, "Download Failed", Toast.LENGTH_SHORT).show();
//				}
//			});
		}
		
	}

}
