package com.lowermainlandpharmacyservices.lmpsformulary;

public class BrandDrug extends Drug{
	
	public String brandName;
	
	public BrandDrug(String genericName, String brandName, String status){
		super(genericName, status);
		this.brandName = brandName;
	}

	public String getBrandName() {
		return brandName;
	}

	public void setBrandName(String brandName) {
		this.brandName = brandName;
	}
	
	

}
