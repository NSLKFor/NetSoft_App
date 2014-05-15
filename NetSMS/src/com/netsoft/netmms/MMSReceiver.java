package com.netsoft.netmms;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SqliteWrapper;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Environment;
import android.provider.Telephony.Mms;
import android.provider.Telephony.Mms.Inbox;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import com.android.mms.MmsConfig;
import com.android.mms.transaction.HttpUtils;
import com.android.mms.transaction.Transaction;
import com.android.mms.transaction.TransactionBundle;
import com.android.mms.transaction.TransactionService;
import com.android.mms.util.SendingProgressTokenManager;
import com.google.android.mms.MmsException;
import com.google.android.mms.pdu.GenericPdu;
import com.google.android.mms.pdu.NotificationInd;
import com.google.android.mms.pdu.PduBody;
import com.google.android.mms.pdu.PduHeaders;
import com.google.android.mms.pdu.PduParser;
import com.google.android.mms.pdu.PduPersister;
import com.google.android.mms.pdu.DeliveryInd;
import com.google.android.mms.pdu.ReadOrigInd;
import com.google.android.mms.pdu.RetrieveConf;
import com.netsoft.netmms.*;
import com.netsoft.netsms.ListContactFetcher;
import com.netsoft.netsms.NotifySMS;
import com.netsoft.netsms.SmsFetcher;

public class MMSReceiver extends BroadcastReceiver {

	private static final boolean LOCAL_LOGV = false;
	private static final String TAG = "SMS RECEIVE";

	@Override
	public void onReceive(final Context context, Intent intent) {
		// TODO Auto-generated method stub

		byte[] pduData = intent.getByteArrayExtra("pdu");

		byte[] pushData = intent.getByteArrayExtra("data");
		PduParser parser = new PduParser(pushData);

		GenericPdu pdu = parser.parse();

		if (null == pdu) {
			Log.e("MMS", "Invalid PUSH data");
			return;
		}

		long threadId;

		PduPersister p = PduPersister.getPduPersister(context);
		ContentResolver cr = context.getContentResolver();
		int type = pdu.getMessageType();
		try {
			switch (type) {
			case PduHeaders.MESSAGE_TYPE_DELIVERY_IND:
			case PduHeaders.MESSAGE_TYPE_READ_ORIG_IND: {
				threadId = findThreadId(context, pdu, type);
				if (threadId == -1) {
					// The associated SendReq isn't found, therefore skip
					// processing this PDU.
					break;
				}

				Uri uri = p.persist(pdu, Inbox.CONTENT_URI);
				// Update thread ID for ReadOrigInd & DeliveryInd.
				ContentValues values = new ContentValues(1);
				values.put(Mms.THREAD_ID, threadId);
				SqliteWrapper.update(context, cr, uri, values, null, null);
				break;
			}
			case PduHeaders.MESSAGE_TYPE_NOTIFICATION_IND: {

				NotificationInd nInd = (NotificationInd) pdu;

				if (MmsConfig.getTransIdEnabled()) {
					byte[] contentLocation = nInd.getContentLocation();
					if ('=' == contentLocation[contentLocation.length - 1]) {
						byte[] transactionId = nInd.getTransactionId();
						byte[] contentLocationWithId = new byte[contentLocation.length
								+ transactionId.length];
						System.arraycopy(contentLocation, 0,
								contentLocationWithId, 0,
								contentLocation.length);
						System.arraycopy(transactionId, 0,
								contentLocationWithId, contentLocation.length,
								transactionId.length);
						nInd.setContentLocation(contentLocationWithId);
					}
				}

				if (!isDuplicateNotification(context, nInd)) {
					// Save the pdu. If we can start downloading the real pdu
					// immediately,
					// don't allow persist() to create a thread for the
					// notificationInd
					// because it causes UI jank.
					// Uri uri = p.persist(pdu, Inbox.CONTENT_URI);
					//
					// // Start service to finish the notification transaction.
					// Intent svc = new Intent(context,
					// TransactionService.class);
					// svc.putExtra(TransactionBundle.URI, uri.toString());
					// svc.putExtra(TransactionBundle.TRANSACTION_TYPE,
					// Transaction.NOTIFICATION_TRANSACTION);
					// context.startService(svc);
					//

					// ///****** download mms from pdu
					List<APN> apns = new ArrayList<APN>();
					APNHelper helper = new APNHelper(context);
					apns = helper.getMMSApns();

					String locationDownload = new String(
							nInd.getContentLocation());
					try {

						boolean isProxy = !TextUtils
								.isEmpty(apns.get(0).MMSProxy);
						int port = Integer.parseInt(apns.get(0).MMSPort);
						String Proxy = apns.get(0).MMSProxy.toString();
						String url = apns.get(0).MMSCenterUrl.toString();

						byte[] resp = HttpUtils
								.httpConnection(context, -1L, locationDownload,
										null, HttpUtils.HTTP_GET_METHOD,
										isProxy, Proxy, port);

						RetrieveConf retrieveConf = (RetrieveConf) new PduParser(
								resp).parse();
						PduPersister persister = PduPersister
								.getPduPersister(context);
						Uri msgUri = p.persist(retrieveConf, Inbox.CONTENT_URI);

						ContentValues values = new ContentValues(1);
						values.put(Mms.DATE, System.currentTimeMillis() / 1000L);
						SqliteWrapper.update(context,
								context.getContentResolver(), msgUri, values,
								null, null);
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
						Log.e("LOg e",
								"Error form httpconnection: " + e.getMessage());
					}

					// //////***** end download mms form pdu
				} else if (LOCAL_LOGV) {
					Log.v(TAG, "Skip downloading duplicate message: "
							+ new String(nInd.getContentLocation()));
				}
				break;
			}
			default:
				Log.e(TAG, "Received unrecognized PDU.");
			}
		} catch (MmsException e) {
			Log.e(TAG, "Failed to save the data from PUSH: type=" + type, e);
		} catch (RuntimeException e) {
			Log.e(TAG, "Unexpected RuntimeException.", e);
		}

		//
		// if(type == PduHeaders.MESSAGE_TYPE_DELIVERY_IND) {
		// messageId = new String(((DeliveryInd)pdu).getMessageId());
		// //Notify app that mms with messageId was delivered
		// }
		//
		// int status = ((DeliveryInd)pdu).getStatus();
		// if(status == PduHeaders.STATUS_RETRIEVED) {
		// //message delivered. update storage
		// }

		String[] columns = null;
		String[] values = null;

		Cursor curPdu = context.getContentResolver().query(
				Uri.parse("content://mms"), null, null, null, null);
		if (curPdu.moveToNext()) {
			// String read = curRead.getString(curRead.getColumnIndex("read"));
			// Gets ID of message
			String id = curPdu.getString(curPdu.getColumnIndex("_id"));
			// Gets thread ID of message
			String thread_id = curPdu.getString(curPdu
					.getColumnIndex("thread_id"));
			// Gets subject of message (if any)
			String subject = curPdu.getString(curPdu.getColumnIndex("sub"));
			// Gets date of message
			long date = curPdu.getLong(curPdu.getColumnIndex("date")) * 1000;

			String selectionAddr = new String("msg_id = '" + id + "'");
			Uri uriAddr = Uri.parse("content://mms/" + id + "/addr");
			Cursor curAddr = context.getContentResolver().query(uriAddr, null,
					null, null, null);

			String address = SmsFetcher.getMMSAddress(context, id);
			if (curAddr.moveToNext()) {
				String contact_id = curAddr.getString(curAddr
						.getColumnIndex("contact_id"));
				Log.e("MMS REceiver", "contact_id : " + contact_id);
				// String address = curAddr.getString(curAddr
				// .getColumnIndex("address"));
				Log.e("MMS REceiver", "address : " + address);
				String selectionPart = new String("mid = '" + id + "'");
				Log.e("MMS REceiver", "selectionPart : " + selectionPart);
				Cursor curPart = context.getContentResolver().query(
						Uri.parse("content://mms/part"),
						new String[] { "_id", "ct", "_data", "text", "cl" },
						selectionPart, null, null);
				// Cursor curPart = context.getContentResolver
				// ().query(Uri.parse ("content://mms/" + id + "/part"), null,
				// null, null, null);
				Log.e("MMS REceiver", "MMSMonitor :: parts records length == "
						+ curPart.getCount());
				while (curPart.moveToNext()) {

					String contentType = curPart.getString(curPart
							.getColumnIndex("ct"));
					String partId = curPart.getString(curPart
							.getColumnIndex("_id"));
					Log.e("MMS REceiver", "MMSMonitor :: partId == " + partId);
					Log.e("MMS REceiver", "MMSMonitor :: part mime type == "
							+ contentType);

					if (isImageType(contentType) == true) {

						Log.e("", "MMSMonitor :: ==== Get the Image start ====");
						String fileName = "mms_" + partId;
						String fileType = contentType;

						byte[] imgData = readMMSPart(context, partId);
						Log.e("", "MMSMonitor :: Iimage data length == "
								+ imgData.length + "\nfileType: " + fileType);
						if(imgData.length > 0){
							Intent smsReceiveIntent = new Intent(context,
									NotifySMS.class);
							smsReceiveIntent
									.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
							smsReceiveIntent.putExtra("add", ListContactFetcher
									.ConvertNumberPhoneAddress(address));
							smsReceiveIntent.putExtra("bd", "New MMS message");
							smsReceiveIntent.putExtra("timeStamp", date);
							smsReceiveIntent.putExtra("img", imgData);
							smsReceiveIntent.putExtra("EXIT", "");

							context.startActivity(smsReceiveIntent);
						}

						
					}

					// columns = curPart.getColumnNames();
					// if(values == null)
					// values = new String[columns.length];
					//
					// for(int i=0; i< curPart.getColumnCount(); i++){
					// values[i] = curPart.getString(i);
					// Log.e("MMS REceiver", curPart.getString(i));
					// }
					// String contact_idd = curPart.getString(0);
					//
					// if(values[3].equals("image/jpeg") ||
					// values[3].equals("image/bmp") ||
					// values[3].equals("image/gif") ||
					// values[3].equals("image/jpg") ||
					// values[3].equals("image/png"))
					// {
					// GetMmsAttachment(context, values[0],values[12]);
					// //Toast.makeText(getApplicationContext(),
					// "Retrieved MMS attachment", Toast.LENGTH_LONG);
					// }

				}
				curPart.close();
				
			}
		}
		curPdu.close();

		/*
		 * 
		 * String id = ""; // this is the id of your MMS message that you are
		 * going to search for Cursor locationQuery =
		 * context.getContentResolver().query(Uri.parse("content://mms/"), new
		 * String[] {"m_size", "exp", "ct_l", "_id"}, "_id=?", new String[]{id},
		 * null); locationQuery.moveToFirst();
		 * 
		 * String exp = "1"; String size = "1";
		 * 
		 * try { size =
		 * locationQuery.getString(locationQuery.getColumnIndex("m_size")); exp
		 * = locationQuery.getString(locationQuery.getColumnIndex("exp")); }
		 * catch (Exception f) {
		 * 
		 * }
		 * 
		 * String location =
		 * locationQuery.getString(locationQuery.getColumnIndex("ct_l"));
		 * 
		 * List<APN> apns = new ArrayList<APN>(); SharedPreferences sharedPrefs
		 * = context.getSharedPreferences("netsoft_preferences", 0);
		 * 
		 * try { APNHelper helper = new APNHelper(context); apns =
		 * helper.getMMSApns();
		 * 
		 * } catch (Exception e) { APN apn = new
		 * APN(sharedPrefs.getString("mmsc_url", ""),
		 * sharedPrefs.getString("mms_port", ""),
		 * sharedPrefs.getString("mms_proxy", "")); apns.add(apn); }
		 * 
		 * 
		 * try { byte[] resp = HttpUtils.httpConnection( context,
		 * SendingProgressTokenManager.NO_TOKEN, location, null,
		 * HttpUtils.HTTP_GET_METHOD, !TextUtils.isEmpty(apns.get(0).MMSProxy),
		 * apns.get(0).MMSProxy, Integer.parseInt(apns.get(0).MMSPort));
		 * 
		 * RetrieveConf retrieveConf = (RetrieveConf) new
		 * PduParser(resp).parse(); PduPersister persister =
		 * PduPersister.getPduPersister(context); Uri msgUri =
		 * persister.persist(retrieveConf, Inbox.CONTENT_URI);
		 * 
		 * ContentValues values1 = new ContentValues(1); values1.put(Mms.DATE,
		 * System.currentTimeMillis() / 1000L); SqliteWrapper.update(context,
		 * context.getContentResolver(), msgUri, values1, null, null);
		 * 
		 * Toast.makeText(context, "Message Received",
		 * Toast.LENGTH_SHORT).show(); // SqliteWrapper.delete(context,
		 * context.getContentResolver(), // Uri.parse("content://mms/"),
		 * "thread_id=? and _id=?", new String[] {threadIds, msgId});
		 * 
		 * // ((Activity)
		 * context).getWindow().getDecorView().findViewById(android
		 * .R.id.content).post(new Runnable() { // // @Override // public void
		 * run() { // Toast.makeText(context, "Message Received",
		 * Toast.LENGTH_SHORT).show(); // } // }); } catch (Exception e) {
		 * e.printStackTrace();
		 * 
		 * // ((Activity)
		 * context).getWindow().getDecorView().findViewById(android
		 * .R.id.content).post(new Runnable() { // // @Override // public void
		 * run() { // Toast.makeText(context, "Download Failed",
		 * Toast.LENGTH_SHORT).show(); // } // }); }
		 */

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

	public static void GetMmsAttachment(Context context, String _id,
			String _data) {
		Uri partURI = Uri.parse("content://mms/part/" + _id);
		String filePath = "/sdcard/photo.jpg";
		InputStream is = null;
		OutputStream picFile = null;
		Bitmap bitmap = null;

		try {
			is = context.getContentResolver().openInputStream(partURI);
			bitmap = BitmapFactory.decodeStream(is);

			picFile = new FileOutputStream(filePath);
			bitmap.compress(Bitmap.CompressFormat.JPEG, 100, picFile);
			picFile.flush();
			picFile.close();
		} catch (Exception e) {
			e.printStackTrace();
			// throw new MmsException(e);
		}
	}

	private static long findThreadId(Context context, GenericPdu pdu, int type) {
		String messageId;

		if (type == PduHeaders.MESSAGE_TYPE_DELIVERY_IND) {
			messageId = new String(((DeliveryInd) pdu).getMessageId());
		} else {
			messageId = new String(((ReadOrigInd) pdu).getMessageId());
		}

		StringBuilder sb = new StringBuilder('(');
		sb.append(Mms.MESSAGE_ID);
		sb.append('=');
		sb.append(DatabaseUtils.sqlEscapeString(messageId));
		sb.append(" AND ");
		sb.append(Mms.MESSAGE_TYPE);
		sb.append('=');
		sb.append(PduHeaders.MESSAGE_TYPE_SEND_REQ);
		// TODO ContentResolver.query() appends closing ')' to the selection
		// argument
		// sb.append(')');

		Cursor cursor = SqliteWrapper.query(context,
				context.getContentResolver(), Mms.CONTENT_URI,
				new String[] { Mms.THREAD_ID }, sb.toString(), null, null);
		if (cursor != null) {
			try {
				if ((cursor.getCount() == 1) && cursor.moveToFirst()) {
					return cursor.getLong(0);
				}
			} finally {
				cursor.close();
			}
		}

		return -1;
	}

	private static boolean isDuplicateNotification(Context context,
			NotificationInd nInd) {
		byte[] rawLocation = nInd.getContentLocation();
		if (rawLocation != null) {
			String location = new String(rawLocation);
			String selection = Mms.CONTENT_LOCATION + " = ?";
			String[] selectionArgs = new String[] { location };
			Cursor cursor = SqliteWrapper.query(context,
					context.getContentResolver(), Mms.CONTENT_URI,
					new String[] { Mms._ID }, selection, selectionArgs, null);
			if (cursor != null) {
				try {
					if (cursor.getCount() > 0) {
						// We already received the same notification before.
						return true;
					}
				} finally {
					cursor.close();
				}
			}
		}
		return false;
	}

}
