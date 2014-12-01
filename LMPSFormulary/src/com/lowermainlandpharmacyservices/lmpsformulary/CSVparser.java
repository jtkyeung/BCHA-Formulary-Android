package com.lowermainlandpharmacyservices.lmpsformulary;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import com.opencsv.CSVReader;


public class CSVparser {
	GenericDrugList genericList;
	BrandDrugList brandList;

	public CSVparser(){
		genericList = new GenericDrugList();
		brandList = new BrandDrugList();
	}
	public void parseFormulary(InputStream csvFile){
		BufferedReader dataFile = new BufferedReader(new InputStreamReader(csvFile));
		CSVReader reader = null;
		//		int count = 0;
		try {
			reader = new CSVReader(dataFile);
			String [] nextLine;
			reader.readNext(); //title line
			while ((nextLine = reader.readNext()) != null) 	{
				if(!(nextLine[0].equals(""))){ //handles all the empty lines

					//genericList-------------------------------------------------------------------------------
					if(genericList.containsGenericName(nextLine[0])){ //if drug already in the list
						//add strength
						((GenericFormularyDrug)genericList.getGenericDrug(nextLine[0])).addStrength(nextLine[1]);

						//if brandName cell is not empty, check if the brandNames are unique and add to drug if needed
						if(!(nextLine[2].equals(""))){
							addBrandNameToExistingFormularyDrug(nextLine[0], nextLine[2]);
						}
					}
					else if(nextLine[2].equals("")){ //if there is a drug with no brand name
						genericList.addGenericDrug(new GenericFormularyDrug(nextLine[0],"", nextLine[1]));
					}
					else{
						addGenericFormularyDrugWithBrandName(nextLine[0],nextLine[1], nextLine[2]);
					}
				}
				//brandList------------------------------------------------------------------------------
				if(!(nextLine[2].equals(""))){
					if(nextLine[2].contains(",")){ //if there is more than 1 brand name per cell
						String[] brandNameList;
						brandNameList = nextLine[2].split(",");//break up brand names 
						for(String brandName:brandNameList){
							addBrandNameFormulary(nextLine[0], brandName, nextLine[1]);//add brand names one by one
						}
					}
					else{ //there is only 1 brand name
						addBrandNameFormulary(nextLine[0], nextLine[2], nextLine[1]);//add just the one brand name
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
		BufferedReader dataFile = new BufferedReader(new InputStreamReader(csvFile));
		CSVReader reader = null;
		try {
			reader = new CSVReader(dataFile);
			String [] nextLine;
			reader.readNext(); //title line
			while ((nextLine = reader.readNext()) != null) 	{
				if(!(nextLine[0].equals(""))){ //handles empty lines
					genericList.addGenericDrug(new GenericExcludedDrug(nextLine[0],nextLine[1]));
					brandList.addBrandDrug(new BrandExcludedDrug(nextLine[0], nextLine[1])); //may need to make method robust by iterating multiple brand names
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

	public void parseRestricted(InputStream csvFile){
		BufferedReader dataFile = new BufferedReader(new InputStreamReader(csvFile));
		CSVReader reader = null;
		try {
			reader = new CSVReader(dataFile);
			String [] nextLine;
			String lastGenericDrug=null;
			String lastBrandDrug = null;

			reader.readNext(); //title line
			while ((nextLine = reader.readNext()) != null) 	{

				//extraline for restricted criteria
				if((nextLine[0].equals("")) && !(nextLine[2].equals(""))){
					((GenericRestrictedDrug)genericList.getGenericDrug(lastGenericDrug)).additionalCriteria(nextLine[2]);
					((BrandRestrictedDrug)brandList.getBrandDrug(lastBrandDrug)).additionalCriteria(nextLine[2]);
				}
				else if(!(nextLine[0].equals(""))){//handles blank lines
					if(nextLine[1].equals("")){//no brandname
						genericList.addGenericDrug(new GenericRestrictedDrug(nextLine[0], "", nextLine[2]));
						lastGenericDrug = nextLine[0]; //sets the last drug if next line is extra criteria
					}
					else{
						if(nextLine[2].contains(",")){
							String[] brandNameList;
							brandNameList = nextLine[2].split(",");
							brandList.addBrandDrug(new BrandRestrictedDrug(nextLine[0], brandNameList[0], nextLine[2]));
							for(String additionalBrand:brandNameList){
								//if brand name already exists, add just the generic name to the list
								if(brandList.containsBrandName(nextLine[1])){
									((BrandRestrictedDrug)brandList.getBrandDrug(nextLine[1])).addGenericName(nextLine[0]);
								}
								else{
								brandList.addBrandDrug(new BrandRestrictedDrug(nextLine[0], additionalBrand, nextLine[2]));
								}
							}
							lastBrandDrug = brandNameList[0];
						}
						else{
							if(brandList.containsBrandName(nextLine[1])){
								((BrandRestrictedDrug)brandList.getBrandDrug(nextLine[1])).addGenericName(nextLine[0]);
							}
							else{
							brandList.addBrandDrug(new BrandRestrictedDrug(nextLine[0], nextLine[1], nextLine[2]));
							lastBrandDrug = nextLine[1];
							}
						}
					}
				}
			}
		}
		catch (IOException e) {
			System.out.println(e.getMessage());
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
			genericList.addGenericDrug(new GenericFormularyDrug(genericName, brandNameList[0], strength));
			for(String extraBrands:brandNameList){
				addBrandNameToExistingFormularyDrug(genericName, extraBrands);
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