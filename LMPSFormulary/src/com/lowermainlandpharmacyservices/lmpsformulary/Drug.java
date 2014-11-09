package com.lowermainlandpharmacyservices.lmpsformulary;

public class Drug {
	protected String genericName;
	protected String brandName;
	protected String status;
	
	
	public Drug(){
	}

    public Drug(String genName, String brandName, String status){
        this.genericName = genName;
        this.brandName = brandName;
        this.status = status;
    }
	
	public String getGenericName(){
		return genericName;
	}
	
	public String getBrandName(){
		return brandName;
	}

    public String getStatus() { return status; }

    public void setGenericName(String newGenName) {
        this.genericName = newGenName;
    }
    public void setBrandName(String newBrandName){
        this.brandName = newBrandName;
    }

    public void setStatus(String newStatus){
        this.status = newStatus;
    }
}


