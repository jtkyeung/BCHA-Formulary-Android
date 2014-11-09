package com.lowermainlandpharmacyservices.lmpsformulary;

import java.util.HashMap;

public class NameList {
    HashMap<String, String> nameList;

    public NameList() {
        nameList = new HashMap<String, String>();
    }

    public boolean containsBrandName(String brandName) {
        return nameList.containsValue(brandName);
    }

    public void put(String brandName, String drugName) {
        nameList.put(brandName, drugName);
    }

    public String getGenericName(String brandName){
        return nameList.get(brandName);
    }
}
