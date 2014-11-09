package com.lowermainlandpharmacyservices.lmpsformulary;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;

public class MainActivity extends Activity {

	public final static String EXTRA_INFO = "com.lowermainlandpharmacyservices.MainActivity.SEARCHINPUT";
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
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
        Intent searchResult = new Intent(this, DisplayFormularyResult.class);
        EditText editText = (EditText) findViewById(R.id.search_input);
        String searchInput = editText.getText().toString();
//Kelvin's recommendation---------------------------------------
        /*CSVParser parser = new CSVParser();
        DrugList supplyList = parser.getSupplyList();
        NameList nameList = parser.getNameList();
        if (supplyList.containsGenericName(searchInput)) {
            searchResult.putExtra(EXTRA_INFO, supplyList.getDrug(searchInput));
            startActivity(searchResult);
        } else if (nameList.containsBrandName(searchInput)) {
            searchResult.putExtra(EXTRA_INFO,
                    supplyList.getDrug(nameList.getGenericName(searchInput)));
            startActivity(searchResult);
        } else {
            Toast.makeText(this, searchInput + " is non-Formulary or typo has been made",
                            Toast.LENGTH_LONG).show();
        }*/
        searchResult.putExtra(EXTRA_INFO, searchInput);
        startActivity(searchResult);


//    public Drug searchDrug(String input) throws Exception {
//        CSVParser parser = new CSVParser();
//        DrugList supplyList = parser.getSupplyList();
//        NameList nameList = parser.getNameList();
//        if (parser.inSystem(input)) {
//            if (supplyList.containsGenericName(input)) {
//                return supplyList.getDrug(input);
//            } else if (nameList.containsBrandName(input)) {
//                return supplyList.getDrug(nameList.getGenericName(input));
//            }
//        } else {
//            return null;
//        }
//
//    }
    }
}