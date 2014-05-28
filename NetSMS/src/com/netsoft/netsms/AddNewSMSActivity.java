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
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
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
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class AddNewSMSActivity extends Activity {

	ImageButton btnSendNew;
	ImageButton btnAttach;
	ImageView attNew;
	EditText addNew;
	EditText sendBodyNew;

	Handler delayhandler = new Handler();

	SmsItem smsItem = new SmsItem();
	private final int SELECT_PHOTO = 100;
	private MMSPart[] parts;

	Bundle bundle;
	String add = "";
	String bd = "";
	String timeStamp = "";
	byte[] imgtemp = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_add_new_sms);

		addNew = (EditText) findViewById(R.id.addAddress);
		sendBodyNew = (EditText) findViewById(R.id.addEnterBox);
		attNew = (ImageView) findViewById(R.id.imgAttachNew);
		btnAttach = (ImageButton) findViewById(R.id.AttachButton);
		btnAttach.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
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

				smsItem.address = addNew.getText().toString();
				smsItem.body = sendBodyNew.getText().toString();
				smsItem.id = "0";
				smsItem.readStatus = 1;
				smsItem.type = 2;
				smsItem.date = System.currentTimeMillis();

				if ((addNew.getText().toString().equals("") || sendBodyNew
						.getText().toString().equals(""))
						&& parts == null) {

					if (addNew.getText().toString().equals("")
							&& !sendBodyNew.getText().toString().equals("")) {
						Toast.makeText(
								v.getContext(),
								"Address is Empty.\nPlease enter address and try again.",
								Toast.LENGTH_SHORT).show();
					}

					if (!addNew.getText().toString().equals("")
							&& sendBodyNew.getText().toString().equals("")) {
						Toast.makeText(
								v.getContext(),
								"Your message is Empty. \nPlease enter your message and try again.",
								Toast.LENGTH_SHORT).show();
					}

					if (addNew.getText().toString().equals("")
							&& sendBodyNew.getText().toString().equals("")) {
						Toast.makeText(
								v.getContext(),
								"Address and your message and is Empty. \nPlease enter address, your message and try again.",
								Toast.LENGTH_SHORT).show();
					}
					return;
				} else {

					if (addNew.getText().toString().equals("")) {
						Toast.makeText(
								v.getContext(),
								"Address is Empty.\nPlease enter address and try again.",
								Toast.LENGTH_SHORT).show();
						return;
					}

					if (parts != null) {
						String address = addNew.getText().toString()
								.replaceAll(" ", "");
						Toast.makeText(getApplicationContext(), "Send MMS",
								Toast.LENGTH_SHORT).show();

						 APNHelper aHelper = new APNHelper(v.getContext());
						 
						 aHelper.sendMMS(address, parts);
						 String[] tmpAdd = new String[1];
						 tmpAdd[0] = address;
						 aHelper.insert(v.getContext(), tmpAdd, "MMS to "
						 + address, parts[0].Data);
						 
						 Intent intent = new Intent(v.getContext(),
									MainActivity.class);
							intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
							startActivity(intent);

//						Intent smsReceiveIntent = new Intent(
//								AddNewSMSActivity.this, ListSMSActivity.class);
//						smsReceiveIntent
//								.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//						smsReceiveIntent.putExtra("address", address);
//						smsReceiveIntent.putExtra("body", "MMS to " + address);
//						smsReceiveIntent.putExtra("time",
//								System.currentTimeMillis());
//						smsReceiveIntent.putExtra("img", parts[0].Data);
//						smsReceiveIntent.putExtra("isNotify", "0");
//						startActivity(smsReceiveIntent);
						return;
					}
					if (!sendBodyNew.getText().toString().equals("")) {
						 SMSSender sendSMS = new SMSSender();
						 sendSMS.sendSMSMessage(v.getContext(), smsItem);

						Intent intent = new Intent(v.getContext(),
								MainActivity.class);
						intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
						startActivity(intent);
					}

				}
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
					attNew.setImageBitmap(yourSelectedImage);
					int res = 100;

					ByteArrayOutputStream stream = new ByteArrayOutputStream();

					String mimeType = ListSMSActivity.GetMimeType(this,
							selectedImage);

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

}
