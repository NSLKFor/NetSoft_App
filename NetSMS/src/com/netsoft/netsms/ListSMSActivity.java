package com.netsoft.netsms;

import java.util.List;

import android.app.Fragment;
import android.app.ListActivity;
import android.content.ContentResolver;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class ListSMSActivity extends ListActivity {

	private String address;
	private TextView empty;
	private Button btnSend;
	private EditText edtMessage;
	private List<SmsItem> listSMS;
	private SmsAdapter smsAdapter;
	
	Bundle bundle;
	String strAdd = "";
	String strBody = "";
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_list_sms);
		// get address in application 
		NetSMSApplication application = (NetSMSApplication) getApplication();
		this.address = application.getAddress();
		
		this.empty = (TextView)findViewById(R.id.emptySMS);
		
		//*************** Get add from Bundle extra ****************
		
		bundle = getIntent().getExtras();
		if (bundle != null){
			if(bundle.getString("address") != null ||
					!(bundle.getString("address").equals(""))){
				strAdd = bundle.getString("address");
			}
			if(bundle.getString("body") != null || 
					!(bundle.getString("body").equals(""))){
				strBody = bundle.getString("body");
			}
			
			if(!strAdd.equals("")){
				address = strAdd;
				addMessage2List(strAdd, strBody);
			}

		}

		//**********************************************************
		
		//***********Set ListView to render **************
		final ListView listView = getListView();
		listView.setItemsCanFocus(false);
		listView.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
		listView.setEmptyView(this.empty);
		
		//**************
		
		//***********Load List contact sms *************
		loadListContact();
		
		//*******************************************
		smsAdapter =  new SmsAdapter(ListSMSActivity.this, listSMS);
		setListAdapter(smsAdapter);
		
		edtMessage = (EditText)findViewById(R.id.EnterBox);
		
		btnSend = (Button) findViewById(R.id.sendButton);
		btnSend.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				SMSSender sendSMS =  new SMSSender();
				sendSMS.sendSMSMessage(address, edtMessage.getText().toString());
				
			}

		});
		
	}

	private void addMessage2List(String strAdd2, String strBody2) {
		// TODO Auto-generated method stub
		
		final SmsFetcher sf=  new SmsFetcher(strAdd2);	
	    
		Uri message = Uri.parse("content://sms/");
		ContentResolver cr = getContentResolver();
		Cursor cursor = cr.query(message, null, null, null, null);

		listSMS = sf.addItem2List(cursor, strBody2);
	}

	private void loadListContact() {
		// TODO Auto-generated method stub
		final SmsFetcher sf=  new SmsFetcher(this.address);	
	    
		Uri message = Uri.parse("content://sms/");
		ContentResolver cr = getContentResolver();
		Cursor cursor = cr.query(message, null, null, null, null);

		listSMS = sf.getListSMS(cursor);
	}


	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.list_sm, menu);
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
			View rootView = inflater.inflate(R.layout.fragment_list_sm,
					container, false);
			return rootView;
		}
	}
	
//    @Override
//    public boolean onKeyDown(int keyCode, KeyEvent event)
//    {
//        if ((keyCode == KeyEvent.KEYCODE_BACK))
//        {
//        	Toast.makeText(this, "Press back button", Toast.LENGTH_LONG).show();
//        }
//        return super.onKeyDown(keyCode, event);
//    }
    
    @Override
    public void onBackPressed() {
    	Toast.makeText(this, "Press back button", Toast.LENGTH_LONG).show();
            super.onBackPressed();
            this.finish();
    }

}
