package com.lowermainlandpharmacyservices.lmpsformulary;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;

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
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.Toast;

public class MainActivity extends Activity {

	// public final static String EXTRA_INFO =
	// "com.lowermainlandpharmacyservices.MainActivity.SEARCHINPUT";
	// public final static String EXTRA_STRENGTHS =
	// "com.lowermainlandpharmacyservices.MainActivity.STRENGTHS";
	// public final static String EXTRA_GENERICNAME =
	// "com.lowermainlandpharmacyservices.MainActivity.GENERICNAME";
	// public final static String EXTRA_BRANDNAME =
	// "com.lowermainlandpharmacyservices.MainActivity.BRANDNAME";
	// public final static String EXTRA_RESTRICTIONS =
	// "com.lowermainlandpharmacyservices.MainActivity.RESTRICTIONS";
	// public final static String EXTRA_EXCLUDED_REASON =
	// "com.lowermainlandpharmacyservices.MainActivity.EXCLUDED_REASON";
	// public final static String EXTRA_TYPE =
	// "com.lowermainlandpharmacyservices.MainActivity.TYPE";

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
		// checks if files need update
		DownloadTask fileVersion = new DownloadTask(MainActivity.this,
				"fileVersion.txt");
		settings = getApplicationContext().getSharedPreferences("foo", 0);
		editor = settings.edit();
		try {
			if (settings.contains("filesDownloaded")) {
				fis = openFileInput("fileVersion.txt");
				reader = new BufferedReader(new InputStreamReader(fis));
				line = reader.readLine();
				currVersion = line;
				System.out.println("current version is " + line);
				fis.close();
			} else {
				currVersion = "";
			}
			// fileVersion.execute("https://www.dropbox.com/s/4cvo08xnmlg7qr6/update.txt?dl=1").get();
			// //get() waits for a return
			fileVersion
					.execute(
							"https://www.dropbox.com/s/cyng7mv7xaxr2hc/update.txt?dl=1")
					.get();

			fis = openFileInput("fileVersion.txt");
			reader = new BufferedReader(new InputStreamReader(fis));
			line = reader.readLine();
			String newVersion = line;
			System.out.println("currVersion is " + currVersion
					+ " newVersion is " + newVersion);

			if (!(currVersion.equals(newVersion))) {
				System.out.println("We need an update!");
				Toast.makeText(this, "File update in progress",
						Toast.LENGTH_LONG).show();

				ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);

				NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
				isConnected = activeNetwork != null
						&& activeNetwork.isConnectedOrConnecting();
				// if network is on
				if (isConnected) {
					// execute when new updates are needed
					final DownloadTask downloadFormulary = new DownloadTask(
							MainActivity.this, "formularyUpdated.csv");
					// downloadFormulary.execute("https://www.dropbox.com/s/3qdfzzfeucp83nt/formulary.csv?dl=1").get();
					downloadFormulary
							.execute(
									"https://www.dropbox.com/s/uezse2mqq1mqx1w/formulary.csv?dl=1")
							.get();
					final DownloadTask downloadExcluded = new DownloadTask(
							MainActivity.this, "excludedUpdated.csv");
					// downloadExcluded.execute("https://www.dropbox.com/s/lj6ucd9o7u1og3k/excluded.csv?dl=1").get();
					downloadExcluded
							.execute(
									"https://www.dropbox.com/s/y1zt4yhmouc1yko/excluded.csv?dl=1")
							.get();
					final DownloadTask downloadRestricted = new DownloadTask(
							MainActivity.this, "restrictedUpdated.csv");
					// downloadRestricted.execute("https://www.dropbox.com/s/n4so74xl4n7wbhy/restricted.csv?dl=1").get();
					downloadRestricted
							.execute(
									"https://www.dropbox.com/s/khmb7l5yu1ysip1/restricted.csv?dl=1")
							.get();

					// We need an Editor object to make preference changes.
					// All objects are from android.context.Context
					editor.putBoolean("filesDownloaded", true);
					editor.commit();
					Toast.makeText(this, "Update completed", Toast.LENGTH_LONG)
							.show();
				} else { // if network is off
					Toast.makeText(
							this,
							"A version update is available, please connect to wi-fi "
									+ "and restart to app to update",
							Toast.LENGTH_LONG).show();
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		} catch (Exception e) {
			currVersion = "-2";
		} finally {
			try {
				if (fis != null)
					fis.close();
			} catch (IOException ex) {
				ex.printStackTrace();
			}
			// delete after
			editor.putBoolean("toParse", true);
			editor.commit();
		}

		// parsing
		// begins----------------------------------------------------------------------
		if (settings.getBoolean("toParse", true)) {
			masterList = new CSVparser();
			System.out.println("initparser");
			try {
				if (settings.getBoolean("filesDownloaded", false)) {
					System.out.println("parser from updated files");
					masterList
							.parseFormulary(openFileInput("formularyUpdated.csv"));
					System.out.println("formularyparsed");
					masterList
							.parseExcluded(openFileInput("excludedUpdated.csv"));
					System.out.println("excludedparsed");
					masterList
							.parseRestricted(openFileInput("restrictedUpdated.csv"));
				} else {
					System.out.println("parser from default files");
					masterList.parseFormulary(assetManager
							.open("formulary.csv"));
					System.out.println("formularyparsed");
					masterList.parseExcluded(assetManager.open("excluded.csv"));
					System.out.println("excludedparsed");
					masterList.parseRestricted(assetManager
							.open("restricted.csv"));
				}
				System.out.println("parsingdidntbreak");
			} catch (FileNotFoundException e) {
				Toast.makeText(
						this,
						"An error has caused this app to malfunction. "
								+ "Please ensure there is enough memory on the phone, network is "
								+ "present or re-install the app",
						Toast.LENGTH_LONG).show();
				e.printStackTrace();
			} catch (IOException e) {
				Toast.makeText(
						this,
						"An error has caused this app to malfunction. "
								+ "Please ensure there is enough memory on the phone, network is "
								+ "present or re-install the app",
						Toast.LENGTH_LONG).show();
				e.printStackTrace();
			}

			genericList = masterList.getListByGeneric();
			brandList = masterList.getListByBrand();
			// parsing ends----------------------------------------------
			// predictive text-------------------------------------------

			// make master nameList
			ArrayList<String> masterDrugNameList = genericList
					.getGenericNameList(); // add all the generic names
			ArrayList<String> brandNameList = brandList.getBrandNameList();

			for (String brandName : brandNameList) {
				if (!(masterDrugNameList.contains(brandName))) { // only add
																	// brand
																	// names if
																	// they
																	// don't
																	// already
																	// appear
					masterDrugNameList.add(brandName);
				}
			}
			Collections.sort(masterDrugNameList); // sort the arraylist of names
													// alphabetically

			autocompletetextview = (AutoCompleteTextView) findViewById(R.id.search_input);
			ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
					android.R.layout.select_dialog_item, masterDrugNameList);
			autocompletetextview.setThreshold(1);
			autocompletetextview.setAdapter(adapter);
			// predictive text end---------------------------------------

			// hide keyboard after selection
			autocompletetextview
					.setOnItemClickListener(new OnItemClickListener() {

						@Override
						public void onItemClick(AdapterView<?> arg0, View arg1,
								int arg2, long arg3) {
							InputMethodManager in = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
							in.toggleSoftInput(
									InputMethodManager.HIDE_IMPLICIT_ONLY, 0);
						}

					});

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

		if (genericList.containsGenericName(searchInput)) {
			drug = genericList.getGenericDrug(searchInput);
			type = "Generic";
			System.out.println("checkdrug");

			if (drug.getStatus().equals("Formulary")) {
				Intent formularyResult = new Intent(this,
						DisplayFormularyResult.class);
				GenericFormularyDrug fdrug = (GenericFormularyDrug) drug;
				formularyResult.putExtra(Utilities.EXTRA_GENERICNAME,
						fdrug.getGenericName());
				formularyResult.putStringArrayListExtra(
						Utilities.EXTRA_BRANDNAME, fdrug.getBrandNames());
				formularyResult.putStringArrayListExtra(
						Utilities.EXTRA_STRENGTHS, fdrug.getStrengths());
				formularyResult.putExtra(Utilities.EXTRA_TYPE, type);
				startActivity(formularyResult);
				System.out.println("startedactivity");

			} else if (drug.getStatus().equals("Restricted")) {
				Intent restrictedResult = new Intent(this,
						DisplayRestrictedResult.class);
				GenericRestrictedDrug rdrug = (GenericRestrictedDrug) drug;
				restrictedResult.putExtra(Utilities.EXTRA_GENERICNAME,
						rdrug.getGenericName());
				restrictedResult.putStringArrayListExtra(
						Utilities.EXTRA_BRANDNAME, rdrug.getBrandNames());
				restrictedResult.putExtra(Utilities.EXTRA_RESTRICTIONS,
						rdrug.getCriteria());
				restrictedResult.putExtra(Utilities.EXTRA_TYPE, type);
				startActivity(restrictedResult);

			} else if (drug.getStatus().equals("Excluded")) {
				Intent excludedResult = new Intent(this,
						DisplayExcludedResult.class);
				GenericExcludedDrug edrug = (GenericExcludedDrug) drug;
				excludedResult.putExtra(Utilities.EXTRA_GENERICNAME,
						edrug.getGenericName());
				excludedResult.putStringArrayListExtra(
						Utilities.EXTRA_BRANDNAME, edrug.getBrandNames());
				excludedResult.putExtra(Utilities.EXTRA_EXCLUDED_REASON,
						edrug.getCriteria());
				excludedResult.putExtra(Utilities.EXTRA_TYPE, type);
				startActivity(excludedResult);
			}

		} else if (brandList.containsBrandName(searchInput)) {
			drug = brandList.getBrandDrug(searchInput);
			type = "Brand";

			if (drug.getStatus().equals("Formulary")) {
				Intent formularyResult = new Intent(this,
						DisplayFormularyResult.class);
				BrandFormularyDrug fdrug = (BrandFormularyDrug) drug;
				formularyResult.putExtra(Utilities.EXTRA_GENERICNAME,
						fdrug.getGenericNames());
				formularyResult.putExtra(Utilities.EXTRA_BRANDNAME,
						fdrug.getBrandName());
				formularyResult.putStringArrayListExtra(
						Utilities.EXTRA_STRENGTHS, fdrug.getStrengths());
				formularyResult.putExtra(Utilities.EXTRA_TYPE, type);
				startActivity(formularyResult);

			} else if (drug.getStatus().equals("Restricted")) {
				Intent restrictedResult = new Intent(this,
						DisplayRestrictedResult.class);
				BrandRestrictedDrug rdrug = (BrandRestrictedDrug) drug;
				restrictedResult.putStringArrayListExtra(
						Utilities.EXTRA_GENERICNAME, rdrug.getGenericNames());
				restrictedResult.putExtra(Utilities.EXTRA_BRANDNAME,
						rdrug.getBrandName());
				restrictedResult.putExtra(Utilities.EXTRA_RESTRICTIONS,
						rdrug.getCriteria());
				restrictedResult.putExtra(Utilities.EXTRA_TYPE, type);
				startActivity(restrictedResult);

			} else if (drug.getStatus().equals("Excluded")) {
				Intent excludedResult = new Intent(this,
						DisplayExcludedResult.class);
				BrandExcludedDrug edrug = (BrandExcludedDrug) drug;
				excludedResult.putStringArrayListExtra(
						Utilities.EXTRA_GENERICNAME, edrug.getGenericNames());
				excludedResult.putExtra(Utilities.EXTRA_BRANDNAME,
						edrug.getBrandName());
				excludedResult.putExtra(Utilities.EXTRA_EXCLUDED_REASON,
						edrug.getCriteria());
				excludedResult.putExtra(Utilities.EXTRA_TYPE, type);
				startActivity(excludedResult);
			}

		} else {
			Intent otherResult = new Intent(this, DisplayOtherResult.class);
			otherResult.putExtra(Utilities.EXTRA_INFO, searchInput);
			startActivity(otherResult);
		}

	}

	public void displayHelp(View view) throws Exception {
		Intent helpResult = new Intent(this, HelpActivity.class);
		startActivity(helpResult);
	}

}
