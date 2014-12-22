package com.lowermainlandpharmacyservices.lmpsformulary;


public class GenericExcludedDrug extends GenericDrug {
	public StringBuilder criteria;
	
	public GenericExcludedDrug(String genericName, String brandName, String criteria){
        super(genericName, brandName, "Excluded");
        this.criteria = new StringBuilder(criteria);
	}
	
	public String getCriteria(){
		return criteria.toString();
	}
	
	 public void additionalCriteria(String extraCriteria){    	
	    	StringBuilder extraAddition = new StringBuilder();
	    	
	    	//add bullet
	    	if(!(extraCriteria.contains(":")|| extraCriteria.contains("OR")&&extraCriteria.length()==2)){
	    		extraAddition.append("-   ");
	    	}
	    	
	    	for (int i = 0; i < extraCriteria.length();i++){
	    			extraAddition.append(extraCriteria.charAt(i));
	    	}
	    	this.criteria.insert(criteria.length(), ("\n\n"+extraAddition.toString()));	
	    }
}

