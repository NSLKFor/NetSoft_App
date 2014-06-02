package com.netsoft.netsms;

import java.text.DateFormat;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.SmsMessage;
import android.widget.Toast;

//get 
public class SMSRECEIVER extends BroadcastReceiver {
	@Override
	public void onReceive(Context context, Intent intent) {
		// TODO Auto-generated method stub

		Bundle bundle = intent.getExtras();
		SmsMessage[] msgs = null;
		String address = "";
		String body = "";
		String timeStamp = "";
		if (bundle != null) {
			Object[] pdus = (Object[]) bundle.get("pdus");
			msgs = new SmsMessage[pdus.length];
			for (int i = 0; i < msgs.length; i++) {
				msgs[i] = SmsMessage.createFromPdu((byte[]) pdus[i]);
				address = msgs[i].getOriginatingAddress();

				body = msgs[i].getMessageBody().toString();

				timeStamp = DateFormat.getInstance().format(
						msgs[i].getTimestampMillis());
			}

			Toast.makeText(context, "Receive message from: " + address,
					Toast.LENGTH_LONG).show();


			Intent smsReceiveIntent = new Intent(context, NotifySMS.class);
			smsReceiveIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			smsReceiveIntent.putExtra("add", address);
			smsReceiveIntent.putExtra("bd", body);
			smsReceiveIntent.putExtra("timeStamp", timeStamp);
			smsReceiveIntent.putExtra("EXIT", "");

			context.startActivity(smsReceiveIntent);

		}
	}
}
