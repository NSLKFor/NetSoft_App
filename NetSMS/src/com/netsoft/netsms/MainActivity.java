package com.netsoft.netsms;

import java.security.GeneralSecurityException;
import java.util.List;

import com.netsoft.constant.Constants;

import android.app.Fragment;
import android.app.ListActivity;
import android.app.NotificationManager;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends ListActivity {
	private TextView empty;
	// private List<SmsItem>smsItems;
	// private SmsAdapter sadapter;
	private ListContactAdapter listContactAdapter;
	private List<ListContactItem> listContact;
	private ProgressDialog progressDialog;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		Toast.makeText(getApplicationContext(), "onCreate of Main Activity", Toast.LENGTH_SHORT).show();
		NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
		notificationManager.cancel(9999);

		setContentView(R.layout.activity_main);

		this.empty = (TextView) findViewById(R.id.empty);

		final ListView listView = getListView();
		listView.setItemsCanFocus(false);
		listView.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
		listView.setEmptyView(this.empty);

	
		/*
		 * title of action bar
		 * 
		 * TextView nameTitle = (TextView)findViewById(R.id.txtTitle);
		 * nameTitle.setText("NetSMS");
		 * 
		 * 
		 * ImageButton Add = (ImageButton)findViewById(R.id.header);
		 * Add.setOnClickListener(new View.OnClickListener() {
		 * 
		 * @Override public void onClick(View v) { // TODO Auto-generated method
		 * stub Intent intent = new Intent(v.getContext(),
		 * AddNewSMSActivity.class); startActivity(intent); } });
		 */

	}
	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		//Toast.makeText(getApplicationContext(), "Onresume of Main Activity", Toast.LENGTH_SHORT).show();
		NetSMSApplication application = (NetSMSApplication) getApplication();
		listContact = application.getListContactItem();
		if(listContact == null ){
			loadListContact(MainActivity.this);
		}else{
			listContactAdapter = new ListContactAdapter(MainActivity.this,
					listContact);
			setListAdapter(listContactAdapter);
		}
	}
	

	private void loadListContact(final Context context) {
		// TODO Auto-generated method stub
		if (listContact == null) {
			progressDialog = ProgressDialog.show(this, "Load Data",
					"Loading ... ", true, false);
		}

		new Thread(){
			public void run() {
				final ListContactFetcher lf = new ListContactFetcher();
				listContact = lf.getListContact(context);
				handler.sendEmptyMessage(Constants.MSG_GET_ITEMS);
			}
		}.start();

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
		if (id == R.id.action_new_sms) {
			NetSMSApplication application = (NetSMSApplication) getApplication();
			application.setListContactItem(listContact);
			
			Intent intent = new Intent(this, AddNewSMSActivity.class);
			intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
			startActivity(intent);
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

	protected void onListItemClick(ListView l, View v, int position, long id) {
		// get selected items
		
		ListContactItem listContactItem = (ListContactItem) getListAdapter()
				.getItem(position);
		Toast.makeText(this, listContactItem.address.toString(),
				Toast.LENGTH_SHORT).show();

		NetSMSApplication application = (NetSMSApplication) getApplication();
		application.setAddress(listContactItem.address.toString());

		if (listContactItem.name == null) {
			application.setName(listContactItem.address.toString());
		} else {
			application.setName(listContactItem.name.toString());
		}
		// listContact.set(, object)
		application.setThumnail(listContactItem.thumnail);
		application.setListContactItem(listContact);

		listContactItem.readStatus = 1;
		listContactAdapter.updateItem(position, listContactItem);
		listContactAdapter.notifyDataSetChanged();

		Intent intent = new Intent(this, ListSMSActivity.class);
		startActivity(intent);
	}

	private final Handler handler = new Handler() {
		@Override
		public void handleMessage(final Message msg) {
			switch (msg.what) {
			case Constants.MSG_GET_ITEMS:
				progressDialog.dismiss();
				
				listContactAdapter = new ListContactAdapter(MainActivity.this,
						listContact);
				setListAdapter(listContactAdapter);
				
				if (listContact == null || listContact.size() == 0) {
					empty.setText("No Data");
				} else {
					listContactAdapter = new ListContactAdapter(MainActivity.this,
							listContact);
					setListAdapter(listContactAdapter);
				}
				break;
			}
		}
	};

}
