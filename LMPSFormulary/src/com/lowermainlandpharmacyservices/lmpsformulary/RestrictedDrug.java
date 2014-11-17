package com.lowermainlandpharmacyservices.lmpsformulary;

public class RestrictedDrug extends Drug {
	private StringBuffer criteria;

	public RestrictedDrug(String genericName, String brandName, String criteria){
        super(genericName, brandName, "Restricted");
		this.criteria = new StringBuffer(criteria);
	}

	public String getCriteriaString(){
		return criteria.toString();
	}

    public void additionalCriteria(String extraCriteria){
        this.criteria.append(extraCriteria);
    }
}
