package com.netsoft.netsms;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.DateFormat;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import android.R.integer;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Environment;
import android.provider.ContactsContract.PhoneLookup;
import android.util.Log;

public class SmsFetcher {
	private String address;

	public SmsFetcher(String address) {
		this.address = address;
	}

	public List<SmsItem> getListSMS(Context context) {

		Uri message = Uri.parse("content://sms");
		ContentResolver cr = context.getContentResolver();
		String selection = "";
		List<String> listNumberFormat = new ArrayList<String>();
		listNumberFormat = formatNumberPhone(this.address);
		for (int i = 0; i < listNumberFormat.size() - 1; i++) {
			selection = selection + "address = '" + listNumberFormat.get(i)
					+ "' or ";
		}
		selection = selection + "address = '"
				+ listNumberFormat.get(listNumberFormat.size() - 1) + "'";

		Cursor cursor = cr.query(message, null, selection, null, null);
		// Cursor cursor = cr.query(message, null, "address = '"+ this.address+
		// "' or "+ "address = '"+ convertAddress(this.address)+ "'", null,
		// null);
		// Cursor cursor = cr.query(message, null,null, null, null);

		List<SmsItem> listMessage = new ArrayList<SmsItem>();
		if (cursor.moveToNext()) {
			do {
				// String temp = cursor.getString(cursor
				// .getColumnIndexOrThrow("address"));
				// String ttt =temp.replace(" ", "");
				// temp = ttt;
				// if (temp.equals(this.address)
				// || temp.equals(convertAddress(this.address))) {

				SmsItem item = new SmsItem();
				item.address = this.address;
				item.id = Integer.parseInt(cursor.getString(
						cursor.getColumnIndexOrThrow("_id")).toString());
				item.body = cursor.getString(
						cursor.getColumnIndexOrThrow("body")).toString();
				item.readStatus = Integer.parseInt(cursor.getString(
						cursor.getColumnIndexOrThrow("read")).toString());
				item.type = Integer.parseInt(cursor.getString(
						cursor.getColumnIndexOrThrow("type")).toString());
				item.date = cursor
						.getLong(cursor.getColumnIndexOrThrow("date"));

				listMessage.add(item);
				if (item.readStatus == 0) {
					ContentValues values = new ContentValues();
					values.put("read", true);
					context.getContentResolver().update(
							Uri.parse("content://sms/inbox"),
							values,
							"_id="
									+ cursor.getString(cursor
											.getColumnIndexOrThrow("_id")),
							null);
				}
				// }
			} while (cursor.moveToNext());
			cursor.close();
		}

		listMessage = SortListSMS(listMessage);

		// listMessage.addAll(getMMS(context, "+841252840600"));

		return listMessage;
	}

	private void getSMS(Context context, String id) {
		// TODO Auto-generated method stub
		ContentResolver contentResolver = context.getContentResolver();
		String selection = "_id = " + id;
		Uri uri = Uri.parse("content://sms");
		Cursor cursor = contentResolver.query(uri, null, selection, null, null);
		if (cursor.moveToNext()) {
			do {
				String phone = cursor.getString(cursor
						.getColumnIndex("address"));
				int type = cursor.getInt(cursor.getColumnIndex("type"));// 2 =
																		// sent,
																		// etc.
				long date = cursor.getLong(cursor.getColumnIndex("date"));
				String body = cursor.getString(cursor.getColumnIndex("body"));
			} while (cursor.moveToNext());
			cursor.close();
		}

	

	}

	public List<SmsItem> addItem2List(Context context, String strBody2,
			byte[] bitmap, long lTime) {
		// TODO Auto-generated method stub
		Uri message = Uri.parse("content://sms");
		ContentResolver cr = context.getContentResolver();
		String selection = "";
		List<String> listNumberFormat = new ArrayList<String>();
		listNumberFormat = formatNumberPhone(this.address);
		for (int i = 0; i < listNumberFormat.size() - 1; i++) {
			selection = selection + "address = '" + listNumberFormat.get(i)
					+ "' or ";
		}
		selection = selection + "address = '"
				+ listNumberFormat.get(listNumberFormat.size() - 1) + "'";
		Cursor cursor = cr.query(message, null, selection, null, null);

		List<SmsItem> listMessage = new ArrayList<SmsItem>();
		if (cursor.moveToNext()) {
			do {
				String temp = cursor.getString(cursor
						.getColumnIndexOrThrow("address"));

				if (temp.equals(this.address)
						|| temp.equals(convertAddress(this.address))) {
					SmsItem item = new SmsItem();
					item.address = this.address;
					item.id = Integer.parseInt(cursor.getString(
							cursor.getColumnIndexOrThrow("_id")).toString());
					item.body = cursor.getString(
							cursor.getColumnIndexOrThrow("body")).toString();
					item.readStatus = Integer.parseInt(cursor.getString(
							cursor.getColumnIndexOrThrow("read")).toString());
					item.type = Integer.parseInt(cursor.getString(
							cursor.getColumnIndexOrThrow("type")).toString());
					item.date = cursor.getLong(cursor
							.getColumnIndexOrThrow("date"));
					item.imgMMS = null;

					// item.body = Integer.toString((int)
					// System.currentTimeMillis());

					listMessage.add(item);
				}
			} while (cursor.moveToNext());
			cursor.close();
		}

		SmsItem item = new SmsItem();
		item.address = this.address;
		item.body = strBody2;

		item.id = (Integer) 0; // Integer.parseInt(cursor.getString(cursor.getColumnIndexOrThrow("_id")).toString())
								// ;
		item.readStatus = (Integer) 0; // Integer.parseInt(cursor.getString(cursor.getColumnIndexOrThrow("read")).toString());
		item.type = (Integer) 0; // Integer.parseInt(cursor.getString(cursor.getColumnIndexOrThrow("type")).toString());
		item.date = lTime; // cursor.getLong(cursor.getColumnIndexOrThrow("date"))
							// /
							// 1000;

		item.imgMMS = bitmap;

		if (bitmap != null) {
			Log.e("akjsfhkjasf", "bitmap not null--" + bitmap.length);
		} else {
			Log.e("akjsfhkjasf", "bitmap null");
		}

		listMessage.add(item);

		listMessage = SortListSMS(listMessage);
		return listMessage;
	}

	public List<SmsItem> SortListSMS(List<SmsItem> list) {

		int lenght = list.size();
		for (int i = 0; i < lenght / 2; i++) {
			SmsItem temp = list.get(i);
			list.set(i, list.get(lenght - i - 1));
			list.set(lenght - i - 1, temp);
		}
		return list;
	}

	public String convertAddress(String add) {
		String temp = "";
		switch (add.length()) {
		case 10:
			temp = "+84" + add.substring(1);
			break;
		case 11:
			temp = "+84" + add.substring(1);
			break;
		}
		;

		return temp;
	}

	public static List<String> formatNumberPhone(String adds) {
		List<String> fmNumber = new ArrayList<String>();
		String temp = "";
		fmNumber.add(adds);
		switch (adds.length()) {
		case 10:
			temp = adds.substring(0, 3) + " " + adds.substring(3, 6) + " "
					+ adds.substring(6, 8) + " " + adds.substring(8);
			fmNumber.add(temp);
			temp = "";

			temp = "+84" + adds.substring(1);
			fmNumber.add(temp);
			temp = "";

			temp = "+84 " + adds.substring(1, 3) + " " + adds.substring(3, 6)
					+ " " + adds.substring(6, 8) + " " + adds.substring(8);
			fmNumber.add(temp);
			break;
		case 11:
			temp = "";
			temp = adds.substring(0, 4) + " " + adds.substring(4, 7) + " "
					+ adds.substring(7);
			fmNumber.add(temp);
			temp = "";

			temp = "+84" + adds.substring(1);
			fmNumber.add(temp);
			temp = "";

			temp = "+84 " + adds.substring(1, 4) + " " + adds.substring(4, 7)
					+ " " + adds.substring(7);
			fmNumber.add(temp);
			break;
		}

		return fmNumber;
	}

	public static ArrayList<SmsItem> getMMS(Context context, String address) {

		long time1 = System.currentTimeMillis();
		ArrayList<SmsItem> listMMS = new ArrayList<SmsItem>();
		Cursor curPdu = context.getContentResolver().query(
				Uri.parse("content://mms"), null, null, null, null);

		 if (curPdu.moveToNext()) {
		do {
			//Get type of mms
			String id = curPdu.getString(curPdu.getColumnIndex("_id"));
			// Gets ID of message
			int mtype = curPdu.getInt(curPdu.getColumnIndex("msg_box"));
			//Get read Status
			int readStatus = curPdu.getInt(curPdu.getColumnIndex("read"));	

			// Gets thread ID of message
			String thread_id = curPdu.getString(curPdu
					.getColumnIndex("thread_id"));
			// Gets subject of message (if any)
			String subject = curPdu.getString(curPdu.getColumnIndex("sub"));

			String selectionPart = new String("mid = '" + id + "'");
			Log.e("MMS REceiver", "selectionPart : " + selectionPart);

			Cursor curPart = context.getContentResolver().query(
					Uri.parse("content://mms/part"),
					// new String[] { "_id", "ct", "_data", "text", "cl" },
					null, selectionPart, null, null);
			Log.e("MMS REceiver", "MMSMonitor :: parts records length == "
					+ curPart.getCount());
			if (curPart.getCount() == 0) {
				continue;
			}

			// Gets date of message
			long date = curPdu.getLong(curPdu.getColumnIndex("date")) * 1000;
			String time = DateFormat.getInstance().format(date);
			Log.e("Time", "\ntime to compare to sms: " + time);

			// *******************get address *******************

			String addr = getMMSAddress(context, id);
			// ********************end get address
			// ************************************

			// check address in mms with address of item
			List<String> listAddress = new ArrayList<String>();
			listAddress = formatNumberPhone(address);
			boolean bEqual = false;
			for (int i = 0; i < listAddress.size(); i++) {
				if (addr.equals(listAddress.get(i))) {
					bEqual = true;
				}
			}

			if (!bEqual) {
				continue;
			}

			SmsItem item = new SmsItem();
			item.address = address;
			item.date = date;
			item.id = Integer.parseInt(id);
			item.readStatus = readStatus;
			item.type = mtype;
			item.body = "";
			//item.body = "message " + id;

			curPart.moveToFirst();
			do {

				// String addr =
				// curPart.getString(curPart.getColumnIndex("address"));
				// Log.e("-----------------", "--------- Address mms" + addr);

				String contentType = curPart.getString(curPart
						.getColumnIndex("ct"));
				String partId = curPart
						.getString(curPart.getColumnIndex("_id"));
				Log.e("MMS REceiver", "MMSMonitor :: partId == " + partId);
				Log.e("MMS REceiver", "MMSMonitor :: part mime type == "
						+ contentType);

				if (contentType.equals("text/plain")) {

					String data = curPart.getString(curPart
							.getColumnIndex("_data"));
					String body;
					if (data != null) {
						// implementation of this method below
						body = getMmsText(context, partId);
					} else {
						body = curPart
								.getString(curPart.getColumnIndex("text"));
					}

					item.body = body;
					Log.e("A", "--------String  :: string == "
							+ body);
				}

				if (isImageType(contentType) == true) {

					Log.e("", "MMSMonitor :: ==== Get the Image start ====");
					String fileName = "mms_" + partId;
					String fileType = contentType;

					byte[] imgData = readMMSPart(context, partId);
					Log.e("", "MMSMonitor :: Iimage data length == "
							+ imgData.length + "\nfileType: " + fileType);

					item.imgMMS = imgData;

					// File sdcard = Environment.getExternalStorageDirectory();
					// File editedFile = new File(sdcard, "AA" + id + ".jpeg");
					//
					// // if file is already exists then first delete it
					// if (editedFile.exists()) {
					// // editedFile.delete();
					// } else {
					//
					// FileOutputStream fOut;
					// try {
					// fOut = new FileOutputStream(editedFile);
					// bmp.compress(Bitmap.CompressFormat.JPEG, 90, fOut);
					// } catch (FileNotFoundException e) {
					// // TODO Auto-generated catch block
					// e.printStackTrace();
					// }
					// }

				}

			} while (curPart.moveToNext());
			curPart.close();
			listMMS.add(item);
		} while (curPdu.moveToNext());
		curPdu.close();
		
	}
		// while in herw

		long time2 = System.currentTimeMillis();

		Log.e("", "Duration time to load mms is: " + (float) (time2 - time1)
				/ 1000);

		return listMMS;

	}

	public static boolean isImageType(String mime) {
		boolean result = false;
		if (mime.equalsIgnoreCase("image/jpg")
				|| mime.equalsIgnoreCase("image/jpeg")
				|| mime.equalsIgnoreCase("image/png")
				|| mime.equalsIgnoreCase("image/gif")
				|| mime.equalsIgnoreCase("image/bmp")) {
			result = true;
		}
		return result;
	}

	public static byte[] readMMSPart(Context context, String partId) {
		byte[] partData = null;
		Uri partURI = Uri.parse("content://mms/part/" + partId);
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		InputStream is = null;

		try {

			Log.e("", "MMSMonitor :: Entered into readMMSPart try..");
			ContentResolver mContentResolver = context.getContentResolver();
			is = mContentResolver.openInputStream(partURI);

			byte[] buffer = new byte[256];
			int len = is.read(buffer);
			while (len >= 0) {
				baos.write(buffer, 0, len);
				len = is.read(buffer);
			}
			partData = baos.toByteArray();
			// Log.i("", "Text Msg  :: " + new String(partData));

		} catch (IOException e) {
			Log.e("", "MMSMonitor :: Exception == Failed to load part data");
		} finally {
			if (is != null) {
				try {
					is.close();
				} catch (IOException e) {
					Log.e("", "Exception :: Failed to close stream");
				}
			}
		}
		return partData;
	}

	private static String getMmsText(Context context, String id) {
		Uri partURI = Uri.parse("content://mms/part/" + id);
		InputStream is = null;
		StringBuilder sb = new StringBuilder();
		try {
			is = context.getContentResolver().openInputStream(partURI);
			if (is != null) {
				InputStreamReader isr = new InputStreamReader(is, "UTF-8");
				BufferedReader reader = new BufferedReader(isr);
				String temp = reader.readLine();
				while (temp != null) {
					sb.append(temp);
					temp = reader.readLine();
				}
			}
		} catch (IOException e) {
		} finally {
			if (is != null) {
				try {
					is.close();
				} catch (IOException e) {
				}
			}
		}
		return sb.toString();
	}

	public static String getMMSAddress(Context context, String id) {
		String receiver = "";
		String sender = "";
		final String[] projection = new String[] { "address", "contact_id",
				"charset", "type" };
		final String selection = "type=137 or type=151"; // PduHeaders
		Uri.Builder builder = Uri.parse("content://mms").buildUpon();
		builder.appendPath(String.valueOf(id)).appendPath("addr");

		Cursor cursor = context.getContentResolver().query(builder.build(),
				projection, selection, null, null);

		if (cursor.moveToFirst()) {
			do {
				String add = cursor.getString(cursor.getColumnIndex("address"));
				String type = cursor.getString(cursor.getColumnIndex("type"));
				Log.e("-----------------", "\n\n--------- Address mms: " + add);
				Log.e("-----------------", "\n\n--------- Type mms: " + type);

				switch (Integer.parseInt(type)) {
				case 151:
					receiver = add;
					break;
				case 137:
					if (!add.equals("insert-address-token")) {
						sender = add;
					}
					break;
				}
				;

			} while (cursor.moveToNext());
			cursor.close();
		}

		Log.e("-----------------", "\n\n--------- Receive: " + receiver);

		if (!sender.equals("")) {
			Log.e("-----------------", "\n\n--------- Type mms: "
					+ "Message inbound");
		} else {
			Log.e("-----------------", "\n\n--------- Type mms: "
					+ "Message outbound");
		}
		// return address.replaceAll("[^0-9]", "");
		return receiver;
	}

}
