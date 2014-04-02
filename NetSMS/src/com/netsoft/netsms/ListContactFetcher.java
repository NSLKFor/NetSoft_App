package com.netsoft.netsms;

import java.util.ArrayList;
import java.util.List;

import android.database.Cursor;

public class ListContactFetcher {
	
	public List<ListContactItem>getListContact(Cursor cursor){	
		ArrayList<ListContactItem> listContact = new ArrayList<ListContactItem>();
		cursor.moveToFirst();
		do{   
			int status = 0;
			for(int idx=0;idx<cursor.getColumnCount();idx++)
			{
			   for(int i = 0; i < listContact.size(); i++){
				   if( cursor.getColumnName(idx).equals("address") && listContact.get(i).equals(cursor.getString(idx))){
					   status = 1;
					   break;
				   }
			   }
					
				if(status == 0 && cursor.getColumnName(idx).equals("address")){
					ListContactItem listContactItem = new ListContactItem();
					
					listContactItem.address = cursor.getString(idx);
					listContactItem.body = cursor.getString(cursor.getColumnIndexOrThrow("body")).toString();
					listContactItem.time = cursor.getString(cursor.getColumnIndexOrThrow("date")).toString();
					listContact.add(listContactItem);
				}				
		   }
		}while(cursor.moveToNext());
		
		return listContact;
	}




}
