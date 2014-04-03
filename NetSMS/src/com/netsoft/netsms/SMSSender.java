package com.netsoft.netsms;

import android.telephony.SmsManager;
import android.widget.Toast;

public class SMSSender {
	public void sendSMSMessage(String numberPhone, String message){
		SmsManager sms = SmsManager.getDefault();
		sms.sendTextMessage(numberPhone, null, message, null, null);
	}
}
