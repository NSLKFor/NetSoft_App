package com.example.testsms;

import android.app.Activity;
import android.app.Fragment;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import android.telephony.SmsManager;
import android.telephony.SmsMessage;

public class MainActivity extends Activity {

	IntentFilter intentFilter;
	private BroadcastReceiver intentReceiver  = new BroadcastReceiver(){

		@Override
		public void onReceive(Context context, Intent intent) {
			// TODO Auto-generated method stub
			TextView txvReceiveMess = (TextView)findViewById(R.id.txvReceiveMessage);
			txvReceiveMess.setText(intent.getExtras().getString("sms"));
		}
		
	};
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
/*		

		if (savedInstanceState == null) {
			getFragmentManager().beginTransaction()
					.add(R.id.container, new PlaceholderFragment()).commit();
		}
		*/
		
		intentFilter = new IntentFilter();
		intentFilter.addAction("SMS_RECEIVED_ACTION");
		
		final EditText edtNumberPhone = (EditText)findViewById(R.id.edtNum);
		final EditText edtMessage = (EditText)findViewById(R.id.edtMess);
		final Button btnSend = (Button)findViewById(R.id.btSend);
		
		
		
		btnSend.setOnClickListener(new Button.OnClickListener(){
			public void onClick(View v){
				//SendSMS(edtNumberPhone.getText().toString(), edtMessage.getText().toString());
/*
				Intent i = new Intent (android.content.Intent.ACTION_VIEW);
				i.putExtra("address",  "0125284060; 0918600689");
				i.putExtra("sms_body", "Hello");
				i.setType("vnd.android-dir/mms-sms");
				startActivity(i);
				*/			
				}
			}
		);
		
		
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	/**
	 * A placeholder fragment containing a simple view.
	 */
	public static class PlaceholderFragment extends Fragment {

		public PlaceholderFragment() {
		}

		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container,
				Bundle savedInstanceState) {
			View rootView = inflater.inflate(R.layout.fragment_main, container,
					false);
			return rootView;
		}
	}
	public final void SendSMS(String numberPhone, String message){
		
		String SEND = "SMS_SENDED";
		String DELEVER = "SMS_DELEVERED";
		
		
		PendingIntent sendPI = PendingIntent.getBroadcast(this, 0, new Intent(SEND), 0);
		PendingIntent deliverPI = PendingIntent.getBroadcast(this, 0, new Intent(DELEVER), 0);
		
		registerReceiver(new BroadcastReceiver(){
			@Override
			public void onReceive(Context arg0, Intent arg1){
				switch(getResultCode())
				{
				case Activity.RESULT_OK:
					Toast.makeText(getBaseContext(), "SMS Sent", Toast.LENGTH_SHORT).show();
					break;
				case SmsManager.RESULT_ERROR_GENERIC_FAILURE:
					Toast.makeText(getBaseContext(), "Generic Failure", Toast.LENGTH_SHORT).show();
					break;
				case SmsManager.RESULT_ERROR_NO_SERVICE:
					Toast.makeText(getBaseContext(), "ERROR NO SERVICE", Toast.LENGTH_SHORT).show();
					break;
				case SmsManager.RESULT_ERROR_RADIO_OFF:
					Toast.makeText(getBaseContext(), "EROR RADIO OFF", Toast.LENGTH_SHORT).show();
					break;
				case SmsManager.RESULT_ERROR_NULL_PDU:
					Toast.makeText(getBaseContext(), "ERROR NULL PDU", Toast.LENGTH_SHORT).show();
					break;
			}
		}
			}, new IntentFilter(SEND));
		
		
		registerReceiver(new BroadcastReceiver(){
			@Override
			public void onReceive(Context arg0, Intent arg1){
				switch(getResultCode()){
				case Activity.RESULT_OK:
					Toast.makeText(getBaseContext(), "Deliver", Toast.LENGTH_SHORT).show();
				case Activity.RESULT_CANCELED:
					Toast.makeText(getBaseContext(), "SMS not Deliver", Toast.LENGTH_SHORT).show();
				}
			}
		}, new IntentFilter(DELEVER));
		
		SmsManager smsmgr = SmsManager.getDefault();				
		smsmgr.sendTextMessage(numberPhone, null, message,sendPI, deliverPI);
	}
	
	@Override
		protected void onResume() {
		registerReceiver(intentReceiver, intentFilter);
		super.onResume();
	}
	
	@Override
		protected void onPause() {
		registerReceiver(intentReceiver, intentFilter);
		super.onPause();
	}
	
}
