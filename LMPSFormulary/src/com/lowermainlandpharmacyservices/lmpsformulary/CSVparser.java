package com.lowermainlandpharmacyservices.lmpsformulary;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;

import com.opencsv.CSVReader;

public class CSVparser {
	GenericDrugList genericList;
	BrandDrugList brandList;

	public CSVparser(){
		genericList = new GenericDrugList();
		brandList = new BrandDrugList();
	}
	public void parseFormulary(InputStream csvFile){
	//public void parseFormulary(BufferedReader dataFile){
		BufferedReader dataFile = new BufferedReader(new InputStreamReader(csvFile));
		CSVReader reader = null;
		//		int count = 0;
		try {
			reader = new CSVReader(dataFile);
			String [] nextLine;
			reader.readNext(); //title line
			while ((nextLine = reader.readNext()) != null) 	{
				String name = nextLine[0].toUpperCase();
				String brandname = nextLine[2].toUpperCase();
				
				if(!(name.equals(""))){ //handles all the empty lines
					
					//genericList-------------------------------------------------------------------------------
					if(genericList.containsGenericName(name)){ //if drug already in the list
						//add strength
						((GenericFormularyDrug)genericList.getGenericDrug(name)).addStrength(nextLine[1]);

						//if brandName cell is not empty, check if the brandNames are unique and add to drug if needed
						if(!(brandname.equals(""))){
							addBrandNameToExistingFormularyDrug(name, brandname);
						}
					}
					else if(brandname.equals("")){ //if there is a drug with no brand name
						genericList.addGenericDrug(new GenericFormularyDrug(name,"", nextLine[1]));
					}
					else{
						addGenericFormularyDrugWithBrandName(name,nextLine[1], brandname);
					}
				}
				//brandList------------------------------------------------------------------------------
				if(!(brandname.equals(""))){
					if(brandname.contains(",")){ //if there is more than 1 brand name per cell
						String[] brandNameList;
						brandNameList = brandname.split(",");//break up brand names 
						for(String brandName:brandNameList){
							addBrandNameFormulary(name, brandName.trim(), nextLine[1]);//add brand names one by one
						}
					}
					else{ //there is only 1 brand name
						addBrandNameFormulary(name, brandname, nextLine[1]);//add just the one brand name
					}
				}
			}
			dataFile.close();
		} catch (IOException e) {
			System.out.println(e.getMessage());
			e.printStackTrace();
		}
		finally{
			dataFile = null;
		}
	}

	public void parseExcluded(InputStream csvFile){
		BufferedReader dataFile;
		try {
			dataFile = new BufferedReader(new InputStreamReader(csvFile,"UTF-8"));
		} catch (UnsupportedEncodingException e1) {
			dataFile = new BufferedReader(new InputStreamReader(csvFile));
			e1.printStackTrace();
			System.out.println("Can not read");
		}
		CSVReader reader = null;
		try {
			reader = new CSVReader(dataFile);
			String [] nextLine;
			String lastGenericDrug=null;
			String lastBrandDrug = null;

			reader.readNext(); //title line
			while ((nextLine = reader.readNext()) != null) 	{
				
				String name = nextLine[0].toUpperCase();
				String brandname = nextLine[2].toUpperCase();
				
				
				//extraline for restricted criteria
				if((name.equals("")) && !(brandname.equals(""))){
					((GenericExcludedDrug)genericList.getGenericDrug(lastGenericDrug)).additionalCriteria(brandname);
					((BrandExcludedDrug)brandList.getBrandDrug(lastBrandDrug)).additionalCriteria(brandname);
				}
				else if(!(name.equals(""))){//handles blank lines
					if(nextLine[1].equals("")){//no brandname
						genericList.addGenericDrug(new GenericExcludedDrug(name, "", brandname));
						lastGenericDrug = name; //sets the last drug if next line is extra criteria
					}
					else{
						if(nextLine[1].contains(",")){
							String[] brandNameList;
							brandNameList = nextLine[1].split(",");
							brandList.addBrandDrug(new BrandExcludedDrug(name, brandNameList[0], brandname));
							for(String additionalBrand:brandNameList){
								//if brand name already exists, add just the generic name to the list
								if(brandList.containsBrandName(nextLine[1])){
									((BrandExcludedDrug)brandList.getBrandDrug(nextLine[1])).addGenericName(name);
								}
								else{
									brandList.addBrandDrug(new BrandExcludedDrug(name, additionalBrand, brandname));
									genericList.addGenericDrug(new GenericExcludedDrug(name, nextLine[1], brandname));
									lastGenericDrug = name; //sets the last drug if next line is extra criteria
								}
							}
							lastBrandDrug = brandNameList[0];
						}
						else{
							if(brandList.containsBrandName(nextLine[1]) && 
									brandList.getBrandDrug(nextLine[1]).getStatus().equals("Excluded")){
								((BrandExcludedDrug)brandList.getBrandDrug(nextLine[1])).addGenericName(name);
							}
							else{
								brandList.addBrandDrug(new BrandExcludedDrug(name, nextLine[1], brandname));
								lastBrandDrug = nextLine[1];
								genericList.addGenericDrug(new GenericExcludedDrug(name, nextLine[1], brandname));
								lastGenericDrug = name; //sets the last drug if next line is extra criteria
							}
						}
					}
				}
			}
			dataFile.close();
		}
		catch (IOException e) {
			System.out.println("I/O error " + e.getMessage());
			e.printStackTrace();
		}
		finally{
			dataFile = null;
		}
	}

	public void parseRestricted(InputStream csvFile){
		BufferedReader dataFile;
		try {
			dataFile = new BufferedReader(new InputStreamReader(csvFile,"UTF-8"));
		} catch (UnsupportedEncodingException e1) {
			dataFile = new BufferedReader(new InputStreamReader(csvFile));
			e1.printStackTrace();
		}
		CSVReader reader = null;
		try {
			reader = new CSVReader(dataFile);
			String [] nextLine;
			String lastGenericDrug=null;
			String lastBrandDrug = null;
			ArrayList<String> restrictedBrandNameList = new ArrayList<String>();

			reader.readNext(); //title line
			while ((nextLine = reader.readNext()) != null) 	{
				String name = nextLine[0].trim().toUpperCase();
				String restrictedCriteria = nextLine[2].trim().toUpperCase();
				
				//extraline for restricted criteria
				if((name.equals("")) && !(restrictedCriteria.equals(""))){
					((GenericRestrictedDrug)genericList.getGenericDrug(lastGenericDrug)).additionalCriteria(restrictedCriteria);
					((BrandRestrictedDrug)brandList.getBrandDrug(lastBrandDrug)).additionalCriteria(restrictedCriteria);
				}
				else if(!(name.equals(""))){//handles blank lines
					if(nextLine[1].equals("")){//no brandname
						genericList.addGenericDrug(new GenericRestrictedDrug(name, "", restrictedCriteria));
						lastGenericDrug = name; //sets the last drug if next line is extra criteria
					}
					else{
						if(nextLine[1].contains(",")){ //multiple brand names
							String[] brandNameList;
							brandNameList = nextLine[1].split(",");
							brandList.addBrandDrug(new BrandRestrictedDrug(name, brandNameList[0], restrictedCriteria));
							restrictedBrandNameList.add(brandNameList[0]);
							for(String additionalBrand:brandNameList){
								restrictedBrandNameList.add(additionalBrand);
								//if brand name already exists, add just the generic name to the list
								if(restrictedBrandNameList.contains(additionalBrand.trim())){
									((BrandRestrictedDrug)brandList.getBrandDrug(additionalBrand.trim())).addGenericName(name);
								}
								else{
									brandList.addBrandDrug(new BrandRestrictedDrug(name, additionalBrand.trim(), restrictedCriteria));
									genericList.addGenericDrug(new GenericRestrictedDrug(name, additionalBrand.trim(), restrictedCriteria));
								}
							}
							lastGenericDrug = name; //sets the last drug if next line is extra criteria
							lastBrandDrug = brandNameList[0];
						}
						else{ //single brand name
							if(brandList.containsBrandName(nextLine[1]) && 
									brandList.getBrandDrug(nextLine[1]).getStatus().equals("Restricted")){
								((BrandRestrictedDrug)brandList.getBrandDrug(nextLine[1])).addGenericName(name);
							}
							else{
								brandList.addBrandDrug(new BrandRestrictedDrug(name, nextLine[1], restrictedCriteria));
								restrictedBrandNameList.add(nextLine[1]);
								lastBrandDrug = nextLine[1];
								genericList.addGenericDrug(new GenericRestrictedDrug(name, nextLine[1], restrictedCriteria));
								lastGenericDrug = name; //sets the last drug if next line is extra criteria
							}
						}
					}
				}
			}
			dataFile.close();
		}
		catch (IOException e) {
			System.out.println("I/O error " + e.getMessage());
			e.printStackTrace();
		}
		finally{
			dataFile = null;
		}
	}

	private void addBrandNameFormulary(String genericName, String brandName,
			String strength) {
		if(brandList.containsBrandName(brandName)){
			//add strength
			((BrandFormularyDrug)brandList.getBrandDrug(brandName)).addStrength(strength);
			//add generic name
			if(!((BrandFormularyDrug)brandList.getBrandDrug(brandName)).containsGenericName(genericName))
				((BrandFormularyDrug)brandList.getBrandDrug(brandName)).addGenericName(genericName);
		}
		else{
			brandList.addBrandDrug(new BrandFormularyDrug(genericName, brandName, strength));
		}
	}

	private void addGenericFormularyDrugWithBrandName(String genericName, String strength,
			String brandName) {
		if(brandName.contains(",")){
			String[] brandNameList;
			brandNameList = brandName.split(",");
			genericList.addGenericDrug(new GenericFormularyDrug(genericName, brandNameList[0].trim().toUpperCase(), strength));
			for(int i = 1; i < brandNameList.length;i++){
				brandName=brandNameList[i].trim();
				addBrandNameToExistingFormularyDrug(genericName, brandNameList[i].trim());
			}
		}
		else{
			genericList.addGenericDrug(new GenericFormularyDrug(genericName, brandName, strength));
		}

	}
	private void addBrandNameToExistingFormularyDrug(String genericName, String brandName) {
		if(genericList.containsGenericName(genericName)){
			if(!((GenericFormularyDrug)genericList.getGenericDrug(genericName)).containsBrandName(brandName)){
				((GenericFormularyDrug)genericList.getGenericDrug(genericName)).addBrandName(brandName);
			}
		}
	}

	public GenericDrugList getListByGeneric() {
		return genericList;
	}

	public BrandDrugList getListByBrand() {
		return brandList;
	}
}