package com.netsoft.netsms;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import android.database.Cursor;

public class ListContactFetcher {
	
	public List<ListContactItem>getListContact(Cursor cursor){	
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
					
//					listContactItem.body = DateFormat.getInstance().format(cursor.getLong(cursor.getColumnIndexOrThrow("date")));
					
					listContact.add(listContactItem);
				}
		}while(cursor.moveToNext());
		
		return listContact;
	}




}
