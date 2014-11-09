package com.lowermainlandpharmacyservices.lmpsformulary;

public class RestrictedDrug extends Drug {
	private String criteria;

	public RestrictedDrug(String genericName, String brandName, String criteria){
        super(genericName, brandName, "Restricted");
//		super.genericName = genericName;
//		super.brandName = brandName;
//		super.status = "Restricted";
		this.criteria = criteria;
	}

	public String getCriteria(){
		return criteria;
	}

    public void setCriteria(String newCriteria){
        this.criteria = newCriteria;
    }
}
