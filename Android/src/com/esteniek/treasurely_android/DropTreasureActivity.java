package com.esteniek.treasurely_android;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONException;
import org.json.JSONObject;

import services.LocationService;
import services.RESTService;
import android.app.Activity;
import android.app.ActionBar;
import android.app.Fragment;
import android.content.Context;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import android.os.Build;

public class DropTreasureActivity extends Activity {

	private static LocationService location;
	private int lat;	
	private int lng;
	private Fragment frag;	
	private static EditText titleInput;
	private static EditText textInput;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_drop_treasure);
		//titleInput = (EditText) findViewById(R.id.drop_treasure_title);
		//textInput = (EditText) findViewById(R.id.drop_treasure_text);
		
		if (savedInstanceState == null) {
			getFragmentManager().beginTransaction()
					.add(R.id.container, new PlaceholderFragment()).commit();
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
		if (id == R.id.action_drop) {
			dropTreasure();
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
	
	public void dropTreasure() {
		RESTService rest = new RESTService(getBaseContext());
		//rest.dropTreasure(buildJSON());
		System.out.println(buildJSON());
		
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
//		//if (textInput.getText() != null) {
//			if (json != "{") {json += ",";}
//			json += "\"text\":" + textInput.getText().toString();
//		//}
//		//if (titleInput.getText() != null) {
//			if (json != "{") {json += ",";}
//			json += "\"title\":" + titleInput.getText().toString();
//		//}
		json += "\"text\":\"text\"";
		json += "\"title\":\"title\"";
		json += "}";
		return json;
	}

	/**
	 * A placeholder fragment containing a simple view.
	 */
	public static class PlaceholderFragment extends Fragment {

		public PlaceholderFragment() {
		}

		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container,
				Bundle savedInstanceState) {
			View rootView = inflater.inflate(R.layout.fragment_drop_treasure,
					container, false);
			return rootView;
		}
		
		public String getTreasureText() {
			return textInput.getText().toString();
		}
		
		public String getTreasureTitle() {
			return titleInput.getText().toString();
		}
	}

}
