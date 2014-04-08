package com.esteniek.treasurely_android;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONObject;

import android.app.Activity;
import android.app.Fragment;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.Toast;

import com.esteniek.treasurely_android.services.LocationService;
import com.esteniek.treasurely_android.services.RESTService;

/**
 * A fragment representing a list of Items.
 * <p />
 * Large screen devices (such as tablets) are supported by replacing the
 * ListView with a GridView.
 * <p />
 * Activities containing this fragment MUST implement the {@link Callbacks}
 * interface.
 */
public class TreasuresFoundFragment extends Fragment {
	
	private ArrayList<Treasure> treasures;
	
	private OnItemSelectedListener listener;
	private LocationService location;
	private RESTService rest;
	private TreasureAdapter adapter;

	public interface OnItemSelectedListener {
		public void onItemSelected(Treasure treasure);
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		treasures = new ArrayList<Treasure>();
		// Start location service
		location = new LocationService(getActivity());
		// Start rest service
		rest = new RESTService(getActivity());
		// Search for treasure
		findTreasure();
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		View view = inflater
				.inflate(R.layout.fragment_treasuresfoundfragment_list,
						container, false);
		final ListView listView = (ListView) view
				.findViewById(R.id.treasure_list);
		setListViewAdapter(listView);
		listView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1,
					int position, long arg3) {
				Treasure treasure = (Treasure) arg0.getItemAtPosition(position);
				listener.onItemSelected(treasure);
			}
		});
		return view;

	}

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		if (activity instanceof OnItemSelectedListener) {
			listener = (OnItemSelectedListener) activity;
		} else {
			throw new ClassCastException(
					activity.toString()
							+ " must implement TreasuresFoundFragment.OnItemSelectedListener");
		}
	}

	@Override
	public void onDetach() {
		super.onDetach();
		listener = null;
	}

	private void setListViewAdapter(ListView listView) {

		if (treasures != null) {
			adapter = new TreasureAdapter(this.getActivity(),
					R.layout.treasure_found_item, treasures);

			listView.setAdapter(adapter);
		}
	}

	private void findTreasure() {

		String url = getResources().getString(R.string.baseUrl) + "/treasures/"
				+ location.getLatitude() + "/" + location.getLongitude();
		String testurl = "http://treasurely.no-ip.org:7000/treasures/37.334476/-122.039740";
		//System.out.println("findTreasure url: " + url);
		new FindTreasureTask().execute(testurl);
	}

	/**
	 * Find treasures
	 * 
	 * @author st
	 * 
	 */
	private class FindTreasureTask extends AsyncTask<String, Void, String> {

		@Override
		protected String doInBackground(String... params) {

			return rest.findTreasures(params[0]);
		}

		// onPostExecute displays the results of the AsyncTask.
		@Override
		protected void onPostExecute(String result) {
			Toast.makeText(getActivity(), "Received!", Toast.LENGTH_LONG)
					.show();
			System.out.println(result);
			try {
				setResult(result);
			} catch (Exception e) {
				e.printStackTrace();
			}
			if (!treasures.isEmpty()) {
				adapter.notifyDataSetChanged();
			}
		}
	}
	
	private void setResult(String result) throws Exception {
		
		String title = "";
		String text = "";
		String media = "";
		JSONArray array = new JSONArray(result);
		System.out.println(array.length());
		for (int i = 0; i < array.length(); i++) {
		    JSONObject row = array.getJSONObject(i);
		    title = row.getString("title");
		    text = row.getString("text");
		    media = row.getString("media");
		    if(media != null) {
		    	treasures.add(new Treasure(title, text, media));
		    } else {
		    	treasures.add(new Treasure(title, text));
		    }
		}
	}
}
