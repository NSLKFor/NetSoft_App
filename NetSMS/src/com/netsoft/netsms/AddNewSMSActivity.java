package com.netsoft.netsms;

import java.util.Random;

import android.app.Activity;
import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

public class AddNewSMSActivity extends Activity {

	Button btnSendNew;
	EditText addNew;
	EditText sendBodyNew;
	
	SmsItem smsItem =  new SmsItem();
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
		setContentView(R.layout.activity_add_new_sms);
		getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE, R.layout.title_bar);
		
		ImageButton Add = (ImageButton)findViewById(R.id.header);
		TextView nameTitle = (TextView)findViewById(R.id.txtTitle);
		nameTitle.setText("New message");
		

//		if (savedInstanceState == null) {
//			getFragmentManager().beginTransaction()
//					.add(R.id.container, new PlaceholderFragment()).commit();
//		}
		
		
		addNew = (EditText)findViewById(R.id.addAddress);
		sendBodyNew = (EditText)findViewById(R.id.addEnterBox);
		
		btnSendNew = (Button) findViewById(R.id.addSend);
		btnSendNew.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				
				smsItem.address = addNew.getText().toString();
				smsItem.body = sendBodyNew.getText().toString();
				smsItem.id = 0;
				smsItem.readStatus = 1;
				smsItem.type = 2;
				smsItem.date = System.currentTimeMillis();
				
				SMSSender sendSMS =  new SMSSender();
//				sendSMS.sendSMSMessage(addNew.getText().toString(), sendBodyNew.getText().toString());
				sendSMS.sendSMSMessage(v.getContext(), smsItem);
				
				Intent intent = new Intent(v.getContext(), MainActivity.class);
				intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				startActivity(intent);
			}
		});
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.add_new_sm, menu);
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
			View rootView = inflater.inflate(R.layout.fragment_add_new_sm,
					container, false);
			return rootView;
		}
	}

}
