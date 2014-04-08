package com.esteniek.treasurely_android;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

public class TreasureAdapter extends ArrayAdapter<Treasure> {
	
	Context context;
	int resource;
	ArrayList<Treasure> objects = null;

	public TreasureAdapter(Context context, int resource, ArrayList<Treasure> objects) {
		
		super(context, resource, objects);
		this.resource = resource;
		this.context = context;
		this.objects = objects;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		
		View row = convertView;
		TreasureHolder holder = null;

		if (row == null) {
			LayoutInflater inflater = ((Activity) context).getLayoutInflater();
			row = inflater.inflate(resource, parent, false);

			holder = new TreasureHolder();
			holder.txtTreasureTitle = (TextView) row
					.findViewById(R.id.txtTreasureTitle);

			row.setTag(holder);
		} else {
			holder = (TreasureHolder) row.getTag();
		}

		Treasure treasure = objects.get(position);
		holder.txtTreasureTitle.setText(treasure.title);

		return row;
	}

	static class TreasureHolder {
		
		TextView txtTreasureTitle;
	}
}
