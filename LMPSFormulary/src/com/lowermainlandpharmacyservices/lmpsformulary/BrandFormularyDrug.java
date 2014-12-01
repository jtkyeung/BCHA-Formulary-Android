package com.lowermainlandpharmacyservices.lmpsformulary;

import java.util.ArrayList;

public class BrandFormularyDrug extends Drug {
	
	public String brandName;
	public String genericName;
	public ArrayList<String> strengths;
	
	public BrandFormularyDrug(String brandName,String genericName, String strength){
		super("Formulary");
		this.brandName = brandName;
		this.genericName = genericName;
		strengths = new ArrayList<String>();
		strengths.add(strength);
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

	public ArrayList<String> getStrengths() {
		return strengths;
	}

	public void setStrengths(ArrayList<String> strengths) {
		this.strengths = strengths;
	}

}
