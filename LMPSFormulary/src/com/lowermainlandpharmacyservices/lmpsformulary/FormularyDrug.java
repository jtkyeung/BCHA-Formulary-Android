package com.lowermainlandpharmacyservices.lmpsformulary;

import java.util.ArrayList;

public class FormularyDrug extends Drug {
	  public ArrayList<String> strengths;

		public FormularyDrug(String genericName, String brandName, String strength){
	        super(genericName,brandName,"Formulary");
	        this.genericName = genericName;
			this.brandName = brandName;
	        strengths = new ArrayList<String>();

	        strengths.add(strength);
		}

	    public void addStrength(String strength){
	        strengths.add(strength);
	    }

	    public ArrayList<String> getStrengths(){
	        return strengths;
	    }
}




