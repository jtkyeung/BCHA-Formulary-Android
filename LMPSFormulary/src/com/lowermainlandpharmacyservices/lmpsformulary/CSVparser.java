package com.lowermainlandpharmacyservices.lmpsformulary;

import java.io.BufferedReader;
import java.io.FileReader;


public class CSVparser {

	 BufferedReader formularFile;
	    BufferedReader excludedFile;
	    BufferedReader restrictedFile;

	    String splitBy = ",";

	    String formularyLine;
	    String excludedLine;
	    String restrictedLine;

	    DrugList supplyList;
	    NameList nameList;

	    public CSVparser() throws Exception {
	        formularFile = new BufferedReader(new FileReader("formulary.csv"));
	        excludedFile = new BufferedReader(new FileReader("excluded.csv"));
	        restrictedFile = new BufferedReader(new FileReader("restricted.csv"));
	        //TODO add the actual csv files (may need to refactor names once included)
	        supplyList = new DrugList();
	        nameList = new NameList();
	    }

	    public DrugList parserData() throws Exception {
	        //TODO write better exception
	        //TODO what to do when csv runs across blank line?
	        //TODO handle with try catches?
	        formularyLine = formularFile.readLine();
	        excludedLine = excludedFile.readLine();
	        restrictedLine = restrictedFile.readLine();//Does not read title line
	        String[] wordToken;

	        //formulary csv:[generic name, strength, brand name]
	        //TODO figure out how to split formulary with non-formulary
	        //TODO for now, ALL drugs are formulary
	        while ((formularyLine = formularFile.readLine()) != null) {
	            wordToken = formularyLine.split(splitBy);

	            //if the drug is already found in the list, add strength to formulary
	            if(supplyList.containsGenericName(wordToken[0])){
	                ((FormularyDrug)supplyList.getDrug(wordToken[0])).addStrength(wordToken[1]);
	            }
	            else {
	                supplyList.addDrug(new FormularyDrug(wordToken[0], wordToken[2], wordToken[1]));
	                nameList.put(wordToken[2].toString(), wordToken[0].toString());
	            }
	        }

	            //excluded csv:[generic name, brand name]
	        while ((excludedLine = excludedFile.readLine()) != null) {
	            wordToken = excludedLine.split(splitBy);
	            supplyList.addDrug(new ExcludedDrug(wordToken[0], wordToken[1]));
	            //TODO
	        }

	            //restricted csv:[generic name, brand name, restriction criteria]
	        while ((restrictedLine = restrictedFile.readLine()) != null) {
	            wordToken = restrictedLine.split(splitBy);
	            supplyList.addDrug(new RestrictedDrug(wordToken[0], wordToken[1], wordToken[2]));
	            //TODO
	        }

	        formularFile.close();
	        return supplyList;
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