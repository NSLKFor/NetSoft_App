package com.example.testmms;

public class APN {
    public String MMSCenterUrl = "";
    public String MMSPort = "";
    public String MMSProxy = ""; 
    
    public APN(String MMSCenterUrl, String MMSPort, String MMSProxy)
    {
    	this.MMSCenterUrl = MMSCenterUrl;
    	this.MMSPort = MMSPort;
    	this.MMSProxy = MMSProxy;
    }
    
    public APN()
    {
    	
    }
}
