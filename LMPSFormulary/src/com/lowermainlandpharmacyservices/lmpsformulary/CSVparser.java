package com.lowermainlandpharmacyservices.lmpsformulary;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;


public class CSVparser {
	BufferedReader dataFile;
	//	BufferedReader excludedFile;
	//	BufferedReader restrictedFile;
	//	BufferedReader formularyFile;
	String splitBy = ",";
	String[] wordToken = null;

	//	String formularyLine;
	//	String excludedLine;
	//	String restrictedLine;
	String dataLine;

	DrugList supplyList;
	NameList nameList;
	int counter = 0;		//FOR TESTING
	int counter2 = 0;

	//	public CSVparser(InputStream formularyCSV,InputStream excludedCSV, InputStream restrictedCSV) {
	//		formularyFile = new BufferedReader(new InputStreamReader(formularyCSV));
	//		excludedFile = new BufferedReader(new InputStreamReader(excludedCSV));
	//		restrictedFile = new BufferedReader(new InputStreamReader(restrictedCSV));
	//
	//		supplyList = new DrugList();
	//		nameList = new NameList();
	//	}
	public CSVparser(InputStream dataCSV){
		dataFile = new BufferedReader(new InputStreamReader(dataCSV));
		supplyList = new DrugList();
		nameList = new NameList();
	}

	public void parseFormulary() throws IOException{


		//formulary csv:[generic name, strength, brand name]
		try {
			dataLine = dataFile.readLine(); //ignore title line

			while ((dataLine = dataFile.readLine()) != null) {
				wordToken = dataLine.split(splitBy);
				//counter++;
				//if the drug is already found in the list, add strength to formulary
				if(supplyList.containsGenericName(wordToken[0])){
					((FormularyDrug)supplyList.getDrug(wordToken[0])).addStrength(wordToken[1]);
					counter++;
				}
				else {
					supplyList.addDrug(new FormularyDrug(wordToken[0], wordToken[2], wordToken[1]));
					nameList.put(wordToken[2].toString(), wordToken[0].toString());
					//						counter++;
				}

			}
			dataFile.close();
		}
		catch (ArrayIndexOutOfBoundsException e){
			supplyList.addDrug(new FormularyDrug(wordToken[0],"N/A", wordToken[1]));
			//			counter2++;
			parseFormulary();
		}	
		//		System.out.println(counter);
		//		System.out.println(counter2);
		//		System.out.println(nameList.getNameListSize()+supplyList.getDrugListSize()+counter);
	}

	public void parseExcluded() throws IOException{
		//		String[] wordToken;

		//formulary csv:[generic name, strength, brand name]
		try {
			dataLine = dataFile.readLine(); //ignore title line

			//excluded csv:[generic name, brand name]
			while ((dataLine = dataFile.readLine()) != null) {
				wordToken = dataLine.split(splitBy);
				supplyList.addDrug(new ExcludedDrug(wordToken[0], wordToken[1]));
			}

			dataFile.close();
		}
		catch (ArrayIndexOutOfBoundsException e){
			dataFile.close();
		}	

	}

	public DrugList parseRestricted() throws IOException{
		//		String[] wordToken;

		//formulary csv:[generic name, strength, brand name]
		try {
			dataLine = dataFile.readLine(); //ignore title line

			//excluded csv:[generic name, brand name]
			while ((dataLine = dataFile.readLine()) != null) {
				wordToken = dataLine.split(splitBy);
				supplyList.addDrug(new RestrictedDrug(wordToken[0], wordToken[1], wordToken[2]));
				//TODO
			}

			dataFile.close();
		}
		catch (ArrayIndexOutOfBoundsException e){
			dataFile.close();
			return supplyList;
		}	

		return supplyList;
	}

	//	public DrugList parserData() throws IOException {
	//		//TODO write better exception
	//		//TODO what to do when csv runs across blank line?
	//		//TODO handle with try catches?
	//		try {
	//			formularyLine = formularFile.readLine();
	//			excludedLine = excludedFile.readLine();
	//			restrictedLine = restrictedFile.readLine();//Does not read title line
	//		} catch (IOException e) {
	//			System.out.println("Can not read files"+ e.getMessage());
	//			e.printStackTrace();
	//		}
	//
	//		String[] wordToken;
	//
	//		//formulary csv:[generic name, strength, brand name]
	//		//TODO figure out how to split formulary with non-formulary
	//		//TODO for now, ALL drugs are formulary
	//		try {
	//			while ((formularyLine = formularFile.readLine()) != null) {
	//				wordToken = formularyLine.split(splitBy);
	//				//if the drug is already found in the list, add strength to formulary
	//				if(supplyList.containsGenericName(wordToken[0])){
	//					((FormularyDrug)supplyList.getDrug(wordToken[0])).addStrength(wordToken[1]);
	//				}
	//				else {
	//					supplyList.addDrug(new FormularyDrug(wordToken[0], wordToken[2], wordToken[1]));
	//					nameList.put(wordToken[2].toString(), wordToken[0].toString());
	//				}
	//			}
	//
	//
	//			//excluded csv:[generic name, brand name]
	//			while ((excludedLine = excludedFile.readLine()) != null) {
	//				wordToken = excludedLine.split(splitBy);
	//				supplyList.addDrug(new ExcludedDrug(wordToken[0], wordToken[1]));
	//				System.out.println(wordToken[0]+" "+wordToken[1]+" "+wordToken[2]);
	//				//TODO
	//			}
	//
	//			//restricted csv:[generic name, brand name, restriction criteria]
	//			while ((restrictedLine = restrictedFile.readLine()) != null) {
	//				wordToken = restrictedLine.split(splitBy);
	//				supplyList.addDrug(new RestrictedDrug(wordToken[0], wordToken[1], wordToken[2]));
	//				//TODO
	//			}
	//			formularFile.close();
	//		}
	//		catch (IOException e) {
	//			System.out.println("Could not print"+ e.getMessage());
	//			e.printStackTrace();
	//		}
	//		catch (ArrayIndexOutOfBoundsException e){
	//			formularFile.close();
	//			return null;
	//		}
	//
	//		return null;
	//	}

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