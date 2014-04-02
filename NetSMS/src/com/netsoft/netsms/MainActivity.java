package com.netsoft.netsms;

import java.util.List;

import android.app.Fragment;
import android.app.ListActivity;
import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends ListActivity {
	private TextView empty;
//	private List<SmsItem>smsItems;
//	private SmsAdapter sadapter;
	private ListContactAdapter listContactAdapter;
	private List<String> listContact;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

//		if (savedInstanceState == null) {
//			getFragmentManager().beginTransaction()
//					.add(R.id.container, new PlaceholderFragment()).commit();
//		}
		
		this.empty = (TextView) findViewById(R.id.empty);
		
		final ListView listView = getListView();
		listView.setItemsCanFocus(false);
		listView.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
		listView.setEmptyView(this.empty);
		
		
		loadListContact();
		listContactAdapter =  new ListContactAdapter(MainActivity.this, listContact);
		setListAdapter(listContactAdapter);
		
		
	}
	
	private void loadListContact() {
		// TODO Auto-generated method stub
		final ListContactFetcher lf=  new ListContactFetcher();	
    
		Uri message = Uri.parse("content://sms/");
		ContentResolver cr = getContentResolver();
		Cursor cursor = cr.query(message, null, null, null, null);

		listContact = lf.getListContact(cursor);
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

    protected void onListItemClick(ListView l, View v, int position, long id) {              
        //get selected items
        String address = (String) getListAdapter().getItem(position);
        Toast.makeText(this, address, Toast.LENGTH_SHORT).show();
        
        NetSMSApplication application = (NetSMSApplication) getApplication();
		application.setAddress(address);
		
		
		Intent intent = new Intent(this, ListSMSActivity.class);
		startActivity(intent);
		

}
}
