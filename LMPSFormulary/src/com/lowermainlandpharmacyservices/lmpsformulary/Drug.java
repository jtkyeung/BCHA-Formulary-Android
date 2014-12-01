package com.lowermainlandpharmacyservices.lmpsformulary;

public class Drug {
	public String genericName;
	protected String status;
	
    public Drug(String genericName, String status){
    	this.genericName = genericName;
        this.status = status;
    }
    
    public String getGenericName(){
    	return genericName;
    }
    
    public void setGenericName(String generic){
    	this.genericName = generic;
    }

    public String getStatus() { 
    	return status; 
    }
    
    public void setStatus(String newStatus){
        this.status = newStatus;
    }
}


