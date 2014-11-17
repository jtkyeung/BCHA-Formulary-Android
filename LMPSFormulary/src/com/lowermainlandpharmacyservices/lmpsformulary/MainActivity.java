package com.lowermainlandpharmacyservices.lmpsformulary;

import android.app.Activity;
import android.content.Intent;
import android.content.res.AssetManager;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

public class MainActivity extends Activity {

	public final static String EXTRA_INFO = "com.lowermainlandpharmacyservices.MainActivity.SEARCHINPUT";
	public final static String EXTRA_STRENGTHS = "com.lowermainlandpharmacyservices.MainActivity.STRENGTHS";
	public final static String EXTRA_GENERICNAME = "com.lowermainlandpharmacyservices.MainActivity.GENERICNAME";
	public final static String EXTRA_BRANDNAME = "com.lowermainlandpharmacyservices.MainActivity.BRANDNAME";

	public AssetManager assetManager;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		getActionBar().hide();

		assetManager = getAssets();
	}



	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}



	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	public void displayResult(View view) throws Exception {

		EditText editText = (EditText) findViewById(R.id.search_input);
		String searchInput = editText.getText().toString().toUpperCase();
		//Kelvin's changes begin (below)---------------------------------------
		CSVparser masterList = new CSVparser();
		masterList.parseFormulary(assetManager.open("formulary.csv"));
		masterList.parseExcluded(assetManager.open("excluded.csv"));
		masterList.parseRestricted(assetManager.open("restricted.csv"));
		//Kelvin's changes end-------------------------------------------------

		DrugList drugList = masterList.getSupplyList();
		NameList nameList = masterList.getNameList();
		Drug drug = null;

		if(drugList.containsGenericName(searchInput)){
			drug = drugList.getDrug(searchInput);
		} else if (nameList.containsBrandName(searchInput)){
			String genericName = nameList.getGenericName(searchInput);
			drug = drugList.getDrug(genericName);
		} else {
			Toast toast = Toast.makeText(getApplicationContext(), "Drug " + "(" + searchInput + ")" + " Not Found", Toast.LENGTH_SHORT);
			toast.show();
		}
		
		if (drug != null) {
			if (drug.getStatus() == "Formulary") { 
				Intent formularyResult = new Intent(this, DisplayFormularyResult.class);
				FormularyDrug fdrug = (FormularyDrug) drug;
				formularyResult.putExtra(EXTRA_GENERICNAME, fdrug.getGenericName());
				formularyResult.putExtra(EXTRA_BRANDNAME, fdrug.getBrandName());
				formularyResult.putStringArrayListExtra(EXTRA_STRENGTHS, fdrug.getStrengths());
				formularyResult.putExtra(EXTRA_INFO, searchInput);
				startActivity(formularyResult);
	
			} else if (drug.getStatus() == "Restricted") {
				Intent restrictedResult = new Intent(this, DisplayRestrictedResult.class);
				restrictedResult.putExtra(EXTRA_BRANDNAME, drug.getBrandName());
				startActivity(restrictedResult);
	
			} else if (drug.getStatus() == "Excluded") {
				Intent excludedResult = new Intent(this, DisplayExcludedResult.class);
				ExcludedDrug edrug = (ExcludedDrug) drug;
				excludedResult.putExtra(EXTRA_GENERICNAME, edrug.getGenericName());
				excludedResult.putExtra(EXTRA_BRANDNAME, edrug.getBrandName());
				startActivity(excludedResult);
			}
		} 
		
	}

	//  public Drug searchDrug(String input) throws Exception {
	//      CSVParser parser = new CSVParser();
	//      DrugList supplyList = parser.getSupplyList();
	//      NameList nameList = parser.getNameList();
	//      if (parser.inSystem(input)) {
	//          if (supplyList.containsGenericName(input)) {
	//              return supplyList.getDrug(input);
	//          } else if (nameList.containsBrandName(input)) {
	//              return supplyList.getDrug(nameList.getGenericName(input));
	//          }
	//      } else {
	//          return null;
	//      }
	//
	//  }
}
