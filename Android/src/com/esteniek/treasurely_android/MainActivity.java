package com.esteniek.treasurely_android;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.Menu;
import android.view.MenuItem;

import com.esteniek.treasurely_android.services.LocationService;
import com.esteniek.treasurely_android.services.RESTService;

public class MainActivity extends Activity implements
		TreasuresFoundFragment.OnItemSelectedListener {

	private RESTService rest;
	private static LocationService location;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		if (savedInstanceState == null) {
			init();
		}
	}

	/**
	 * Initialize app
	 */
	public void init() {

		// Check if user is already logged in
		if (alreadyLoggedIn()) {
			// Load Main Activity Fragment
			getFragmentManager().beginTransaction()
					.add(R.id.container, new TreasuresFoundFragment()).commit();
		} else {
			// If not logged in launch Login Activity
			startActivity(new Intent(this, LoginActivity.class));
		}
		// Start location service
		location = new LocationService(getBaseContext());
		// Start rest service
		rest = new RESTService(getBaseContext());
		// Load treasures
		findTreasures();
	}

	/**
	 * Check if user is already logged in
	 * 
	 * @return
	 */
	public boolean alreadyLoggedIn() {
		boolean userIdPresent = false;

		// Get shared preferences
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getBaseContext());

		String token = prefs.getString("userId", "");
		if (token != "") {
			userIdPresent = true;
			System.out.println("Current token: " + prefs.getString("userId", ""));
		} 

		return userIdPresent;
	}

	private void logout() {

		// Get shared preferences
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
		
		// Clear prefs containing userkey
		prefs.edit().clear().commit();
		
		// Restart Main Activity
		startActivity(new Intent(this, MainActivity.class));
	}

	/**
	 * Load treasures
	 */
	private void findTreasures() {
		Boolean locationWorking = location.getLatitude() != 0.0 || location.getLongitude() != 0.0;
		if (locationWorking) {
			rest.findTreasures(location.getLatitude(), location.getLongitude());
		} else {
			// use hard coded coordinates
			rest.findTreasures(37.422006,-122.084095);
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
		
		switch (item.getItemId()) {
		case R.id.action_drop:
			// New Intent to launch a Drop Treasure Activity
			startActivity(new Intent(this, DropTreasureActivity.class));
			break;
		case R.id.action_refresh:
			// Reload treasures
			findTreasures();
			break;
		case R.id.action_logout:
			// Reload treasures
			logout();
			break;
		default:
			return super.onOptionsItemSelected(item);
		}

		return true;
	}

	@Override
	public void onItemSelected(Treasure treasure) {

		Intent intent = new Intent(getApplicationContext(),
				TreasureBoxActivity.class);
		intent.putExtra("image", treasure.media);
		intent.putExtra("title", treasure.title);
		intent.putExtra("text", treasure.text);
		startActivity(intent);
	}
}
