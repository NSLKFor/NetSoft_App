package com.netsoft.netsms;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

public class NotifySMS extends Activity{
	
	Bundle bundle;
	String add = "";
	String bd = "";
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		bundle = getIntent().getExtras();
		
		add  = bundle.getString("add");
		bd  = bundle.getString("bd");
		
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setMessage(" Message from: 01252840600\n Time: today\n This is a test dialod alert for NetSMS")
		.setCancelable(false)
		.setPositiveButton("Reply", new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				// TODO Auto-generated method stub
				
				Intent smsReceiveIntent = new Intent (NotifySMS.this, ListSMSActivity.class);
				smsReceiveIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				smsReceiveIntent.putExtra("address", add);
				smsReceiveIntent.putExtra("body", bd);
				NotifySMS.this.startActivity(smsReceiveIntent);
				
			}
		})
		.setNegativeButton("Close", new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				// TODO Auto-generated method stub
				
			}
		});
		AlertDialog alert = builder.create();
		alert.show();
		
		
		AlertMessage(this, add, bd);
		
		
		
	}
	
	
	@SuppressWarnings("deprecation")
	public void AlertMessage(Context context,String adds, String body){
		
//		Intent smsReceiveIntent = new Intent (context, ListSMSActivity.class);
//		smsReceiveIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//		smsReceiveIntent.putExtra("address", adds);
//		smsReceiveIntent.putExtra("body", body);
//		context.startActivity(smsReceiveIntent);

		
		NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
		Notification notification = new Notification(R.drawable.ic_launcher, adds + ": New message" , System.currentTimeMillis());	
		
		Intent notificationIntent = new Intent(context, ListSMSActivity.class);		
		notificationIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		notificationIntent.putExtra("address", adds);
		notificationIntent.putExtra("body", body);
				
		PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent, 0);
		notification.setLatestEventInfo(context, adds , body, pendingIntent);		
		notificationManager.notify(9999, notification);
		
		
	
		
		
	}
	
	

}


//private void Notify (String notificationTitle, String notificationMessage){
//	
//	//*********************** Show switch content on atatus bar !!!!!!!!!!!!!!!"will updatefor logic in future  "*************
//
//			NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
//			Notification notification = new Notification(R.drawable.ic_launcher, strAdd + ": New message" , System.currentTimeMillis());
//			
//			Intent notificationIntent = new Intent(this, ListSMSActivity.class);
//			PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent, 0);
////			notificationIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
////			notificationIntent.putExtra("address", strAdd);
////			notificationIntent.putExtra("body", strBody);
//			
//			notification.setLatestEventInfo(ListSMSActivity.this, strAdd , strBody, pendingIntent);
//			notificationManager.notify(9999, notification);
//			
//	//************************************************************************************************************************
//		}

