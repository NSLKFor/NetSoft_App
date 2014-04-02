package com.netsoft.netsms;

import java.util.ArrayList;
import java.util.List;

import android.database.Cursor;

public class SmsFetcher {
	private String address;
	
	public SmsFetcher (String address){
		this.address = address;
	}
	
	public List<SmsItem>getListSMS( Cursor cursor){
		
		List<SmsItem> listMessage = new ArrayList<SmsItem>();
		cursor.moveToFirst();
		do{
			if(cursor.getString(cursor.getColumnIndexOrThrow("address")).equals(this.address)){
				SmsItem item = new SmsItem();
				item.address = this.address;
				item.id = Integer.parseInt(cursor.getString(cursor.getColumnIndexOrThrow("_id")).toString()) ;
				item.body = cursor.getString(cursor.getColumnIndexOrThrow("body")).toString();
				item.readStatus = Integer.parseInt(cursor.getString(cursor.getColumnIndexOrThrow("read")).toString());
				item.type = Integer.parseInt(cursor.getString(cursor.getColumnIndexOrThrow("type")).toString());
				item.date = cursor.getString(cursor.getColumnIndexOrThrow("date")).toString();
				
				listMessage.add(item);
			}
		}while(cursor.moveToNext());
		
		return listMessage;
	}

}
