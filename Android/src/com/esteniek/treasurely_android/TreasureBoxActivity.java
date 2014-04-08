package com.esteniek.treasurely_android;

import java.io.InputStream;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;

public class TreasureBoxActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getActionBar().setDisplayHomeAsUpEnabled(true);

		setContentView(R.layout.activity_treasure_box);
		
		Bundle extras = getIntent().getExtras();
		if (extras != null) {
			String mtitle = extras.getString("title");
			String mtext = extras.getString("text");
			String mmedia = extras.getString("image");
			TextView title = (TextView) findViewById(R.id.treasure_title);
			title.setText(mtitle+"\n\n"+ mtext);
//			TextView text = (TextView) findViewById(R.id.treasure_title);
//			text.setText(mtext);
			ImageView iv = (ImageView) findViewById(R.id.treasure_media);
			mmedia = (mmedia != null) ? mmedia : "";
			if (mmedia != "") {
				String url = this.getApplicationContext().getString(
						R.string.baseUrl)
						+ "public" + mmedia;

				new DownloadImageTask(iv).execute(url);
			} else {
				iv.setImageResource(R.drawable.default_background);
			}
		}
	}

	class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {
		ImageView bmImage;

		public DownloadImageTask(ImageView bmImage) {
			this.bmImage = bmImage;
		}

		@Override
		protected void onPreExecute() {
			// TODO Auto-generated method stub
			super.onPreExecute();
		}

		protected Bitmap doInBackground(String... urls) {
			String urldisplay = urls[0];
			Bitmap mIcon11 = null;
			try {
				InputStream in = new java.net.URL(urldisplay).openStream();
				mIcon11 = BitmapFactory.decodeStream(in);
			} catch (Exception e) {
				Log.e("Error", e.getMessage());
				e.printStackTrace();
			}
			return mIcon11;
		}

		@Override
		protected void onPostExecute(Bitmap result) {
			super.onPostExecute(result);
			bmImage.setImageBitmap(result);
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.treasure_box, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		
		switch (item.getItemId()) {
		case R.id.action_drop:
			// New Intent to launch a Drop Treasure Activity
			startActivity(new Intent(this, DropTreasureActivity.class));
			break;
		case android.R.id.home:
			startActivity(new Intent(this, MainActivity.class));
	        break;
	    default:
			return super.onOptionsItemSelected(item);
		}

		return true;
	}

}
