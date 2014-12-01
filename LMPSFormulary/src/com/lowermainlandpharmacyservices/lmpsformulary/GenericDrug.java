package com.lowermainlandpharmacyservices.lmpsformulary;

import java.util.ArrayList;

public class GenericDrug extends Drug{
	public ArrayList<String> brandNames;
	
	public GenericDrug(String genericName,String brandName, String status){
		super(genericName, status); //superconstructor Drug
		
		brandNames = new ArrayList<String>();
		brandNames.add(brandName);
	}
	
	public boolean containsBrandName(String brand){
		for(String brandName:brandNames){
			if(brandName.equalsIgnoreCase(brand))
				return true;
		}
		return false;
	}
	
	public void addBrandName(String brand){
		brandNames.add(brand);
	}
	
	public ArrayList<String> getBrandNames(){
		return brandNames;
	}

}
