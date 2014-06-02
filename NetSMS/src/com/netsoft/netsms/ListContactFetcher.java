package com.netsoft.netsms;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.provider.ContactsContract;
import android.provider.ContactsContract.PhoneLookup;
import android.telephony.PhoneNumberUtils;
import android.util.Log;

public class ListContactFetcher {

	public ArrayList<ListContactItem> listContact = new ArrayList<ListContactItem>();

	public List<ListContactItem> getListContact(Context context) {

		long time1 = System.currentTimeMillis();

		// get infomation from conversations
		ContentResolver contentResolver = context.getContentResolver();
		Uri uri = Uri.parse("content://mms-sms/conversations/");
		Cursor query = contentResolver.query(uri, null, null, null, null);

		if (query.moveToNext()) {
			do {
				String string = query.getString(query.getColumnIndex("ct_t"));
				String id = query.getString(query.getColumnIndex("_id"));
				if ("application/vnd.wap.multipart.related".equals(string)
						|| "application/vnd.wap.multipart.mixed".equals(string)
						|| "application/vnd.wap.multipart.alternative"
								.equals(string)) {
					// it's MMS
					ContentResolver cRr = context.getContentResolver();
					String selection = "_id = " + "'" + id + "'";
					Uri u = Uri.parse("content://mms");
					Cursor cs = cRr.query(u, null, selection, null, null);
					cs.moveToFirst();
					do {

						// Gets ID of message
						String pid = cs.getString(cs.getColumnIndex("_id"));
						// Gets thread ID of message
						String thread_id = cs.getString(cs
								.getColumnIndex("thread_id"));
						// Gets subject of message (if any)
						String subject = cs.getString(cs.getColumnIndex("sub"));

						String selectionPart = new String("mid = '" + pid + "'");
						Log.e("MMS REceiver", "selectionPart : "
								+ selectionPart);

						Cursor curPart = context.getContentResolver().query(
								Uri.parse("content://mms/part"),
								// new String[] { "_id", "ct", "_data", "text",
								// "cl" },
								null, selectionPart, null, null);
						Log.e("MMS REceiver",
								"MMSMonitor :: parts records length == "
										+ curPart.getCount());
						if (curPart.getCount() == 0) {
							continue;
						}

						// Gets date of message
						long date = cs.getLong(cs.getColumnIndex("date")) * 1000;
						// String time = DateFormat.getInstance().format(date);
						// Log.e("Time", "\ntime to compare to sms: " + time);

						// *******************get address *******************

						// SmsFetcher smsfetcher = new SmsFetcher("");
						String temp = SmsFetcher.getMMSAddress(context, pid);
						// ********************end get address
						// ************************************

						ListContactItem listContactItem = new ListContactItem();
						String addr = ConvertNumberPhoneAddress(temp);
						listContactItem.address = addr;
						listContactItem.body = "MMS Message";
						listContactItem.time = date;
						listContactItem.name = getContactName(context, addr);

						long contact_ID = fetchContactIdFromPhoneNumber(
								context, addr);

						listContactItem.thumnail = getPhotoUri(context,
								contact_ID);
						listContactItem.readStatus = 1;

						listContact.add(listContactItem);

					} while (cs.moveToNext());
					cs.close();

				} else {
					// it's SMS
					// PhoneNumberUtils.formatNumber("0917076422",
					// PhoneNumberUtils.TOA_International);

					ContentResolver cRr = context.getContentResolver();
					String selection = "_id = " + "'" + id + "'";
					Uri u = Uri.parse("content://sms");
					Cursor cs = cRr.query(u, null, selection, null, null);
					cs.moveToFirst();
					String phone = "";
					String temp = cs.getString(cs.getColumnIndex("address"));

					// convert to standard format address
					phone = ConvertNumberPhoneAddress(temp);

					// get info of message
					int type = cs.getInt(cs.getColumnIndex("type"));// 2 = sent,
																	// etc.
					long date = cs.getLong(cs.getColumnIndex("date"));
					String body = cs.getString(cs.getColumnIndex("body"));

					Log.e("AAAAAAA", "---- id: " + id);
					Log.e("AAAAAAA", "---- address: " + phone);
					Log.e("AAAAAAA", "---- type: " + type);
					Log.e("AAAAAAA", "---- date: " + date);
					Log.e("AAAAAAA", "---- body: " + body);

					ListContactItem listContactItem = new ListContactItem();

					listContactItem.address = phone;
					listContactItem.body = body;
					listContactItem.time = date;
					listContactItem.name = getContactName(context, phone);

					long contact_ID = fetchContactIdFromPhoneNumber(context,
							phone);

					listContactItem.thumnail = getPhotoUri(context, contact_ID);
					listContactItem.readStatus = cs.getInt(cs
							.getColumnIndexOrThrow("read"));

					listContact.add(listContactItem);
				}
			} while (query.moveToNext());
			query.close();
		}

		Log.e("AAAAAAAAAAA", "------------------ Number of  contact: "
				+ listContact.size());
		long time2 = System.currentTimeMillis();
		Log.e("Time duration ", "Loadlist time duration: " + (time2 - time1)
				/ 1000);

		// sort list contact
		SortListContact();

		return listContact;
	}

	// get contact name for phoneNumber
	public static String getContactName(Context context, String phoneNumber) {
		ContentResolver cr = context.getContentResolver();
		Uri uri = Uri.withAppendedPath(PhoneLookup.CONTENT_FILTER_URI,
				Uri.encode(phoneNumber));
		Cursor cursor = cr.query(uri,
				new String[] { PhoneLookup.DISPLAY_NAME }, null, null, null);
		if (cursor == null) {
			return null;
		}
		String contactName = null;
		if (cursor.moveToNext()) {
			contactName = cursor.getString(cursor
					.getColumnIndex(PhoneLookup.DISPLAY_NAME));
		}
		if (cursor != null && !cursor.isClosed()) {
			cursor.close();
		}
		return contactName;
	}

	// get contact id for numberphone
	public static long fetchContactIdFromPhoneNumber(Context context,
			String phoneNumber) {
		Uri uri = Uri.withAppendedPath(PhoneLookup.CONTENT_FILTER_URI,
				Uri.encode(phoneNumber));
		Cursor cursor = context.getContentResolver().query(uri,
				new String[] { PhoneLookup.DISPLAY_NAME, PhoneLookup._ID },
				null, null, null);

		long contactId = 0;

		if (cursor.moveToNext()) {
			do {
				contactId = cursor.getLong(cursor
						.getColumnIndex(PhoneLookup._ID));
			} while (cursor.moveToNext());
			cursor.close();
		}

		return contactId;
	}

	// get thumnail for number phone
	public static Uri getPhotoUri(Context context, long contactId) {
		ContentResolver contentResolver = context.getContentResolver();

		try {
			Cursor cursor = contentResolver
					.query(ContactsContract.Data.CONTENT_URI,
							null,
							ContactsContract.Data.CONTACT_ID
									+ "="
									+ contactId
									+ " AND "

									+ ContactsContract.Data.MIMETYPE
									+ "='"
									+ ContactsContract.CommonDataKinds.Photo.CONTENT_ITEM_TYPE
									+ "'", null, null);

			if (cursor != null) {
				if (!cursor.moveToNext()) {
					return null; // no photo
				}
			} else {
				return null; // error in cursor process
			}

		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}

		Uri person = ContentUris.withAppendedId(
				ContactsContract.Contacts.CONTENT_URI, contactId);
		return Uri.withAppendedPath(person,
				ContactsContract.Contacts.Photo.CONTENT_DIRECTORY);
	}

	// convert number phone to standard
	public static String ConvertNumberPhoneAddress(String temp) {

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

	// sort the do not read sms to the top of list
	protected void SortListContact() {
		int pivot = 0;

		long time1 = System.currentTimeMillis();
		for (int i = 1; i < listContact.size(); i++) {
			Log.e("Sort list", "\n--- " + listContact.get(i).address
					+ " ---- readstatus: " + listContact.get(i).readStatus);
			if (listContact.get(i).readStatus == 0) {
				ListContactItem temp = listContact.get(i);
				listContact.set(i, listContact.get(pivot));
				listContact.set(pivot, temp);
				pivot++;
			}
		}

		for (int i = pivot; i < listContact.size() - 1; i++) {
			for (int j = i + 1; j < listContact.size(); j++) {
				if (listContact.get(i).time < listContact.get(j).time) {
					ListContactItem temp = listContact.get(i);
					listContact.set(i, listContact.get(j));
					listContact.set(j, temp);
				}
			}
		}
		long time2 = System.currentTimeMillis();
		Log.e("Time duration ", "Sortlist Time duration: " + (time2 - time1)
				/ 1000);
	}

}
