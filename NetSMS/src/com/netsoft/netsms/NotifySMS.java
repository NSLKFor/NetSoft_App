package com.netsoft.netsms;

import java.io.File;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;

public class NotifySMS extends Activity {

	Bundle bundle;
	String add = "";
	String bd = "";
	long timeStamp = 0;
	byte[] imgtemp = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		bundle = getIntent().getExtras();

		if (bundle.getString("EXIT").equals("true")) {
			finish();
		}

		add = bundle.getString("add");
		bd = bundle.getString("bd");
		timeStamp = bundle.getLong("timeStamp");
		imgtemp = bundle.getByteArray("img");

		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setMessage(
				"New message from: " + add + "\n" + bd + "\n" + timeStamp)
				.setCancelable(false)
				.setPositiveButton("Reply",
						new DialogInterface.OnClickListener() {

							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								// TODO Auto-generated method stub

								Intent smsReceiveIntent = new Intent(
										NotifySMS.this, ListSMSActivity.class);
								smsReceiveIntent
										.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
								smsReceiveIntent.putExtra("address", add);
								smsReceiveIntent.putExtra("body", bd);
								smsReceiveIntent.putExtra("time", timeStamp);
								smsReceiveIntent.putExtra("img", imgtemp);
								smsReceiveIntent.putExtra("isNotify", "1");
								startActivity(smsReceiveIntent);

							}
						})
				.setNegativeButton("Close",
						new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								// TODO Auto-generated method stub
								// Intent intent = new
								// Intent(Intent.ACTION_MAIN);
								// intent.addCategory(Intent.CATEGORY_HOME);
								// intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
								// startActivity(intent);

								Intent intent = getIntent();
								intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
								intent.putExtra("EXIT", "true");
								startActivity(intent);

							}
						});
		AlertDialog alert = builder.create();
		alert.show();

		AlertMessage(this, add, bd);
		
//		File f = new File("/sdcard/AAAAAAA.jpeg");
//		Bitmap bm = BitmapFactory.decodeFile(f.getAbsolutePath());


	}


	@SuppressWarnings("deprecation")
	public void AlertMessage(Context context, String adds, String body) {

		// Intent smsReceiveIntent = new Intent (context,
		// ListSMSActivity.class);
		// smsReceiveIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		// smsReceiveIntent.putExtra("address", adds);
		// smsReceiveIntent.putExtra("body", body);
		// context.startActivity(smsReceiveIntent);

		NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
		Notification notification = new Notification(R.drawable.ic_launcher,
				adds + ": New message", System.currentTimeMillis());
		notification.flags = Notification.DEFAULT_LIGHTS
				| Notification.DEFAULT_SOUND | Notification.FLAG_AUTO_CANCEL
				| Notification.FLAG_SHOW_LIGHTS;

		Intent notificationIntent = new Intent(context, ListSMSActivity.class);
		notificationIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		notificationIntent.putExtra("address", adds);
		notificationIntent.putExtra("body", body);

		PendingIntent pendingIntent = PendingIntent.getActivity(this, 0,
				notificationIntent, 0);
		notification.setLatestEventInfo(context, adds, body, pendingIntent);
		notificationManager.notify(9999, notification);
		// notificationManager.cancel(9999);

	}
	
	@Override
	protected void onRestart() {
		// TODO Auto-generated method stub
		super.onRestart();
		Intent intent = new Intent(this, MainActivity.class);
		intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
		startActivity(intent);
	}

}

// private void Notify (String notificationTitle, String notificationMessage){
//
// //*********************** Show switch content on atatus bar
// !!!!!!!!!!!!!!!"will updatefor logic in future  "*************
//
// NotificationManager notificationManager = (NotificationManager)
// getSystemService(NOTIFICATION_SERVICE);
// Notification notification = new Notification(R.drawable.ic_launcher, strAdd +
// ": New message" , System.currentTimeMillis());
//
// Intent notificationIntent = new Intent(this, ListSMSActivity.class);
// PendingIntent pendingIntent = PendingIntent.getActivity(this, 0,
// notificationIntent, 0);
// // notificationIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
// // notificationIntent.putExtra("address", strAdd);
// // notificationIntent.putExtra("body", strBody);
//
// notification.setLatestEventInfo(ListSMSActivity.this, strAdd , strBody,
// pendingIntent);
// notificationManager.notify(9999, notification);
//
// //************************************************************************************************************************
// }

