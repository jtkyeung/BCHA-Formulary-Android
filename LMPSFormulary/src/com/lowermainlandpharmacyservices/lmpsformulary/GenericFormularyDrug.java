package com.lowermainlandpharmacyservices.lmpsformulary;

import java.util.ArrayList;

public class GenericFormularyDrug extends Drug {
	public String genericName;
	public ArrayList<String> strengths;
	public ArrayList<String> brandNames;

	public GenericFormularyDrug(String genericName, String brandName, String strength){
		super("Formulary");
		this.genericName = genericName;
		
		this.brandNames = new ArrayList<String>();
		this.brandNames.add(brandName);
		
		strengths = new ArrayList<String>();
		strengths.add(strength);
	}

	public void addStrength(String strength){
		strengths.add(strength);
	}
	public void addBrandName(String brand){
		brandNames.add("brand");
	}

	public ArrayList<String> getStrengths(){
		return strengths;
	}
	
	public ArrayList<String> getBrandNames(){
		return brandNames;
	}
	
	public String getGenericName(){
		return genericName;
	}
	
	public boolean containsBrandName(String brand){
		for(String brandName:brandNames){
			if(brandName.equalsIgnoreCase(brand))
				return true;
		}
		return false;
	}
}




