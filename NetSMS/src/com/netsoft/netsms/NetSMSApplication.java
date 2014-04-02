package com.netsoft.netsms;

import android.app.Application;

public class NetSMSApplication extends Application{

	private String address;
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
	
}
