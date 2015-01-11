package com.lowermainlandpharmacyservices.lmpsformulary;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.AssetManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.Toast;

public class MainActivity extends Activity {

	public final static String EXTRA_INFO = "com.lowermainlandpharmacyservices.MainActivity.SEARCHINPUT";
	public final static String EXTRA_STRENGTHS = "com.lowermainlandpharmacyservices.MainActivity.STRENGTHS";
	public final static String EXTRA_GENERICNAME = "com.lowermainlandpharmacyservices.MainActivity.GENERICNAME";
	public final static String EXTRA_BRANDNAME = "com.lowermainlandpharmacyservices.MainActivity.BRANDNAME";
	public final static String EXTRA_RESTRICTIONS = "com.lowermainlandpharmacyservices.MainActivity.RESTRICTIONS";
	public final static String EXTRA_EXCLUDED_REASON = "com.lowermainlandpharmacyservices.MainActivity.EXCLUDED_REASON";
	public final static String EXTRA_TYPE = "com.lowermainlandpharmacyservices.MainActivity.TYPE";

	// declare the dialog as a member field of your activity
	ProgressDialog mProgressDialog;
	GenericDrugList genericList;
	BrandDrugList brandList;
	SharedPreferences settings;
	SharedPreferences.Editor editor;

	CSVparser masterList = null;
	public AssetManager assetManager;
	private boolean isConnected;
	AutoCompleteTextView autocompletetextview;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		getActionBar().hide();

		assetManager = getAssets();
		FileInputStream fis = null;
		String currVersion;
		BufferedReader reader;
		String line;
		//checks if files need update
		DownloadTask fileVersion = new DownloadTask(MainActivity.this, "fileVersion.txt");
		settings = getApplicationContext().getSharedPreferences("foo", 0);
		editor = settings.edit();
		try {
			if(settings.contains("filesDownloaded")) {
				fis = openFileInput("fileVersion.txt");
				reader = new BufferedReader(new InputStreamReader(fis));
				line = reader.readLine();
				currVersion = line;
				System.out.println("current version is " + line);
				fis.close();
			}else {
				currVersion = "";
			}
			fileVersion.execute("https://www.dropbox.com/s/4cvo08xnmlg7qr6/update.txt?dl=1").get(); //get() waits for a return

			fis = openFileInput("fileVersion.txt");
			reader = new BufferedReader(new InputStreamReader(fis));
			line = reader.readLine();
			String newVersion =line;
			System.out.println("currVersion is "+ currVersion + " newVersion is " + newVersion);

			if(!(currVersion.equals(newVersion))){
				System.out.println("We need an update!");
				Toast.makeText(this, "File update in progress", Toast.LENGTH_LONG).show();

				ConnectivityManager cm =
						(ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);

				NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
				isConnected = activeNetwork != null && activeNetwork.isConnectedOrConnecting();
				//if network is on	
				if(isConnected){
					// execute this when the downloader must be fired
					final DownloadTask downloadFormulary = new DownloadTask(MainActivity.this, "formularyUpdated.csv");
					downloadFormulary.execute("https://www.dropbox.com/sh/ctdjnxoemlx9hbr/AABotiW6CP_-JrGAh0mw1nkma/formulary.csv?dl=1").get();
					final DownloadTask downloadExcluded = new DownloadTask(MainActivity.this, "excludedUpdated.csv");
					downloadExcluded.execute("https://www.dropbox.com/s/lj6ucd9o7u1og3k/excluded.csv?dl=1").get(); //local
					//					downloadExcluded.execute("https://www.dropbox.com/sh/ctdjnxoemlx9hbr/AAAh2jkw2watr9KpopeH_JUsa/excluded.csv?dl=1").get();
					final DownloadTask downloadRestricted = new DownloadTask(MainActivity.this, "restrictedUpdated.csv");
					downloadRestricted.execute("https://www.dropbox.com/s/n4so74xl4n7wbhy/restricted.csv?dl=1").get();
					//						https://www.dropbox.com/sh/ctdjnxoemlx9hbr/AACa_xqMx2PZWMoWKe5tJoRda/restricted.csv?dl=1
					// We need an Editor object to make preference changes.
					// All objects are from android.context.Context
					editor.putBoolean("filesDownloaded", true);
					editor.commit();
					Toast.makeText(this, "Update completed", Toast.LENGTH_LONG).show();
				}
				else{ //if network is off
					Toast.makeText(this, "A version update is available, please connect to wi-fi "
							+ "and restart to app to update", Toast.LENGTH_LONG).show();
				}
			}
		}
		catch (IOException e) {
			e.printStackTrace();
		} 
		catch (Exception e){
			currVersion = "-2"; //TODO change later
		}
		finally {
			try {
				if (fis != null)
					fis.close();
			} catch (IOException ex) {
				ex.printStackTrace();
			}
			//delete after
			editor.putBoolean("toParse", true);
			editor.commit();
		}

		//parsing begins----------------------------------------------------------------------
		if (settings.getBoolean("toParse", true)) {
			masterList = new CSVparser();
			System.out.println("initparser");
			try{
				if (settings.getBoolean("filesDownloaded", false)){
					System.out.println("parser from updated files");
					masterList.parseFormulary(openFileInput("formularyUpdated.csv"));
					System.out.println("formularyparsed");
					masterList.parseExcluded(openFileInput("excludedUpdated.csv"));
					System.out.println("excludedparsed");
					masterList.parseRestricted(openFileInput("restrictedUpdated.csv"));
					System.out.println("parsingdidntbreak");
				}
				else{
					System.out.println("parser from default files");
					masterList.parseFormulary(assetManager.open("formulary.csv"));
					System.out.println("formularyparsed");
					masterList.parseExcluded(assetManager.open("excluded.csv"));
					System.out.println("excludedparsed");
					masterList.parseRestricted(assetManager.open("restricted.csv"));
				}
				System.out.println("parsingdidntbreak");
			}
			catch (FileNotFoundException e){
				Toast.makeText(this, "An error has caused this app to malfunction. "
						+ "Please ensure there is enough memory on the phone, network is "
						+ "present or re-install the app", Toast.LENGTH_LONG).show();
				e.printStackTrace();
			} catch (IOException e) {
				Toast.makeText(this, "An error has caused this app to malfunction. "
						+ "Please ensure there is enough memory on the phone, network is "
						+ "present or re-install the app", Toast.LENGTH_LONG).show();
				e.printStackTrace();
			}

			genericList = masterList.getListByGeneric();
			brandList = masterList.getListByBrand();
			//parsing ends----------------------------------------------
			//predictive text-------------------------------------------

			//make master nameList
			ArrayList<String> masterDrugNameList = genericList.getGenericNameList(); //add all the generic names
			ArrayList<String> brandNameList = brandList.getBrandNameList();
			for(String brandName:brandNameList){
				if(!(masterDrugNameList.contains(brandName))){ //only add brand names if they don't already appear
					masterDrugNameList.add(brandName);
				}
			}
			Collections.sort(masterDrugNameList); //sort the arraylist of names alphabetically

			autocompletetextview = (AutoCompleteTextView) findViewById(R.id.search_input);

			ArrayAdapter<String> adapter = new ArrayAdapter<String> (this,android.R.layout.select_dialog_item, masterDrugNameList);
			autocompletetextview.setThreshold(1);
			autocompletetextview.setAdapter(adapter);	
			//predictive text end---------------------------------------

			System.out.println("madelists");
			editor.putBoolean("toParse", false);
			// Commit the edits!
			editor.commit();
		}
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
		String searchInput = editText.getText().toString().toUpperCase().trim();
		Drug drug = null;
		String type = null;

		if(genericList.containsGenericName(searchInput)){
			drug = genericList.getGenericDrug(searchInput);
			type = "Generic";
			System.out.println("checkdrug");

			if (drug.getStatus() == "Formulary") { 
				Intent formularyResult = new Intent(this, DisplayFormularyResult.class);
				GenericFormularyDrug fdrug = (GenericFormularyDrug) drug;
				formularyResult.putExtra(EXTRA_GENERICNAME, fdrug.getGenericName());
				formularyResult.putStringArrayListExtra(EXTRA_BRANDNAME, fdrug.getBrandNames());
				formularyResult.putStringArrayListExtra(EXTRA_STRENGTHS, fdrug.getStrengths());
				formularyResult.putExtra(EXTRA_TYPE, type);
				startActivity(formularyResult);
				System.out.println("startedactivity");

			} else if (drug.getStatus() == "Restricted") {
				Intent restrictedResult = new Intent(this, DisplayRestrictedResult.class);
				GenericRestrictedDrug rdrug = (GenericRestrictedDrug) drug;
				restrictedResult.putExtra(EXTRA_GENERICNAME, rdrug.getGenericName());
				restrictedResult.putStringArrayListExtra(EXTRA_BRANDNAME, rdrug.getBrandNames());
				restrictedResult.putExtra(EXTRA_RESTRICTIONS, rdrug.getCriteria());
				restrictedResult.putExtra(EXTRA_TYPE, type);
				startActivity(restrictedResult);

			} else if (drug.getStatus() == "Excluded") {
				Intent excludedResult = new Intent(this, DisplayExcludedResult.class);
				GenericExcludedDrug edrug = (GenericExcludedDrug) drug;
				excludedResult.putExtra(EXTRA_GENERICNAME, edrug.getGenericName());
				excludedResult.putStringArrayListExtra(EXTRA_BRANDNAME, edrug.getBrandNames());
				excludedResult.putExtra(EXTRA_EXCLUDED_REASON, edrug.getCriteria());
				excludedResult.putExtra(EXTRA_TYPE, type);
				startActivity(excludedResult);
			}

		} else if (brandList.containsBrandName(searchInput)){
			drug = brandList.getBrandDrug(searchInput);
			type = "Brand";

			if (drug.getStatus() == "Formulary") { 
				Intent formularyResult = new Intent(this, DisplayFormularyResult.class);
				BrandFormularyDrug fdrug = (BrandFormularyDrug) drug;
				formularyResult.putExtra(EXTRA_GENERICNAME, fdrug.getGenericNames());
				formularyResult.putExtra(EXTRA_BRANDNAME, fdrug.getBrandName());
				formularyResult.putStringArrayListExtra(EXTRA_STRENGTHS, fdrug.getStrengths());
				formularyResult.putExtra(EXTRA_TYPE, type);
				startActivity(formularyResult);

			} else if (drug.getStatus() == "Restricted") {
				Intent restrictedResult = new Intent(this, DisplayRestrictedResult.class);
				BrandRestrictedDrug rdrug = (BrandRestrictedDrug) drug;
				restrictedResult.putStringArrayListExtra(EXTRA_GENERICNAME, rdrug.getGenericNames());
				restrictedResult.putExtra(EXTRA_BRANDNAME, rdrug.getBrandName());
				restrictedResult.putExtra(EXTRA_RESTRICTIONS, rdrug.getCriteria());
				restrictedResult.putExtra(EXTRA_TYPE, type);
				startActivity(restrictedResult);

			} else if (drug.getStatus() == "Excluded") {
				Intent excludedResult = new Intent(this, DisplayExcludedResult.class);
				BrandExcludedDrug edrug = (BrandExcludedDrug) drug;
				excludedResult.putStringArrayListExtra(EXTRA_GENERICNAME, edrug.getGenericNames());
				excludedResult.putExtra(EXTRA_BRANDNAME, edrug.getBrandName());
				excludedResult.putExtra(EXTRA_EXCLUDED_REASON, edrug.getCriteria());
				excludedResult.putExtra(EXTRA_TYPE, type);
				startActivity(excludedResult);
			}

		} else {
			Intent otherResult = new Intent(this, DisplayOtherResult.class);
			otherResult.putExtra(EXTRA_INFO, searchInput);
			/*Toast toast = Toast.makeText(getApplicationContext(), "Drug " + "(" + searchInput + ")" + " Not Found", Toast.LENGTH_SHORT);
			toast.show();*/
			startActivity(otherResult);
		}

	}

	public void displayHelp(View view) throws Exception {
		Intent helpResult = new Intent(this, HelpActivity.class);
		startActivity(helpResult);
	}

}
