package com.esteniek.treasurely_android;

import android.app.Activity;
import android.app.Fragment;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

public class DropTreasureFragment extends Fragment {
	
	private static int RESULT_LOAD_IMAGE = 1;

	private OnTreasureDroppedListener mCallback;
	private EditText inputTitle;
	private EditText inputText;

	public interface OnTreasureDroppedListener {
		public void onTreasureDropped(String title, String text, String media);
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.setHasOptionsMenu(true);
	    // inputMedia
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.fragment_drop_treasure,
				container, false);
		return rootView;
	}
	
	@Override 
	public void onCreateOptionsMenu (Menu menu, MenuInflater inflater) {
	    super.onCreateOptionsMenu(menu, inflater);
	    menu.clear();
	    inflater.inflate(R.menu.drop_treasure, menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		
		switch (item.getItemId()) {
		case R.id.action_drop:
		    inputTitle = (EditText)getView().findViewById(R.id.drop_treasure_title);
		    inputText = (EditText)getView().findViewById(R.id.drop_treasure_text);
			mCallback.onTreasureDropped(inputTitle.getText().toString(), inputText.getText().toString(), null);
			break;
		default:
			return super.onOptionsItemSelected(item);
		}

		return true;
	}

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		try {
			mCallback = (OnTreasureDroppedListener) activity;
		} catch (ClassCastException e) {
			throw new ClassCastException(activity.toString()
					+ " must implement OnTreasureDroppedListener");
		}
	}

	@Override
	public void onDetach() {
		super.onDetach();
		mCallback = null;
	}

}
