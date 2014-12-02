package com.lowermainlandpharmacyservices.lmpsformulary;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
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
	public final static String EXTRA_RESTRICTIONS = "com.lowermainlandpharmacyservices.MainActivity.RESTRICTIONS";
	public final static String EXTRA_TYPE = "com.lowermainlandpharmacyservices.MainActivity.TYPE";

	// declare the dialog as a member field of your activity
	ProgressDialog mProgressDialog;


	public AssetManager assetManager;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		getActionBar().hide();

		assetManager = getAssets();
		
		// instantiate it within the onCreate method
		mProgressDialog = new ProgressDialog(MainActivity.this);
		mProgressDialog.setMessage("A message");
		mProgressDialog.setIndeterminate(true);
		mProgressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
		mProgressDialog.setCancelable(true);
		
		// execute this when the downloader must be fired
		final DownloadTask downloadTask = new DownloadTask(MainActivity.this);
		downloadTask.execute("https://www.dropbox.com/s/4bc4wgv9n8hhcy4/testFile.csv?dl=1");

		mProgressDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
			@Override
			public void onCancel(DialogInterface dialog) {
				downloadTask.cancel(true);
			}
		});
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

		//------------------------------------------------------------------
		EditText editText = (EditText) findViewById(R.id.search_input);
		String searchInput = editText.getText().toString().toUpperCase();
		//Kelvin's changes begin (below)---------------------------------------
		CSVparser masterList = new CSVparser();
		System.out.println("initparser");
		masterList.parseFormulary(assetManager.open("formulary.csv"));
		System.out.println("formularyparsed");
		masterList.parseExcluded(assetManager.open("excluded.csv"));
		System.out.println("excludedparsed");
		masterList.parseRestricted(assetManager.open("restricted.csv"));
		System.out.println("parsingdidntbreak");
		//Kelvin's changes end-------------------------------------------------

		GenericDrugList genericList = masterList.getListByGeneric();
		BrandDrugList brandList = masterList.getListByBrand();
		System.out.println("madelists");
		Drug drug = null;
		String type;

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
				excludedResult.putExtra(EXTRA_TYPE, type);
				startActivity(excludedResult);
			}

		} else {
			Toast toast = Toast.makeText(getApplicationContext(), "Drug " + "(" + searchInput + ")" + " Not Found", Toast.LENGTH_SHORT);
			toast.show();
		}

	}

}
