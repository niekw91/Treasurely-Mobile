package com.esteniek.treasurely_android.services;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONObject;

import com.esteniek.treasurely_android.R;
import com.esteniek.treasurely_android.Treasure;

import android.app.Activity;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

public class RESTService {

	private Context _context;
	private String baseUrl = "http://treasurely.no-ip.org:7000";
	private Treasure[] treasures;

	/**
	 * Constructor
	 * 
	 * @param context
	 */
	public RESTService(Context context) {
		_context = context;
	}

	public Treasure[] getTreasures() {

		return treasures;
	}

	/**
	 * Get treasures
	 * 
	 * @param lat
	 * @param lng
	 */
	public void findTreasures(double lat, double lng) {
		if (isConnected()) {
			String url = _context.getResources().getString(R.string.baseUrl)
					+ "treasures/" + lat + "/" + lng;
			System.out.println("GET request to url: " + url);
			new HttpGETTask().execute(url);
		}
	}

	public String findTreasures(String url) {
		if (isConnected()) {
			return GET(url);
		}
		return "";
	}

	/**
	 * Drop Treasure
	 * 
	 * @param json
	 */
	public void dropTreasure(String json) {

		if (isConnected()) {
			new HttpPOSTTask().execute(baseUrl + "/treasure/", json);
		}
	}

	/**
	 * GET HTTP request
	 * 
	 * @param url
	 * @return
	 */
	public static String GET(String url) {

		InputStream inputStream = null;
		String result = "";
		try {

			// Create HttpClient
			HttpClient httpclient = new DefaultHttpClient();

			// Make GET request to the given URL
			HttpResponse httpResponse = httpclient.execute(new HttpGet(url));

			// receive response as inputStream
			inputStream = httpResponse.getEntity().getContent();

			// convert inputstream to string
			if (inputStream != null) {
				result = convertInputStreamToString(inputStream);
			} else {
				result = "Did not work!";
			}

		} catch (Exception e) {
			System.out.println("InputStream Exception: "
					+ e.getLocalizedMessage());
		}

		return result;
	}

	public String POST(String url, String json) {

		InputStream inputStream = null;
		String result = "";

		if (json != null) {
			try {

				// Create a new HttpClient and Post Header
				HttpClient httpclient = new DefaultHttpClient();

				// Make post request to url
				HttpPost httpPost = new HttpPost(url);

				// Set json to StringEntity
				StringEntity se = new StringEntity(json);

				// Set httpPost Entity
				httpPost.setEntity(se);

				// Set some headers to inform server about the type of the
				// content
				httpPost.setHeader("Accept", "application/json");
				httpPost.setHeader("Content-type", "application/json");

				// Execute POST request to the given URL
				HttpResponse httpResponse = httpclient.execute(httpPost);

				// Receive response as inputStream
				inputStream = httpResponse.getEntity().getContent();

				// Convert inputstream to string
				if (inputStream != null)
					result = convertInputStreamToString(inputStream);
				else
					result = "Did not work!";
			} catch (Exception e) {
				System.out.println("PostDataException: " + e);
			}
		}
		return result;
	}

	private static String convertInputStreamToString(InputStream inputStream)
			throws IOException {

		BufferedReader bufferedReader = new BufferedReader(
				new InputStreamReader(inputStream));
		String line = "";
		String result = "";
		while ((line = bufferedReader.readLine()) != null)
			result += line;

		inputStream.close();
		return result;

	}

	/**
	 * Asynchronous Http POST Request
	 * 
	 * @author st
	 * 
	 */
	private class HttpPOSTTask extends AsyncTask<String, Void, String> {

		@Override
		protected String doInBackground(String... params) {

			return POST(params[0], params[1]);
		}

		// onPostExecute displays the results of the AsyncTask.
		@Override
		protected void onPostExecute(String result) {
			System.out.println("HttpGETTask response: "+result);
		}
	}

	/**
	 * Asynchronous Http GET Request
	 * 
	 * @author st
	 * 
	 */
	private class HttpGETTask extends AsyncTask<String, Void, String> {

		@Override
		protected String doInBackground(String... params) {

			return GET(params[0]);
		}

		// onPostExecute displays the results of the AsyncTask.
		@Override
		protected void onPostExecute(String result) {
			System.out.println("HttpGETTask response: " + result);
		}
	}

	/**
	 * Check if device has internet connection
	 * 
	 * @return boolean
	 */
	public boolean isConnected() {

		ConnectivityManager connMgr = (ConnectivityManager) _context
				.getSystemService(Activity.CONNECTIVITY_SERVICE);
		NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
		if (networkInfo != null && networkInfo.isConnected())
			return true;
		else
			return false;
	}

}
