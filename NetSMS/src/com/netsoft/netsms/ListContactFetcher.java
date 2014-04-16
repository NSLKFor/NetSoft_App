package com.netsoft.netsms;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.ContactsContract;
import android.provider.ContactsContract.PhoneLookup;

public class ListContactFetcher {
	
	public List<ListContactItem>getListContact(Context context, Cursor cursor){	
		ArrayList<ListContactItem> listContact = new ArrayList<ListContactItem>();
		cursor.moveToFirst();
		do{ 
			String temp = cursor.getString(cursor.getColumnIndexOrThrow("address")).toString();
			String adds = temp;
			
			if(temp.substring(0, 3).equals("+84")){
			 switch(temp.length()){
			   case 13:
				   adds = "0" + temp.substring(3);
				   break;
			   case 12:
				   adds = "0" + temp.substring(3);
				   break;
			   };
			}
			
			int status = 0;
			   for(int i = 0; i < listContact.size(); i++){

				   if(listContact.get(i).address.equals(adds) && cursor.getInt(cursor.getColumnIndexOrThrow("read")) == 0){
					   status = 0;
					   break;
				   }
				   
				   if(listContact.get(i).address.equals(adds)){
					   status = 1;
					   break;
				   }
			   }

			   //check if draft sms "no address but have body"
			   if(cursor.getString(cursor.getColumnIndexOrThrow("address")) == null || 
					   cursor.getString(cursor.getColumnIndexOrThrow("address")).toString().equals("")){
				   continue;
			   }
				if(status == 0){
					ListContactItem listContactItem = new ListContactItem();
					
					listContactItem.address = adds;
					listContactItem.body = cursor.getString(cursor.getColumnIndexOrThrow("body")).toString();
					listContactItem.time =   cursor.getLong(cursor.getColumnIndexOrThrow("date"));
					listContactItem.name = getContactName(context, listContactItem.address);
					
//					listContactItem.body = DateFormat.getInstance().format(cursor.getLong(cursor.getColumnIndexOrThrow("date")));
					
					long contact_ID = fetchContactIdFromPhoneNumber(context, listContactItem.address);

					listContactItem.thumnail = getPhotoUri(context, contact_ID);
					listContactItem.readStatus = cursor.getInt(cursor.getColumnIndexOrThrow("read"));
					
					listContact.add(listContactItem);
				}
		}while(cursor.moveToNext());
		
		return listContact;
	}

	  public String getContactName(Context context, String phoneNumber) {
	        ContentResolver cr = context.getContentResolver();
	        Uri uri = Uri.withAppendedPath(PhoneLookup.CONTENT_FILTER_URI,
	                Uri.encode(phoneNumber));
	        Cursor cursor = cr.query(uri,
	                new String[] { PhoneLookup.DISPLAY_NAME }, null, null, null);
	        if (cursor == null) {
	            return null;
	        }
	        String contactName = null;
	        if (cursor.moveToFirst()) {
	            contactName = cursor.getString(cursor
	                    .getColumnIndex(PhoneLookup.DISPLAY_NAME));
	        }
	        if (cursor != null && !cursor.isClosed()) {
	            cursor.close();
	        }
	        return contactName;
	    }
	  
	  public long fetchContactIdFromPhoneNumber(Context context,String phoneNumber) {
		    Uri uri = Uri.withAppendedPath(PhoneLookup.CONTENT_FILTER_URI,
		        Uri.encode(phoneNumber));
		    Cursor cursor = context.getContentResolver().query(uri,
		        new String[] { PhoneLookup.DISPLAY_NAME, PhoneLookup._ID },
		        null, null, null);

		    long contactId = 0;

		    if (cursor.moveToFirst()) {
		        do {
		        contactId = cursor.getLong(cursor
		            .getColumnIndex(PhoneLookup._ID));
		        } while (cursor.moveToNext());
		    }

		    return contactId;
		  }

	  public Uri getPhotoUri(Context context, long contactId) {
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
		        if (!cursor.moveToFirst()) {
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

}
