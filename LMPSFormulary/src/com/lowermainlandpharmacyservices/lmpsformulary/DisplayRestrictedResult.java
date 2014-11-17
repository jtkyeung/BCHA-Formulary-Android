package com.lowermainlandpharmacyservices.lmpsformulary;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

public class DisplayRestrictedResult extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_display_restricted_result);
		getActionBar().hide();
		
		Intent intent = getIntent();
		
		//Generic Name
	    String genericName = intent.getStringExtra(MainActivity.EXTRA_GENERICNAME);
		TextView genericNameTextView = (TextView) findViewById(R.id.restricted_drug_genericname);
	    genericNameTextView.setText(genericName);
	    genericNameTextView.setTypeface(null, Typeface.BOLD);
		
	    //Brand Names
	    String brandNames = "\t" + "\t" + intent.getStringExtra(MainActivity.EXTRA_BRANDNAME);
	    TextView brandNameTextView = (TextView) findViewById(R.id.restricted_brandnames);
	    brandNameTextView.setText(brandNames);
	    brandNameTextView.setTextSize(20);
	    
	    
	    //Status
	    TextView statusTextView = (TextView) findViewById(R.id.restricted);
	    statusTextView.setText("\t" + "\t" + "RESTRICTED");
	    statusTextView.setTextSize(20);
	    statusTextView.setTextColor(Color.parseColor("#CC0000"));
	    statusTextView.setTypeface(null, Typeface.BOLD);
	    
	    //Restriction Criteria
	    String criteria = intent.getStringExtra(MainActivity.EXTRA_RESTRICTIONS);
	    TextView criteriaTextView = (TextView) findViewById(R.id.restriction_criteria);
	    criteriaTextView.setText(criteria);
	    criteriaTextView.setTextSize(15);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.display_restricted_result, menu);
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
