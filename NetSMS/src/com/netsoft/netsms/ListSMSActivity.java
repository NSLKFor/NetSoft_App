package com.netsoft.netsms;

import java.util.List;

import android.app.Fragment;
import android.app.ListActivity;
import android.content.ContentResolver;
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

public class ListSMSActivity extends ListActivity {

	private String address;
	private TextView empty;
	private List<SmsItem> listSMS;
	private SmsAdapter smsAdapter;
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_list_sms);
		NetSMSApplication application = (NetSMSApplication) getApplication();
		address = application.getAddress();
		
		this.empty = (TextView)findViewById(R.id.emptySMS);
		
		final ListView listView = getListView();
		listView.setItemsCanFocus(false);
		listView.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
		listView.setEmptyView(this.empty);
		
//		if (savedInstanceState == null) {
//			getFragmentManager().beginTransaction()
//					.add(R.id.container, new PlaceholderFragment()).commit();
//		}
		
		loadListContact();
		smsAdapter =  new SmsAdapter(ListSMSActivity.this, listSMS);
		setListAdapter(smsAdapter);
		
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

}
