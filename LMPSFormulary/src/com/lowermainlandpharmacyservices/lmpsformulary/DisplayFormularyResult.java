package com.lowermainlandpharmacyservices.lmpsformulary;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

public class DisplayFormularyResult extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_display_formulary_result);
		Intent intent = getIntent();
		
		//Generic Name
	    String genericName = intent.getStringExtra(MainActivity.EXTRA_GENERICNAME);
		TextView genericNameTextView = (TextView) findViewById(R.id.formulary_drug_genericname);
	    genericNameTextView.setText(genericName);
	    genericNameTextView.setTypeface(null, Typeface.BOLD);
		
	    //Brand Names
	    String brandNames = "\t" + "\t" + intent.getStringExtra(MainActivity.EXTRA_BRANDNAME);
	    TextView brandNameTextView = (TextView) findViewById(R.id.formulary_brandnames);
	    brandNameTextView.setText(brandNames);
	    brandNameTextView.setTextSize(20);
	    
	    // Create strength text view
	    ArrayList<String> input = intent.getStringArrayListExtra(MainActivity.EXTRA_STRENGTHS);
	    String strengths = "";
	    for(String s: input) {
	    	 strengths += "\t" + "\t" + "- " + s + "\n";
	    }
	    
	    TextView strengthTextView = (TextView) findViewById(R.id.formulary_strength);
	    strengthTextView.setText(strengths);
	    strengthTextView.setTextSize(20);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.display_formulary_result, menu);
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
}
