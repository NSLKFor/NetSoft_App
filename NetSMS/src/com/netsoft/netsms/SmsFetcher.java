package com.netsoft.netsms;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import android.R.integer;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;

public class SmsFetcher {
	private String address;
	
	public SmsFetcher (String address){
		this.address = address;
	}
	
	public List<SmsItem>getListSMS( Context context){
		
		Uri message = Uri.parse("content://sms/");
		ContentResolver cr = context.getContentResolver();
		Cursor cursor = cr.query(message, null, null, null, null);
		
		List<SmsItem> listMessage = new ArrayList<SmsItem>();
		cursor.moveToFirst();
		do{
			String temp = cursor.getString(cursor.getColumnIndexOrThrow("address"));
			if(temp.equals(this.address) || temp.equals(convertAddress(this.address))){
				SmsItem item = new SmsItem();
				item.address = this.address;
				item.id = Integer.parseInt(cursor.getString(cursor.getColumnIndexOrThrow("_id")).toString()) ;
				item.body = cursor.getString(cursor.getColumnIndexOrThrow("body")).toString();
				item.readStatus = Integer.parseInt(cursor.getString(cursor.getColumnIndexOrThrow("read")).toString());
				item.type = Integer.parseInt(cursor.getString(cursor.getColumnIndexOrThrow("type")).toString());
				item.date = cursor.getLong(cursor.getColumnIndexOrThrow("date")) ;
				
				listMessage.add(item);
				if(item.readStatus  == 0){
					ContentValues values = new ContentValues();
					values.put("read", true);
					context.getContentResolver().update(Uri.parse("content://sms/inbox"), values, "_id=" + cursor.getString(cursor.getColumnIndexOrThrow("_id")), null);
				}
			}
		}while(cursor.moveToNext());
		
		listMessage = SortListSMS(listMessage);
		
		return listMessage;
	}

	public List<SmsItem> addItem2List(Cursor cursor, String strBody2) {
		// TODO Auto-generated method stub
		List<SmsItem> listMessage = new ArrayList<SmsItem>();
		cursor.moveToFirst();
		do{
			String temp = cursor.getString(cursor.getColumnIndexOrThrow("address"));
			
			if(temp.equals(this.address) || temp.equals(convertAddress(this.address))){
				SmsItem item = new SmsItem();
				item.address = this.address;
				item.id = Integer.parseInt(cursor.getString(cursor.getColumnIndexOrThrow("_id")).toString()) ;
				item.body = cursor.getString(cursor.getColumnIndexOrThrow("body")).toString();
				item.readStatus = Integer.parseInt(cursor.getString(cursor.getColumnIndexOrThrow("read")).toString());
				item.type = Integer.parseInt(cursor.getString(cursor.getColumnIndexOrThrow("type")).toString());
				item.date = cursor.getLong(cursor.getColumnIndexOrThrow("date"));
				
//				item.body = Integer.toString((int) System.currentTimeMillis());
				listMessage.add(item);
			}
		}while(cursor.moveToNext());
		
		SmsItem item = new SmsItem();
		item.address = this.address;
		item.body = strBody2;
				
		item.id = (Integer) 0; //Integer.parseInt(cursor.getString(cursor.getColumnIndexOrThrow("_id")).toString()) ;
		item.readStatus = (Integer) 0; //Integer.parseInt(cursor.getString(cursor.getColumnIndexOrThrow("read")).toString());
		item.type = (Integer) 0 ; //Integer.parseInt(cursor.getString(cursor.getColumnIndexOrThrow("type")).toString());
		item.date = 0; //cursor.getLong(cursor.getColumnIndexOrThrow("date")) / 1000;
		
		listMessage.add(item);
		
		listMessage = SortListSMS(listMessage);
		return listMessage;
	}
	public List<SmsItem> SortListSMS (List<SmsItem> list){
		
		int lenght = list.size();
		for(int i =0 ; i < lenght/2; i++){
			SmsItem temp = list.get(i);
			list.set(i, list.get(lenght - i  - 1));
			list.set(lenght - i - 1, temp);
		}
		return list;
	}
	public String convertAddress (String add){
		String temp = "";
		switch(add.length()){
		case 10:
			temp =  "+84" + add.substring(1);
			break;
		case 11:
			temp =  "+84" + add.substring(1);
			break;
		};


		return temp;
	}
}
