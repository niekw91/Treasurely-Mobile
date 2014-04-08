package com.esteniek.treasurely_android;

import services.LocationService;
import services.RESTService;
import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

public class DropTreasureActivity extends Activity implements
DropTreasureFragment.OnTreasureDroppedListener {

	private static LocationService location;
	private static String _title;
	private static String _text;
	private static String _media;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_drop_treasure);
		
		if (savedInstanceState == null) {
			getFragmentManager().beginTransaction()
					.add(R.id.container, new DropTreasureFragment()).commit();
		}
		// Start location service
    	location = new LocationService(getBaseContext());
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.drop_treasure, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
//		if (id == R.id.action_drop) {
//			dropTreasure();
//			return true;
//		}
		return super.onOptionsItemSelected(item);
	}
	
	/**
	 * Build JSON string for POST 
	 * @return
	 */
	public String buildJSON() {
		String json = "{";
		if (true) {
			json += "\"user_id\":\"5331d1968c5302080e841894\"";
		}
		if (true) {
			if (json != "{") {json += ",";}
			json += "\"latitude\":" + location.getLatitude() ;
		}
		if (true) {
			if (json != "{") {json += ",";}
			json += "\"longitude\":" + location.getLongitude();
		}
		if (_text != null) {
			if (json != "{") {json += ",";}
			json += "\"text\":" + _text;
		}
		if (_title != null) {
			if (json != "{") {json += ",";}
			json += "\"title\":" + _title;
		}
		if (_media != null) {
			if (json != "{") {json += ",";}
			json += "\"title\":" + _media;
		}
		json += "}";
		return json;
	}

	@Override
	public void onTreasureDropped(String title, String text, String media) {
		_title = title;
		_text = text;
		_media = media;
		dropTreasure();
	}
	
	public void dropTreasure() {
		
		RESTService rest = new RESTService(getBaseContext());
		//rest.dropTreasure(buildJSON());
		System.out.println(buildJSON());
	}

}
