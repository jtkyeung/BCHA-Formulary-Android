package com.lowermainlandpharmacyservices.lmpsformulary;

import java.util.ArrayList;

public class GenericRestrictedDrug extends Drug {
	public String genericName;
	public ArrayList<String> brandNames;
	public StringBuilder criteria;

	public GenericRestrictedDrug(String genericName, String brandName, String criteria){
        super("Restricted");
        this.genericName = genericName;
        brandNames = new ArrayList<String>();
        brandNames.add(brandName);
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
