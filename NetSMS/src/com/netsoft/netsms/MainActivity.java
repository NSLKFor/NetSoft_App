package com.netsoft.netsms;

import java.util.List;

import android.app.Fragment;
import android.app.ListActivity;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
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

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
		setContentView(R.layout.activity_main);
		// getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE,
		// R.layout.title_bar);

		this.empty = (TextView) findViewById(R.id.empty);

		final ListView listView = getListView();
		listView.setItemsCanFocus(false);
		listView.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
		listView.setEmptyView(this.empty);

		loadListContact(MainActivity.this);
		listContactAdapter = new ListContactAdapter(MainActivity.this,
				listContact);
		setListAdapter(listContactAdapter);

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

	private void loadListContact(Context context) {
		// TODO Auto-generated method stub
		final ListContactFetcher lf = new ListContactFetcher();

		Uri message = Uri.parse("content://sms/");
		ContentResolver cr = getContentResolver();
		Cursor cursor = cr.query(message, null, null, null, null);

		listContact = lf.getListContact(context, cursor);
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
			Intent intent = new Intent(this, AddNewSMSActivity.class);
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

		listContactItem.readStatus = 1;
		listContactAdapter.updateItem(position, listContactItem);
		setListAdapter(listContactAdapter);
		listContactAdapter.notifyDataSetChanged();

		Intent intent = new Intent(this, ListSMSActivity.class);
		startActivity(intent);

	}

}
