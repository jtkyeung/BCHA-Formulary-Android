package com.lowermainlandpharmacyservices.lmpsformulary;

import java.util.HashMap;

public class DrugList {
	
	private HashMap<String, Drug> drugList;

	public DrugList(){
        drugList = new HashMap<String, Drug>();
	}


	public void addDrug(Drug newDrug){

        drugList.put(newDrug.getGenericName(), newDrug);

	}

	public void removeDrug(Drug drug){

        if (drugList.containsValue(drug) != false) {
            drugList.remove(drug.getGenericName());
        }

	}

    public Drug getDrug(String drug) {

        return drugList.get(drug);
    }


	public boolean isEmpty(){

        return drugList.isEmpty();
	}

    public boolean containsDrug(Drug drug) {

        return drugList.containsValue(drug);
    }

    public boolean containsGenericName(String drug) {

        return drugList.containsKey(drug);
    }

}






