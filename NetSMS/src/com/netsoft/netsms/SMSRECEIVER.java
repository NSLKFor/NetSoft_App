package com.netsoft.netsms;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.SmsMessage;
import android.widget.Toast;

public class SMSRECEIVER extends BroadcastReceiver{
	@Override
	public void onReceive(Context context, Intent intent) {
		// TODO Auto-generated method stub
		

		
		
		Bundle bundle = intent.getExtras();
		SmsMessage[] msgs = null;
		String address = "";
		String body = "";
		if(bundle != null){
			Object[] pdus  = (Object[]) bundle.get("pdus");
			msgs = new SmsMessage[pdus.length];
			for(int i = 0; i< msgs.length; i++){
				msgs[i] = SmsMessage.createFromPdu((byte[])pdus[i]);
				address += msgs[i].getOriginatingAddress();
				
				body += msgs[i].getMessageBody().toString();
			}
			
			Toast.makeText(context, "Receive message from: " + address, Toast.LENGTH_LONG).show();
			
/*			Set broadcast intent to action into app to receice 
			Intent mainActivityIntent = new Intent (context, MainActivity.class);
			mainActivityIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			context.startActivity(mainActivityIntent);
			
			//Intent broadcastIntent  = new Intent();
			//broadcastIntent.setAction("SMS_RECEIVED_ACTION");
			//broadcastIntent.putExtra("sms", str);
			//context.sendBroadcast(broadcastIntent);
			//this.abortBroadcast();
			
*/
			
//			
//			NotifySMS notiSMS = new NotifySMS();
//			
//			notiSMS.AlertMessage(context,address, body);
			
			
			
			
			Intent smsReceiveIntent = new Intent (context, NotifySMS.class);
			smsReceiveIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			smsReceiveIntent.putExtra("add", address);
			smsReceiveIntent.putExtra("bd", body);
		
			context.startActivity(smsReceiveIntent);
			
			
			
		}
	}
}
