package com.lowermainlandpharmacyservices.lmpsformulary;

public class BrandExcludedDrug extends Drug {
	public String brandName;
	public String genericName;

	public BrandExcludedDrug(String brandName, String genericName){
		super("Excluded");
		this.brandName = brandName;
		this.genericName = genericName;
	}

	public String getBrandName() {
		return brandName;
	}

	public void setBrandName(String brandName) {
		this.brandName = brandName;
	}

	public String getGenericName() {
		return genericName;
	}

	public void setGenericName(String genericName) {
		this.genericName = genericName;
	}

}
