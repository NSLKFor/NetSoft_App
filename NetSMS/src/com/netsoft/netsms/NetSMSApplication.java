package com.netsoft.netsms;

import java.util.List;

import android.app.Application;
import android.net.Uri;

public class NetSMSApplication extends Application{

	private String address;
	private String name;
	private Uri thumnail;
	private List<ListContactItem> listContactItem;
	
	public NetSMSApplication(){
		super();
	}
	
	@Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public void onTerminate() {
        super.onTerminate();
    }
    
    public void setAddress(String address){
		this.address = address;
	}
	
	public String getAddress() {
		return this.address;
	}
	
	public void setName(String name){
		this.name = name;
	}
	
	public String getName() {
		return this.name;
	}
	
	public void setThumnail(Uri thumnail){
		this.thumnail = thumnail;
	}
	
	public Uri getThumnail() {
		return this.thumnail;
	}
	public void setListContactItem(List<ListContactItem> list){
		this.listContactItem = list;
	}
	
	public List<ListContactItem> getListContactItem() {
		return this.listContactItem;
	}
	
}
