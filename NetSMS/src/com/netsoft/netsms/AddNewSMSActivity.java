package com.netsoft.netsms;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.Random;

import com.netsoft.netmms.APNHelper;
import com.netsoft.netmms.MMSPart;

import android.app.Activity;
import android.app.Fragment;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.ContactsContract;
import android.provider.ContactsContract.PhoneLookup;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FilterQueryProvider;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;
import android.widget.Toast;
import android.content.ContentResolver;

public class AddNewSMSActivity extends Activity implements OnClickListener,
		TextWatcher {

	final String LOG_TAG = "AddnewMessage";

	ImageButton btnSendNew;
	ImageButton btnAttach;
	ImageView imgNewMessage;
	AutoCompleteTextView edtAddressNewMessage;
	EditText sendBodyNew;

	Cursor m_curContacts;
	SimpleCursorAdapter m_slvAdapter;

	Handler delayhandler = new Handler();

	SmsItem smsItem = new SmsItem();
	private final int SELECT_PHOTO = 100;
	private MMSPart[] parts;

	String mAddress = "";
	Bundle bundle;
	String add = "";
	String bd = "";
	String timeStamp = "";
	byte[] imgtemp = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_add_new_sms);

		// instance element
		edtAddressNewMessage = (AutoCompleteTextView) findViewById(R.id.addAddress);
		sendBodyNew = (EditText) findViewById(R.id.addEnterBox);
		imgNewMessage = (ImageView) findViewById(R.id.imgAttachNew);
		btnAttach = (ImageButton) findViewById(R.id.AttachButton);

		edtAddressNewMessage.addTextChangedListener((TextWatcher) this);

		edtAddressNewMessage.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				// TODO Auto-generated method stub
				// Toast.makeText(getApplicationContext(), "selected",
				// Toast.LENGTH_LONG).show();
				Cursor cursor = (Cursor) parent.getItemAtPosition(position);
				String szDisplayName = cursor.getString(cursor
						.getColumnIndexOrThrow(ContactsContract.Contacts.DISPLAY_NAME));
				String szId = cursor.getString(cursor
						.getColumnIndexOrThrow(ContactsContract.Contacts._ID));
				int nId = cursor.getInt(cursor
						.getColumnIndexOrThrow(ContactsContract.Contacts._ID));

				// Toast.makeText(getApplicationContext(), "Item click:" +
				// position + " szId:" + szId
				// + " nId:" + nId + " Data:" + szDisplayName,
				// Toast.LENGTH_LONG).show();

				String number = null;
				ContentResolver cr = getContentResolver();

				if (true) {
					Cursor pCur = cr.query(
							ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
							null,
							ContactsContract.CommonDataKinds.Phone.CONTACT_ID
									+ " = ?",
							new String[] { cursor.getString(cursor
									.getColumnIndex(ContactsContract.Contacts._ID)) },
							null);

					while (pCur.moveToNext()) {
						// Do something with phones
						int numColumnIndex = pCur
								.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER);
						// int numberFieldColumnIndex =
						pCur.getColumnIndex(PhoneLookup.NUMBER);
						number = pCur.getString(numColumnIndex);
					}
					cursor.close();
					pCur.close();
				}
				Toast.makeText(
						getBaseContext(),
						"Item click:" + position + " szId:" + szId + " nId:"
								+ nId + " number :" + number,
						Toast.LENGTH_SHORT).show();
				
				mAddress = number;

			}
		});
		btnAttach.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				
				if (m_curContacts != null)
					m_curContacts.close();
				Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
				photoPickerIntent.setType("image/*");
				startActivityForResult(photoPickerIntent, SELECT_PHOTO);
			}
		});

		btnSendNew = (ImageButton) findViewById(R.id.addSend);
		btnSendNew.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub

				// define info for new message
				smsItem.address = mAddress;// edtAddressNewMessage.getText().toString();
				smsItem.body = sendBodyNew.getText().toString();
				smsItem.id = "0";
				smsItem.readStatus = 1;
				smsItem.type = 2;
				smsItem.date = System.currentTimeMillis();

				// check error empty edit box
				if ((mAddress.equals("") || sendBodyNew.getText().toString()
						.equals(""))
						&& parts == null) {

					if (mAddress.equals("")
							&& !sendBodyNew.getText().toString().equals("")) {
						Toast.makeText(
								v.getContext(),
								"Address is Empty.\nPlease enter address and try again.",
								Toast.LENGTH_SHORT).show();
					}

					if (!mAddress.equals("")
							&& sendBodyNew.getText().toString().equals("")) {
						Toast.makeText(
								v.getContext(),
								"Your message is Empty. \nPlease enter your message and try again.",
								Toast.LENGTH_SHORT).show();
					}

					if (mAddress.equals("")
							&& sendBodyNew.getText().toString().equals("")) {
						Toast.makeText(
								v.getContext(),
								"Address and your message and is Empty. \nPlease enter address, your message and try again.",
								Toast.LENGTH_SHORT).show();
					}
					return;
				} else {

					if (mAddress.equals("")) {
						Toast.makeText(
								v.getContext(),
								"Address is Empty.\nPlease enter address and try again.",
								Toast.LENGTH_SHORT).show();
						return;
					}

					// check and send mms
					if (parts != null) {
						String address = mAddress;
						Toast.makeText(getApplicationContext(), "Send MMS",
								Toast.LENGTH_SHORT).show();

						APNHelper aHelper = new APNHelper(v.getContext());

//						aHelper.sendMMS(address, parts);
						
						Toast.makeText(getApplicationContext(),  "Send mms address: " + mAddress, Toast.LENGTH_SHORT).show();
						String[] tmpAdd = new String[1];
						tmpAdd[0] = address;
						aHelper.insert(v.getContext(), tmpAdd, "MMS to "
								+ address, parts[0].Data);

						Intent intent = new Intent(v.getContext(),
								MainActivity.class);
						intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
						startActivity(intent);

						// Intent smsReceiveIntent = new Intent(
						// AddNewSMSActivity.this, ListSMSActivity.class);
						// smsReceiveIntent
						// .setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
						// smsReceiveIntent.putExtra("address", address);
						// smsReceiveIntent.putExtra("body", "MMS to " +
						// address);
						// smsReceiveIntent.putExtra("time",
						// System.currentTimeMillis());
						// smsReceiveIntent.putExtra("img", parts[0].Data);
						// smsReceiveIntent.putExtra("isNotify", "0");
						// startActivity(smsReceiveIntent);
						return;
					}

					// check and send sms
					if (!sendBodyNew.getText().toString().equals("")) {
						SMSSender sendSMS = new SMSSender();
			//			sendSMS.sendSMSMessage(v.getContext(), smsItem);
						
						Toast.makeText(getApplicationContext(), "Send sms address: " + mAddress, Toast.LENGTH_SHORT).show();

						Intent intent = new Intent(v.getContext(),
								MainActivity.class);
						intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
						startActivity(intent);
					}

				}
			}
		});
	}

	private void ReadContacts(String sort) {
		// TODO Auto-generated method stub
		final Uri uri = ContactsContract.Contacts.CONTENT_URI;
		final String[] projection = new String[] {
				ContactsContract.Contacts._ID,
				ContactsContract.Contacts.DISPLAY_NAME };
		// boolean mShowInvisible = false;
		String selection = ContactsContract.Contacts.IN_VISIBLE_GROUP
				+ " = '1'";
		String[] selectionArgs = null;
		final String sortOrder = ContactsContract.Contacts.DISPLAY_NAME
				+ " COLLATE LOCALIZED ASC";

		m_curContacts = managedQuery(uri, projection, selection, selectionArgs,
				sortOrder);

		String[] fields = new String[] { ContactsContract.Data.DISPLAY_NAME };
		m_slvAdapter = new SimpleCursorAdapter(this,
				android.R.layout.simple_list_item_1, m_curContacts, fields,
				new int[] { android.R.id.text1 });

		m_slvAdapter.setStringConversionColumn(1);

		m_slvAdapter.setFilterQueryProvider(new FilterQueryProvider() {

			public Cursor runQuery(CharSequence constraint) {
				Log.d(LOG_TAG, "runQuery constraint:" + constraint);
				String selection = ContactsContract.Contacts.IN_VISIBLE_GROUP
						+ " = '1'" + " AND "
						+ ContactsContract.Contacts.DISPLAY_NAME + " LIKE '%"
						+ constraint + "%'";
				String[] selectionArgs = null;// new String[]{"'1'"};//, };
				Cursor cur = managedQuery(uri, projection, selection,
						selectionArgs, sortOrder);
				return cur;
			}

		});

		edtAddressNewMessage.setAdapter(m_slvAdapter);
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

	private Runnable mUpdateTimeTask = new Runnable() {
		public void run() { // Todo

			// This line is necessary for the next call
			// delayhandler.postDelayed(this, 200);
			// btnSend.setImageResource(R.drawable.send_icon);

		}
	};

	@Override
	protected void onActivityResult(int requestCode, int resultCode,
			Intent imageReturnedIntent) {
		super.onActivityResult(requestCode, resultCode, imageReturnedIntent);
		switch (requestCode) {
		case SELECT_PHOTO:

			if (resultCode == RESULT_OK) {
				Uri selectedImage = imageReturnedIntent.getData();
				InputStream imageStream;
				try {
					imageStream = getContentResolver().openInputStream(
							selectedImage);
					Bitmap yourSelectedImage = BitmapFactory
							.decodeStream(imageStream);
					imgNewMessage.setImageBitmap(yourSelectedImage);
					int res = 100;

					ByteArrayOutputStream stream = new ByteArrayOutputStream();

					String mimeType = ListSMSActivity.GetMimeType(this,
							selectedImage);

					// callculator compress
					if (mimeType.equals("image/jpeg")) {
						if ((yourSelectedImage.getByteCount() / 1000) > 4000) {
							res = 50;
						} else {
							if ((yourSelectedImage.getByteCount() / 1000) > 2000) {
								res = 80;
							} else {
								if ((yourSelectedImage.getByteCount() / 1000) > 1000) {
									res = 85;
								} else {
									if ((yourSelectedImage.getByteCount() / 1000) > 500) {
										res = 90;
									} else {
										if ((yourSelectedImage.getByteCount() / 1000) > 300) {
											res = 95;
										}
									}

								}
							}
						}

						yourSelectedImage.compress(Bitmap.CompressFormat.JPEG,
								res, stream);
					}
					if (mimeType.equals("image/png")) {
						if ((yourSelectedImage.getByteCount() / 1000) > 4000) {
							res = 70;
						} else {
							if ((yourSelectedImage.getByteCount() / 1000) > 2000) {
								res = 90;
							} else {
								if ((yourSelectedImage.getByteCount() / 1000) > 1000) {
									res = 90;
								} else {
									if ((yourSelectedImage.getByteCount() / 1000) > 500) {
										res = 95;
									} else {
										if ((yourSelectedImage.getByteCount() / 1000) > 300) {
											res = 98;
										}
									}

								}
							}
						}
						yourSelectedImage.compress(Bitmap.CompressFormat.PNG,
								res, stream);
					}

					byte[] byteArray = stream.toByteArray();

					Toast.makeText(
							getApplicationContext(),
							"Lenght image: " + yourSelectedImage.getByteCount()
									/ 1000 + " KB" + "After cpmpress: "
									+ byteArray.length / 1000,
							Toast.LENGTH_LONG).show();

					parts = new MMSPart[1];
					parts[0] = new MMSPart();
					parts[0].Name = "Image";
					parts[0].MimeType = mimeType;
					parts[0].Data = byteArray;

				} catch (FileNotFoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

			}
		}
	}

	@Override
	public void beforeTextChanged(CharSequence s, int start, int count,
			int after) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onTextChanged(CharSequence s, int start, int before, int count) {
		// TODO Auto-generated method stub
		if (m_slvAdapter != null) {
			m_slvAdapter.getFilter().filter(s);
			// m_lvContacts.setAdapter(m_slvAdapter);
			edtAddressNewMessage.setWidth(200);
			edtAddressNewMessage.setAdapter(m_slvAdapter);
		}
	}

	@Override
	public void afterTextChanged(Editable s) {
		// TODO Auto-generated method stub
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub

	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		if (m_curContacts != null)
			m_curContacts.close();

	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		ReadContacts("");
	}

}
