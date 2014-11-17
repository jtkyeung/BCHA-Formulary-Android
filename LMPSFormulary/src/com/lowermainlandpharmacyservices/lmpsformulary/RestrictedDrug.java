package com.lowermainlandpharmacyservices.lmpsformulary;

public class RestrictedDrug extends Drug {
	public StringBuilder criteria;

	public RestrictedDrug(String genericName, String brandName, String criteria){
        super(genericName, brandName, "Restricted");
		this.criteria = new StringBuilder(criteria);
	}

	public String getCriteria(){
		return criteria.toString();
	}

    public void additionalCriteria(String extraCriteria){
    	System.out.println(super.genericName+ " "+criteria +" "+ extraCriteria);
        this.criteria.insert(criteria.length()-1, (" "+extraCriteria));
    }
}
