package com.lowermainlandpharmacyservices.lmpsformulary;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.Normalizer;

import com.opencsv.CSVReader;


public class CSVparser {
	DrugList supplyList;
	NameList nameList;

	public CSVparser(){
		supplyList = new DrugList();
		nameList = new NameList();
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
				//				count++;
				if(!(nextLine[0].equals(""))){
					// nextLine[] is an array of values from the line
					if(supplyList.containsGenericName(nextLine[0])){
						((FormularyDrug)supplyList.getDrug(nextLine[0])).addStrength(nextLine[1]);
					}
					else if(nextLine[2].equals(""))
						supplyList.addDrug(new FormularyDrug(nextLine[0], "N/A", nextLine[1]));
					else{
						supplyList.addDrug(new FormularyDrug(nextLine[0], nextLine[2], nextLine[1]));
						addBrandName(nextLine[0], nextLine[2]);
					}

				}
			}
			dataFile.close();
		}
		catch (IOException e) {
			System.out.println(e.getMessage());
			e.printStackTrace();
		}
		finally{
			System.out.println(supplyList.getDrugListSize() + " Formulary Drug objects made.");
			dataFile=null; //clear file
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
				if(!(nextLine[0].equals(""))){
					// nextLine[] is an array of values from the line
					if(nextLine[1].equals(""))
						supplyList.addDrug(new ExcludedDrug(nextLine[0], "N/A"));
					else{
						supplyList.addDrug(new ExcludedDrug(nextLine[0], nextLine[1]));
						addBrandName(nextLine[0], nextLine[1]);
					}
				}
			}
			dataFile.close();
		}
		catch (IOException e) {
			System.out.println(e.getMessage());
			e.printStackTrace();
		}
		finally{
			System.out.println(supplyList.getDrugListSize() + " Excluded Drug objects made.");
			dataFile=null; //clear file
		}
	}
	public void parseRestricted(InputStream csvFile){
		BufferedReader dataFile = new BufferedReader(new InputStreamReader(csvFile));
		CSVReader reader = null;
		try {
			reader = new CSVReader(dataFile);
			String [] nextLine;
			String lastDrug=null;

			reader.readNext(); //title line
			while ((nextLine = reader.readNext()) != null) 	{
				
				if((nextLine[0].equals("")) && !(nextLine[2].equals(""))){
					((RestrictedDrug)supplyList.getDrug(lastDrug)).additionalCriteria(nextLine[2] + "\n");//TODO fix to add line break
				}
				else if(!((nextLine[0].equals("")))){
					if(nextLine[1].equals("")){
						supplyList.addDrug(new RestrictedDrug(nextLine[0], "N/A", nextLine[2]));
						lastDrug = nextLine[0];
					}
					else{
						supplyList.addDrug(new RestrictedDrug(nextLine[0], nextLine[1], nextLine[2]));
						addBrandName(nextLine [0], nextLine[1]);
						lastDrug = nextLine[0];
					}
				}
			}
			dataFile.close();
		}
		catch (IOException e) {
			System.out.println(e.getMessage());
			e.printStackTrace();
		}
		finally{
			System.out.println(supplyList.getDrugListSize() + " Restricted Drug objects made.");
			dataFile=null; //clear file
		}
	}

	private void addBrandName(String genericName, String brandName) {
		String[] brandNameList;
		if(brandName.contains(",")){
			brandNameList = brandName.split(",");
			for(String nameToAdd: brandNameList){
				nameList.put(nameToAdd.trim(), genericName);
			}
		}
		else{
			nameList.put(brandName.trim(), genericName);
		}
	}
	public Drug getDrugInSystem(String drug) {
		if (supplyList.containsGenericName(drug)){
			return supplyList.getDrug(drug);
		}
		else if(nameList.containsBrandName(drug)){
			return supplyList.getDrug(nameList.getGenericName(drug));
		}
		return null;
	}

	public DrugList getSupplyList(){
		return supplyList;
	}

	public NameList getNameList(){
		return nameList;
	}


}