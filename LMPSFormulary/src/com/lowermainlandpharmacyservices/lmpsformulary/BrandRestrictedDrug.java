package com.lowermainlandpharmacyservices.lmpsformulary;


public class BrandRestrictedDrug extends Drug {

	public String brandName;
	public String genericName;
	public StringBuilder criteria;

	public BrandRestrictedDrug(String genericName, String brandName, String criteria){
        super("Restricted");
        this.genericName = genericName;
        this.brandName = brandName;
		this.criteria = new StringBuilder(criteria);
	}

	public String getCriteria(){
		return criteria.toString();
	}

    public void additionalCriteria(String extraCriteria){
//    	System.out.println(genericName+ " "+criteria +" "+ extraCriteria);
        this.criteria.insert(criteria.length(), (" "+extraCriteria));
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
