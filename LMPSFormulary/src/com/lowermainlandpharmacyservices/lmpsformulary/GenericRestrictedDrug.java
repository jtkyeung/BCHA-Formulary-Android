package com.lowermainlandpharmacyservices.lmpsformulary;


public class GenericRestrictedDrug extends GenericDrug {
	public StringBuilder criteria;

	public GenericRestrictedDrug(String genericName, String brandName, String criteria){
        super(genericName, brandName, "Restricted");
		this.criteria = new StringBuilder(criteria);
	}

	public String getCriteria(){
		return criteria.toString();
	}

    public void additionalCriteria(String extraCriteria){
//    	System.out.println(genericName+ " "+criteria +" "+ extraCriteria);
        this.criteria.insert(criteria.length(), (" "+extraCriteria));
    }
}
