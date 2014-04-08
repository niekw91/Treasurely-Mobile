package com.esteniek.treasurely_android;

import services.LocationService;
import services.RESTService;
import android.app.Activity;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

public class MainActivity extends Activity implements
		TreasuresFoundFragment.OnItemSelectedListener {

	private SharedPreferences prefs;
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
		loadTreasures();
	}

	/**
	 * Check if user is already logged in
	 * 
	 * @return
	 */
	public boolean alreadyLoggedIn() {
		boolean userIdPresent = true;

		// Get shared preferences
		prefs = getPreferences(MODE_PRIVATE);

		// Get userId
		String userId = prefs.getString("userId", null);

		// Check if userid is present
		if (userId != null) {
			userIdPresent = true;
		}

		return userIdPresent;
	}

	/**
	 * Load treasures
	 */
	private void loadTreasures() {
		
		RESTService rest = new RESTService(getBaseContext());
		// rest.getTreasures(location.getLatitude(), location.getLongitude());
		rest.findTreasures(-122.039740, 37.334476);
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
			loadTreasures();
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
