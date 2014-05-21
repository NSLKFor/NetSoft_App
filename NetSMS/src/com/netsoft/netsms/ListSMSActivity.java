package com.netsoft.netsms;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.atomic.AtomicBoolean;

import android.app.Fragment;
import android.app.ListActivity;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint.Align;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.provider.MediaStore;
import android.provider.Telephony;
import android.text.StaticLayout;
import android.text.TextPaint;
import android.text.Layout.Alignment;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.netsoft.constant.Constants;
import com.netsoft.netmms.*;

public class ListSMSActivity extends ListActivity {

	private String address;
	private String name;
	private Uri thumnail;
	private TextView empty;
	private ImageButton btnSend;
	private ImageButton btnAttach;
	private ImageView imgAttach;
	private ListView listView;

	private EditText edtMessage;
	private List<SmsItem> listSMS;
	private SmsAdapter smsAdapter;
	private SmsItem smsItem = new SmsItem();
	private MMSPart[] parts;

	private ProgressDialog progressDialog;
	private static final int SELECT_PHOTO = 100;

	Bundle bundle;
	String strAdd = "";
	String strBody = "";
	String isNotify = "";
	long strTime = 0;
	byte[] img = null;
	Handler delayhandler = new Handler();
	Handler handler = new Handler();
	boolean isrunning = false;
	boolean stackRunning = false;
	boolean isGetSMS = false;
	boolean isGetMMS = false;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_list_sms);
		// get address in application
		NetSMSApplication application = (NetSMSApplication) getApplication();
		this.address = application.getAddress();
		this.name = application.getName();
		this.thumnail = application.getThumnail();

		listSMS = new ArrayList<SmsItem>();

		btnAttach = (ImageButton) findViewById(R.id.listAttachButton);
		imgAttach = (ImageView) findViewById(R.id.listMMSImage);

		this.empty = (TextView) findViewById(R.id.emptySMS);

		edtMessage = (EditText) findViewById(R.id.EnterBox);
		edtMessage.forceLayout();
		btnSend = (ImageButton) findViewById(R.id.imgSend);

		// ***********Set ListView to render **************
		listView = getListView();
		listView.setItemsCanFocus(false);
		listView.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
		listView.setEmptyView(this.empty);

		// **************

	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();

		// *************** Get add from Bundle extra ****************
		bundle = getIntent().getExtras();
		if (bundle != null) {
			if (bundle.getString("address") != null
					&& !(bundle.getString("address").equals(""))) {
				strAdd = bundle.getString("address");
			}
			if (bundle.getString("body") != null
					&& !(bundle.getString("body").equals(""))) {
				strBody = bundle.getString("body");
			}
			if (bundle.getLong("time") != 0) {
				strTime = bundle.getLong("time");
			}
			if (bundle.getString("isNotify") != null
					&& !(bundle.getString("isNotify").equals(""))) {
				isNotify = bundle.getString("isNotify");
			}
			if (bundle.getByteArray("img") != null) {
				img = bundle.getByteArray("img");
			}

			if (!strAdd.equals("")) {
				String phone = "";
				phone = formatToStandardNumber(strAdd);

				this.address = phone;
				addMessage2List(this, address, strBody, img, strTime);
				this.name = ListContactFetcher.getContactName(
						getApplicationContext(), phone);
				long contact_ID = ListContactFetcher
						.fetchContactIdFromPhoneNumber(getApplicationContext(),
								phone);

				this.thumnail = ListContactFetcher.getPhotoUri(
						getApplicationContext(), contact_ID);
			}
		} else {
			// ***********Load List contact sms *************

			NetSMSApplication application = (NetSMSApplication) getApplication();
			this.address = application.getAddress();
			this.name = application.getName();
			this.thumnail = application.getThumnail();

			// error in here did not get listSMSApplication so this application
			// no instance
			// ListSMSApplication listSMSApplication = (ListSMSApplication)
			// getApplication();
			// if (this.address == listSMSApplication.getAddress()) {
			// this.listSMS = listSMSApplication.getListSMSItem();
			// smsAdapter.notifyDataSetChanged();
			// } else {
			address = formatToStandardNumber(address);
			loadListContact(this);
			// }
		}

		// *******************************************
		setTitle(this.name);
		if (this.thumnail != null) {
			InputStream inputStream = null;
			try {
				inputStream = getContentResolver().openInputStream(
						this.thumnail);
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			getActionBar().setIcon(
					Drawable.createFromStream(inputStream,
							this.thumnail.toString()));
		}

		btnSend.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub

				// btnSend.setImageResource(R.drawable.send_selected_icon);
				// delayhandler.postDelayed(mUpdateTimeTask, 100);

				if (parts != null) {
					APNHelper aHelper = new APNHelper(v.getContext());
					 aHelper.sendMMS(address, parts);
					String[] tmpAdd = new String[1];
					tmpAdd[0] = address;
					aHelper.insert(v.getContext(), tmpAdd, "MMS of " + address,
							parts[0].Data);
					return;
				}

				if (edtMessage.getText().toString().equals("")) {
					Toast.makeText(
							v.getContext(),
							"Your message is Empty. \nPlease enter your message and try again.",
							Toast.LENGTH_SHORT).show();
					return;
				} else {
					smsItem.address = address;
					smsItem.body = edtMessage.getText().toString();
					smsItem.id = 1;
					smsItem.readStatus = 1;
					smsItem.type = 2;
					smsItem.date = System.currentTimeMillis();

					SMSSender sendSMS = new SMSSender();
					sendSMS.sendSMSMessage(v.getContext(), smsItem);

					// loadListContact(v.getContext());

					listSMS.add(smsItem);
					smsAdapter.notifyDataSetChanged();
					listView.setSelection(smsAdapter.getCount());
					edtMessage.setText("");
					imgAttach.setImageBitmap(null);

				}

			}
		});

		btnAttach.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				
//				ListSMSApplication lapplication = (ListSMSApplication) getApplication();
//				lapplication.setAddress(address);
//				lapplication.setName(name);
//				lapplication.setThumnail(thumnail);
//				lapplication.setListSMSItem(listSMS);

				Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
				photoPickerIntent.setType("image/*");
				startActivityForResult(photoPickerIntent, SELECT_PHOTO);
			}
		});
	}

	private void addMessage2List(final Context context, final String strAdd2,
			final String strBody2, final byte[] bitmap, final long strTime) {
		// TODO Auto-generated method stub

		final SmsFetcher sf = new SmsFetcher(strAdd2);
		progressDialog = ProgressDialog.show(this, "Load Message Data",
				" Loading ...", true, false);
		DisplayMetrics displaymetrics = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
		int height = displaymetrics.heightPixels;
		int width = displaymetrics.widthPixels;
		
		smsAdapter = new SmsAdapter(ListSMSActivity.this, listSMS, width, height);
		setListAdapter(smsAdapter);

		// listSMS = sf.addItem2List(context, strBody2, bitmap, strTime);

		// It is temporary for this funcition.
		// In the future the funtion will insert item to list but not reload all
		// element

		new Thread() {
			public void run() {
				isGetSMS = true;
				listSMS.addAll(sf.addItem2List(context, strBody2, bitmap,
						strTime));
				mhandler.sendEmptyMessage(Constants.MSG_GET_SMS_ITEMS);
			}
		}.start();
		new Thread() {
			public void run() {
				isGetMMS = true;
				listSMS.addAll(SmsFetcher.getMMS(context, strAdd2));
				mhandler.sendEmptyMessage(Constants.MSG_GET_MMS_ITEMS);
			}
		}.start();

	}

	private void loadListContact(final Context context) {
		// TODO Auto-generated method stub
		final String add = this.address;
		progressDialog = ProgressDialog.show(this, "Load Message Data",
				" Loading ...", true, false);

		DisplayMetrics displaymetrics = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
		int height = displaymetrics.heightPixels;
		int width = displaymetrics.widthPixels;
		final SmsFetcher sf = new SmsFetcher(add);
		
		smsAdapter = new SmsAdapter(ListSMSActivity.this, listSMS, width, height);
		setListAdapter(smsAdapter);

		new Thread() {
			public void run() {
				isGetSMS = true;
				listSMS.addAll(sf.getListSMS(context));
				mhandler.sendEmptyMessage(Constants.MSG_GET_SMS_ITEMS);
			}
		}.start();

		new Thread() {
			public void run() {
				isGetMMS = true;
				listSMS.addAll(SmsFetcher.getMMS(context, add));
				mhandler.sendEmptyMessage(Constants.MSG_GET_MMS_ITEMS);
			}
		}.start();

		// LoadMMSAsynsTask loadMMSAsynsTask = new LoadMMSAsynsTask(this);
		// loadMMSAsynsTask.execute("+841252840600");

	}

	public void notifiyActivityTaskCompleted(ArrayList<SmsItem> result) {
		listSMS.addAll(result);
	}

	public void setStackRunning() {
		stackRunning = false;
	}

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
					imgAttach.setImageBitmap(yourSelectedImage);
					int res = 100;

					ByteArrayOutputStream stream = new ByteArrayOutputStream();

					String mimeType = GetMimeType(this, selectedImage);

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
							this,
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

	public static String GetMimeType(Context context, Uri uriImage) {
		String strMimeType = null;

		Cursor cursor = context.getContentResolver().query(uriImage,
				new String[] { MediaStore.MediaColumns.MIME_TYPE }, null, null,
				null);

		if (cursor != null && cursor.moveToNext()) {
			strMimeType = cursor.getString(0);
		}

		return strMimeType;
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
		if (isNotify.equals("1")) {
			Intent intent = new Intent(Intent.ACTION_MAIN);
			intent.putExtra("EXIT", "true");
			intent.addCategory(Intent.CATEGORY_HOME);
			intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
			startActivity(intent);
			super.onDestroy();
		}
	}

	private Runnable mUpdateTimeTask = new Runnable() {
		public void run() { // Todo

			// This line is necessary for the next call
			// delayhandler.postDelayed(this, 200);
			btnSend.setImageResource(R.drawable.send_icon);

		}
	};

	public static String formatToStandardNumber(String temp) {
		String phone = "";
		if (temp.substring(0, 3).equals("+84")) {
			switch (temp.length()) {
			case 13:
				phone = "0" + temp.substring(3);
				break;
			case 12:
				phone = "0" + temp.substring(3);
				break;
			case 16:
				if (temp.subSequence(6, 7).equals(" ")) {
					phone = "0" + temp.substring(4, 6) + temp.substring(7, 10)
							+ temp.substring(11, 13) + temp.substring(14);
				} else {
					phone = "0" + temp.substring(4, 7) + temp.substring(8, 11)
							+ temp.substring(12);
				}
				break;
			}
			;
		}
		if (temp.substring(0, 2).equals("84")) {
			switch (temp.length()) {
			case 12:
				phone = "0" + temp.substring(2);
				break;
			case 11:
				phone = "0" + temp.substring(2);
				break;
			}
			;
		}

		if (temp.length() == 13 && !temp.substring(0, 3).equals("+84")) {
			if (temp.substring(4, 5).equals(" ")) {
				phone = temp.substring(0, 4) + temp.substring(5, 8)
						+ temp.substring(9);
			} else {
				phone = temp.substring(0, 3) + temp.substring(4, 7)
						+ temp.substring(8, 10) + temp.substring(11);

			}
		}

		if (phone == null || "".equals(phone)) {
			phone = temp;
		}

		return phone;
	}

	public class SMSTimeComparator implements Comparator<SmsItem> {
		@Override
		public int compare(SmsItem arg1, SmsItem arg0) {
			// TODO Auto-generated method stub
			return (arg0.date > arg1.date) ? -1 : (arg0.date == arg1.date) ? 0
					: 1;
		}
	}

	private final Handler mhandler = new Handler() {
		@Override
		public void handleMessage(final Message msg) {
			switch (msg.what) {
			case Constants.MSG_GET_SMS_ITEMS:
				isGetSMS = false;
				if (listSMS == null || listSMS.size() == 0) {
					empty.setText("No Data");
				} else {
					if (!isGetMMS) {
						progressDialog.dismiss();
						Collections.sort(listSMS, new SMSTimeComparator());
						smsAdapter.notifyDataSetChanged();
						// set select in bottom o flistview
						listView.setSelection(smsAdapter.getCount());
					}
				}
				break;
			case Constants.MSG_GET_MMS_ITEMS:
				isGetMMS = false;
				if (listSMS == null || listSMS.size() == 0) {
					empty.setText("No Data");
				} else {
					if (!isGetSMS) {
						progressDialog.dismiss();
						Collections.sort(listSMS, new SMSTimeComparator());
						smsAdapter.notifyDataSetChanged();
						// set select in bottom o flistview
						listView.setSelection(smsAdapter.getCount());
					}
				}
				break;
			}
		}
	};

}
