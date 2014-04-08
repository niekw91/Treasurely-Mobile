package com.esteniek.treasurely_android;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import services.LocationService;
import services.RESTService;
import android.app.Activity;
import android.app.Fragment;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

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
		rest = new RESTService(getActivity());
		//findTreasure();
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
							+ " must implemenet TreasuresFoundFragment.OnItemSelectedListener");
		}
	}

	@Override
	public void onDetach() {
		super.onDetach();
		listener = null;
	}

	private void setListViewAdapter(ListView listView) {

		adapter = new TreasureAdapter(this.getActivity(),
				R.layout.treasure_found_item, treasures);
		if (treasures != null) {
		listView.setAdapter(adapter);
		}
	}

	private void findTreasure() {

		new FindTreasureTask().execute(getResources().getString(R.string.baseUrl) + "/treasures/"
				+ location.getLatitude() + "/" + location.getLongitude());
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

			return rest.POST(params[0], params[1]);
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
			//adapter.notifyDataSetChanged();
		}
	}
	
	private void setResult(String result) throws Exception {
		
		JSONArray array = new JSONArray(result);
		for (int i = 0; i < array.length(); i++) {
		    JSONObject row = array.getJSONObject(i);
		    String title = row.getString("title");
		    String text = row.getString("text");
		    String media = row.getString("media");
		    if(media != null) {
		    	treasures.set(i, new Treasure(title, text, media));
		    } else {
		    	treasures.set(i, new Treasure(title, text));
		    }
		}
		
	}

}
