package com.netsoft.netsms;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.List;

import android.app.Fragment;
import android.app.ListActivity;
import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class ListSMSActivity extends ListActivity {

	private String address;
	private String name;
	private Uri thumnail;
	private TextView empty;
	private ImageView btnSend;
	
	private EditText edtMessage;
	private List<SmsItem> listSMS;
	private SmsAdapter smsAdapter;
	private SmsItem smsItem = new SmsItem();
	
	
	Bundle bundle;
	String strAdd = "";
	String strBody = "";
	String isNotify = "";
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
//		requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
		setContentView(R.layout.activity_list_sms);
//		getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE, R.layout.title_bar);
		
		// get address in application 
		NetSMSApplication application = (NetSMSApplication) getApplication();
		this.address = application.getAddress();
		this.name = application.getName();
		this.thumnail = application.getThumnail();
		
		this.empty = (TextView)findViewById(R.id.emptySMS);
		
		//*************** Get add from Bundle extra ****************
		
		
		bundle = getIntent().getExtras();
		if (bundle != null){
			if(bundle.getString("address") != null &&
					!(bundle.getString("address").equals(""))){
				strAdd = bundle.getString("address");
			}
			if(bundle.getString("body") != null && 
					!(bundle.getString("body").equals(""))){
				strBody = bundle.getString("body");
			}
			if(bundle.getString("isNotify") != null && 
					!(bundle.getString("isNotify").equals(""))){
				isNotify = bundle.getString("isNotify");
			}
			
			
			if(!strAdd.equals("")){
				address = strAdd;
				addMessage2List(strAdd, strBody);
			}

		}

		//**********************************************************
		
		// set title bar 
//		ImageButton Add = (ImageButton)findViewById(R.id.header);
//		Add.setVisibility(View.INVISIBLE);
		
// Set title name 
//		TextView nameTitle = (TextView)findViewById(R.id.txtTitle);
//		nameTitle.setText(address);
		
		
		setTitle(this.name);
		
		if(this.thumnail != null){ 
		    InputStream inputStream = null;
			try {
				inputStream = getContentResolver().openInputStream(this.thumnail);
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		    getActionBar().setIcon(Drawable.createFromStream(inputStream, this.thumnail.toString()));
		}
		
//		getActionBar().setIcon(icon);
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
		// set select in bottom o flistview 
		listView.setSelection(smsAdapter.getCount());
		
		
		edtMessage = (EditText)findViewById(R.id.EnterBox);
		
		btnSend = (ImageView) findViewById(R.id.imgSend);
		btnSend.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				
				
				if(edtMessage.getText().toString().equals("")){
					Toast.makeText(v.getContext(), "Your message is null \nPlease enter your message and try again.", Toast.LENGTH_SHORT).show();
					return;
				}
				else{
					smsItem.address = address;
					smsItem.body = edtMessage.getText().toString();
					smsItem.id = 1;
					smsItem.readStatus = 1;
					smsItem.type = 2;
					smsItem.date = System.currentTimeMillis();
				
					SMSSender sendSMS =  new SMSSender();
					sendSMS.sendSMSMessage(v.getContext(), smsItem);
				
					loadListContact();
					smsAdapter =  new SmsAdapter(ListSMSActivity.this, listSMS);
					setListAdapter(smsAdapter);
					listView.setSelection(smsAdapter.getCount());
				}
				
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
	
    
    @Override
    public void onBackPressed() {
    	Toast.makeText(this, "Press back button", Toast.LENGTH_LONG).show();
            super.onBackPressed();
            if(isNotify.equals("1")){           	
            	Intent intent = new Intent(Intent.ACTION_MAIN);
            	intent.putExtra("EXIT", "true");
            	intent.addCategory(Intent.CATEGORY_HOME);
            	intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            	startActivity(intent);
            }
    }

}
