package com.lowermainlandpharmacyservices.lmpsformulary;

import java.util.ArrayList;

public class GenericExcludedDrug extends Drug {
	public String genericName;
	public ArrayList<String> brandNames;
	
	public GenericExcludedDrug(String genericName, String brandName){
        super("Excluded");
        this.genericName = genericName;
        brandNames = new ArrayList<String>();
        brandNames.add(brandName);
	}
	
	public String getGenericName(){
		return genericName;
	}
	
	public ArrayList<String> getBrandNames(){
		return brandNames;
	}
	
	public boolean containsBrandName(String brand){
		for(String brandName:brandNames){
			if(brandName.equals(brand))
				return true;
		}
		return false;
	}
	
}

