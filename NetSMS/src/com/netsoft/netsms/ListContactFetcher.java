package com.netsoft.netsms;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.ContactsContract.PhoneLookup;

public class ListContactFetcher {
	
	public List<ListContactItem>getListContact(Context context, Cursor cursor){	
		ArrayList<ListContactItem> listContact = new ArrayList<ListContactItem>();
		cursor.moveToFirst();
		do{   
			int status = 0;
			   for(int i = 0; i < listContact.size(); i++){
				   if(listContact.get(i).address.equals(cursor.getString(cursor.getColumnIndexOrThrow("address")).toString())){
					   status = 1;
					   break;
				   }
			   }

				if(status == 0){
					ListContactItem listContactItem = new ListContactItem();
					
					listContactItem.address = cursor.getString(cursor.getColumnIndexOrThrow("address")).toString();
					listContactItem.body = cursor.getString(cursor.getColumnIndexOrThrow("body")).toString();
					listContactItem.time =   cursor.getLong(cursor.getColumnIndexOrThrow("date"));
					listContactItem.name = getContactName(context, listContactItem.address);
					
//					listContactItem.body = DateFormat.getInstance().format(cursor.getLong(cursor.getColumnIndexOrThrow("date")));
					
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


}
