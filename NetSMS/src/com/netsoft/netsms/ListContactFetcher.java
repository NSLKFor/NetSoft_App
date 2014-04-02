package com.netsoft.netsms;

import java.util.ArrayList;
import java.util.List;

import android.database.Cursor;

public class ListContactFetcher {
	
	public List<String>getListContact(Cursor cursor){	
		ArrayList<String> listContact = new ArrayList<String>();
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
					
				if(status == 0 && cursor.getColumnName(idx).equals("address"))
					
					listContact.add(cursor.getString(idx));

		   }
		}while(cursor.moveToNext());
		
		return listContact;
	}




}
