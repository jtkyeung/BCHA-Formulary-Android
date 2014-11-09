package com.lowermainlandpharmacyservices.lmpsformulary;

public class ExcludedDrug extends Drug {
	
	public ExcludedDrug(String genericName, String brandName){
        super(genericName, brandName, "Excluded");
//		super.genericName = genericName;
//		super.brandName = brandName;
//		super.status = "Excluded";
	}
	
}

