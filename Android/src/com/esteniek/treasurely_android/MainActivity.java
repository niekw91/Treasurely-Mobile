package com.esteniek.treasurely_android;

import services.LocationService;
import services.RESTService;
import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;
import android.os.Build;

public class MainActivity extends Activity {

	private TextView treasuresFound;
	private SharedPreferences prefs;
	private static LocationService location;

	@Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    	treasuresFound = (TextView) findViewById(R.id.treasures_found);

    	//treasuresFound.setText("Treasure #1");
        if (savedInstanceState == null) {
        	init();
        }
    }
	/**
	 * Load treasures
	 */
    private void loadTreasures() {
		//RESTService rest = new RESTService(getBaseContext());
		//rest.getTreasures(location.getLatitude(), location.getLongitude());
    	
    	Toast.makeText(this, "Treasures loaded",
			   Toast.LENGTH_LONG).show();
	}
	@Override
    public boolean onCreateOptionsMenu(Menu menu) {
        
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()) {
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

    /**
     * Treasures fragment containing treasures.
     */
    public static class TreasuresFragment extends Fragment {

        public TreasuresFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                Bundle savedInstanceState) {
        	View rootView = inflater.inflate(R.layout.fragment_main, container, false);
        	
            return rootView;
        }
    }
    
    /**
     * Initialize app
     */
    public void init() {
    	
    	// Check if user is already logged in
    	if(alreadyLoggedIn()) {
    		// Load Main Activity Fragment
	        getFragmentManager().beginTransaction()
	        .add(R.id.container, new TreasuresFragment())
	        .commit();
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
}
