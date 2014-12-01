package com.lowermainlandpharmacyservices.lmpsformulary;

import java.util.ArrayList;

public class BrandDrug extends Drug{
	
	public String brandName;
	public ArrayList<String> genericName;
	
	public BrandDrug(String genericName, String brandName, String status){
		super(status);
		this.genericName = new ArrayList<String>();
		this.genericName.add(genericName);
		this.brandName = brandName;
	}

	public String getBrandName() {
		return brandName;
	}

	public void setBrandName(String brandName) {
		this.brandName = brandName;
	}
	
	public ArrayList<String> getGenericNameList(){
		return genericName;
	}
	public void addGenericName(String genericName){
		this.genericName.add(genericName);
	}
	public boolean containsGenericName(String genericName){
		for(String name:this.genericName){
			if(name.equals(genericName))
				return true;
		}
		return false;
	}

}
