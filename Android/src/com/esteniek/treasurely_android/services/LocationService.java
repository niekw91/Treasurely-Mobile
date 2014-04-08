package com.esteniek.treasurely_android.services;

import android.app.Activity;
import android.content.Context;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.widget.Toast;

public class LocationService implements LocationListener{

	private LocationManager locationManager;
	private String provider;
	private double _latitude;
	private double _longitude;
	private Context _context;
	
	public LocationService(Context context) {
		_context = context;
		init();
	}
	
	private void init() {
			
		// Get the location manager
	    locationManager = (LocationManager) _context.getSystemService(Context.LOCATION_SERVICE);
	    // Define the criteria how to select the location provider -> use
	    // default
	    Criteria criteria = new Criteria();
	    provider = locationManager.getBestProvider(criteria, false);
	    Location location = locationManager.getLastKnownLocation(provider);

	    // Initialize the location fields
	    if (location != null) {
	      System.out.println("Provider " + provider + " has been selected.");
	      onLocationChanged(location);
	    } else {
//	    	Toast.makeText(_context, "Location not available",
//	  			   Toast.LENGTH_LONG).show();
	    	System.out.println("Location not available");
	    }
		
	}
	
	/**
	 * Get latitude
	 * @return
	 */
	public double getLatitude() {
		
		return _latitude;
	}
	
	/**
	 * Get longitude
	 * @return
	 */
	public double getLongitude() {
		
		return _longitude;
	}
	
	public void requestUpdates(Activity activity) {
		locationManager.requestLocationUpdates(provider, 400, 1, (LocationListener) activity);
	}
	
	public void removeUpdates(Activity activity) {
		locationManager.removeUpdates((LocationListener) activity);
	}

	@Override
	public void onLocationChanged(Location location) {
		_latitude = (int) (location.getLatitude());
	    _longitude = (int) (location.getLongitude());
	}

	@Override
	public void onProviderDisabled(String provider) {
//		Toast.makeText(_context, "Provider disabled",
//	  			   Toast.LENGTH_LONG).show();
		System.out.println("Provider disabled");
	}

	@Override
	public void onProviderEnabled(String provider) {
//		Toast.makeText(_context, "Enabled new provider " + provider,
//		        Toast.LENGTH_SHORT).show();
		System.out.println("Enabled new provider " + provider);
	}

	@Override
	public void onStatusChanged(String provider, int status, Bundle extras) {
//		Toast.makeText(_context, "Provider status changed ",
//		        Toast.LENGTH_SHORT).show();
		System.out.println("Provider status changed");
	}
}
