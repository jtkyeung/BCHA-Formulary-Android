package com.lowermainlandpharmacyservices.lmpsformulary;

import java.util.ArrayList;
import java.util.List;

public class FormularyDrug extends Drug {
	  public List<String> strengths;

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

	    public List<String> getStrengths(){
	        return strengths;
	    }
}




