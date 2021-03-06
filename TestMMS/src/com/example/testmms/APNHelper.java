package com.example.testmms;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.android.mms.transaction.HttpUtils;
import com.google.android.mms.pdu.EncodedStringValue;
import com.google.android.mms.pdu.PduBody;
import com.google.android.mms.pdu.PduComposer;
import com.google.android.mms.pdu.PduPart;
import com.google.android.mms.pdu.SendReq;

import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.provider.Telephony;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

public class APNHelper {
	private Context context;
	public APNHelper(final Context context) {
	    this.context = context;
	}   
	

	public List<APN> getMMSApns() {     
	    final Cursor apnCursor = this.context.getContentResolver().query(Uri.withAppendedPath(Telephony.Carriers.CONTENT_URI, "current"), null, null, null, null);
	if ( apnCursor == null ) {
	        return Collections.EMPTY_LIST;
	    } else {
	        final List<APN> results = new ArrayList<APN>(); 
	            if ( apnCursor.moveToFirst() ) {
	        do {
	            final String type = apnCursor.getString(apnCursor.getColumnIndex(Telephony.Carriers.TYPE));
	            if ( !TextUtils.isEmpty(type) && ( type.equalsIgnoreCase("*") || type.equalsIgnoreCase("mms") ) ) {
	                final String mmsc = apnCursor.getString(apnCursor.getColumnIndex(Telephony.Carriers.MMSC));
	                final String mmsProxy = apnCursor.getString(apnCursor.getColumnIndex(Telephony.Carriers.MMSPROXY));
	                final String port = apnCursor.getString(apnCursor.getColumnIndex(Telephony.Carriers.MMSPORT));                  
	                final APN apn = new APN();
	                apn.MMSCenterUrl = mmsc;
	                apn.MMSProxy = mmsProxy;
	                apn.MMSPort = port;
	                results.add(apn);
	                
//	                Toast.makeText(context, mmsc + " " + mmsProxy + " " + port, Toast.LENGTH_LONG).show();
	            }
	        } while ( apnCursor.moveToNext() ); 
	             }              
	        apnCursor.close();
	        return results;
	    }
	}
	
	public void sendMMS(final String recipient, final MMSPart[] parts)
	{
		ConnectivityManager mConnMgr =  (ConnectivityManager)this.context.getSystemService(Context.CONNECTIVITY_SERVICE);
		final int result = mConnMgr.startUsingNetworkFeature(ConnectivityManager.TYPE_MOBILE, "enableMMS");
		
		if (result != 0)
		{
			IntentFilter filter = new IntentFilter();
			filter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
			BroadcastReceiver receiver = new BroadcastReceiver() {

				@Override
				public void onReceive(Context context, Intent intent) {
					// TODO Auto-generated method stub
					
					String action = intent.getAction();
					
					if (!action.equals(ConnectivityManager.CONNECTIVITY_ACTION))
					{
						Log.e("MMS", "!action.equals(ConnectivityManager.CONNECTIVITY_ACTION)");
						return;
						
					}
					
					NetworkInfo mNetworkInfo = (NetworkInfo) intent.getParcelableExtra(ConnectivityManager.EXTRA_NETWORK_INFO);
					
					if ( /*(mNetworkInfo == null) ||*/ (mNetworkInfo.getType() != ConnectivityManager.TYPE_MOBILE_MMS))
					{
						Log.e("MMS", "Network type : " + mNetworkInfo.getType());
						return;
					}
					
					if (!mNetworkInfo.isConnected())
					{
						Log.e("MMS", "Network was not connect");
						return;
					} else
					{
						Log.e("MMS", "sendData(recipient, parts)");
						
						sendData(recipient, parts);
						
						context.unregisterReceiver(this);
					}
					
				}

			};
			
			this.context.registerReceiver(receiver, filter);
		} else
		{
			sendData(recipient, parts);
		}
	}

	public void sendData( final String recipient, final MMSPart[] parts)
	{
	
		final Context ct = this.context;
		new Thread(new Runnable() {

			@Override
			public void run() {
				
				final SendReq sendRequest = new SendReq();

				final EncodedStringValue[] phoneNumber = EncodedStringValue.extract(recipient);
					
				if (phoneNumber != null && phoneNumber.length > 0)
				{
					sendRequest.addTo(phoneNumber[0]);
				}
				Log.e("MMS", "phone nmber: " + phoneNumber[0].toString());
				
				final PduBody pduBody = new PduBody();
				
				if (parts != null)
				{
					for (MMSPart part : parts)
					{
						if (part != null)
						{
							try
							{
								final PduPart partPdu = new PduPart();
								partPdu.setName(part.Name.getBytes());
								partPdu.setContentType(part.MimeType.getBytes());
								partPdu.setData(part.Data);
								pduBody.addPart(partPdu);
							} catch (Exception e)
							{
								
							}
						}
					}
				}
				
				Log.e("MMS", "pdu: " + pduBody.toString());
				
				sendRequest.setBody(pduBody);
				
				final PduComposer composer = new PduComposer(ct, sendRequest);
				final byte[] bytesToSend = composer.make();
				
				List<APN> apns = new ArrayList<APN>();
				SharedPreferences sharedPrefs = ct.getSharedPreferences(
						"netsoft_preferences", 0);
				
				try
				{
					APNHelper helper = new APNHelper(ct);
					apns = helper.getMMSApns();
				} catch (Exception e)
				{
					APN apn = new APN(sharedPrefs.getString("mmsc_url", ""), sharedPrefs.getString("mms_port", ""), sharedPrefs.getString("mms_proxy", ""));
					apns.add(apn);
				}
				
				Log.e("MMS", "APN: " + apns.get(0).MMSCenterUrl.toString());
				try {
					boolean isProxy = !TextUtils.isEmpty(apns.get(0).MMSProxy);
					int port  =  Integer.parseInt(apns.get(0).MMSPort);
					String Proxy = apns.get(0).MMSProxy.toString();
					String url = apns.get(0).MMSCenterUrl.toString();
					
					Log.e("MMS", "before  httpConnection");
					
					HttpUtils.httpConnection(ct, 4444L, url, bytesToSend, HttpUtils.HTTP_POST_METHOD, isProxy, Proxy , port);
				
					Log.e("MMS", "go next to httpConnection");
					ConnectivityManager mConnMgr =  (ConnectivityManager)ct.getSystemService(Context.CONNECTIVITY_SERVICE);
					mConnMgr.startUsingNetworkFeature(ConnectivityManager.TYPE_MOBILE, "enableMMS");
					
					IntentFilter filter = new IntentFilter();
					filter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
					
					
					BroadcastReceiver receiver = new BroadcastReceiver() {
			
						@Override
						public void onReceive(Context context, Intent intent) {
							Cursor query = context.getContentResolver().query(Uri.parse("content://mms"), new String[] {"_id"}, null, null, "date desc");
							query.moveToFirst();
							String id = query.getString(query.getColumnIndex("_id"));
							query.close();
							
							ContentValues values = new ContentValues();
					        values.put("msg_box", 2);
					        String where = "_id" + " = '" + id + "'";
					        context.getContentResolver().update(Uri.parse("content://mms"), values, where, null);
						    
					        context.unregisterReceiver(this);
						}
						
					};
					
					
					ct.registerReceiver(receiver, filter);
				} catch (Exception e) {
					Cursor query = ct.getContentResolver().query(Uri.parse("content://mms"), new String[] {"_id"}, null, null, "date desc");
					query.moveToFirst();
					String id = query.getString(query.getColumnIndex("_id"));
					query.close();
					
					ContentValues values = new ContentValues();
			        values.put("msg_box", 5);
			        String where = "_id" + " = '" + id + "'";
			        ct.getContentResolver().update(Uri.parse("content://mms"), values, where, null);
				    
			        
//					((Activity) context).getWindow().getDecorView().findViewById(android.R.id.content).post(new Runnable() {
//						
//						@Override
//						public void run() {
//							Toast.makeText(context, "MMS Error", Toast.LENGTH_SHORT).show();
//						}
//				    	
//				    });
				}
			}
		}).start();
					
	}
}
