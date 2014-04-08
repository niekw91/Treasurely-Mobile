package com.esteniek.treasurely_android;

<<<<<<< HEAD
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.ByteArrayBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HttpContext;
import org.json.JSONObject;

import services.LocationService;
import services.RESTService;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;
=======
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import com.esteniek.treasurely_android.services.LocationService;
import com.esteniek.treasurely_android.services.RESTService;
>>>>>>> 282284004ea5c36aab0901eddd44bda94a7804fc

public class DropTreasureActivity extends Activity implements
DropTreasureFragment.OnTreasureDroppedListener {
	private SharedPreferences prefs;
	
	private DropTreasureTask mDropTask;
	private String newTreasureId;

	private static LocationService location;
	private static String _title;
	private static String _text;
	private static String _media;
	private static String _userId;
	
    ImageView targetImage;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getActionBar().setDisplayHomeAsUpEnabled(true);
		
		setContentView(R.layout.activity_drop_treasure);
		
		ImageButton buttonLoadImage = (ImageButton)findViewById(R.id.drop_treasure_camera);
        targetImage = (ImageView)findViewById(R.id.drop_treasure_media);

        buttonLoadImage.setOnClickListener(new Button.OnClickListener(){

	    @Override
	    public void onClick(View arg0) {
		     // TODO Auto-generated method stub
		     Intent intent = new Intent(Intent.ACTION_PICK,
		       android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
		     startActivityForResult(intent, 0);
	    }});
    }

   @Override
   protected void onActivityResult(int requestCode, int resultCode, Intent data) {
	   // TODO Auto-generated method stub
	   super.onActivityResult(requestCode, resultCode, data);
	
	   if (resultCode == RESULT_OK){
		    Uri targetUri = data.getData();
		    Bitmap bitmap;
		    try {
		     bitmap = BitmapFactory.decodeStream(getContentResolver().openInputStream(targetUri));
		     targetImage.setImageBitmap(bitmap);
		    } catch (FileNotFoundException e) {
		     // TODO Auto-generated catch block
		     e.printStackTrace();
		    }
	   }
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
		switch (item.getItemId()) {
			case android.R.id.home:
				startActivity(new Intent(this, MainActivity.class));
	        break;
		}
		return super.onOptionsItemSelected(item);
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
		if (_text != null) {
			if (json != "{") {json += ",";}
			json += "\"text\":" + _text;
		}
		if (_title != null) {
			if (json != "{") {json += ",";}
			json += "\"title\":" + _title;
		}
		if (_media != null) {
			if (json != "{") {json += ",";}
			json += "\"title\":" + _media;
		}
		json += "}";
		return json;
	}

	@Override
	public void onTreasureDropped(String title, String text, String media) {
		prefs = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
		_userId = prefs.getString("userId", "");
		_title = title;
		_text = text;
		_media = media;
		dropTreasure();
	}
	
	public void dropTreasure() {
		mDropTask = new DropTreasureTask();
		mDropTask.execute((Void) null);
	}

	
	/**
	 * Represents an asynchronous login/registration task used to authenticate
	 * the user.
	 */
	public class DropTreasureTask extends AsyncTask<Void, Void, Boolean> {
		@Override
		protected Boolean doInBackground(Void... params) {
			// TODO: attempt authentication against a network service.
			
			List<NameValuePair> nvp = new ArrayList<NameValuePair>(2);
		    nvp.add(new BasicNameValuePair("user_id", _userId));
		    nvp.add(new BasicNameValuePair("title", _title));
		    nvp.add(new BasicNameValuePair("text", _text));
		    nvp.add(new BasicNameValuePair("latitude", "37.334476"));
		    nvp.add(new BasicNameValuePair("longitude", "-122.039740"));
		    
			RESTService rest = new RESTService(getBaseContext());
			String result = rest.dropTreasure(nvp);
			
			String id = null;
			try {
				id = setResult(result);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			if (id != null) {
				return true;
			} 
			return false;
		}

		@Override
		protected void onPostExecute(final Boolean success) {
			ImageUploadTask imageTask = new ImageUploadTask();
			imageTask.execute((Void) null);
		}

		@Override
		protected void onCancelled() {

		}
		
		private String setResult(String result) throws Exception {			
			JSONObject mainObject = new JSONObject(result);
			
			return mainObject.getString("id");		
		}
	}
	
	class ImageUploadTask extends AsyncTask <Void, Void, String>{
        @Override
        protected String doInBackground(Void... unsued) {
            try {
                HttpClient httpClient = new DefaultHttpClient();
                HttpContext localContext = new BasicHttpContext();
                HttpPost httpPost = new HttpPost("http://treasurely.no-ip.org:7000/upload");

                MultipartEntity entity = new MultipartEntity(
                        HttpMultipartMode.BROWSER_COMPATIBLE);

                ByteArrayOutputStream bos = new ByteArrayOutputStream();
                
                targetImage = (ImageView)findViewById(R.id.drop_treasure_media);
                Bitmap bitmap = ((BitmapDrawable)targetImage.getDrawable()).getBitmap();
                bitmap.compress(CompressFormat.JPEG, 100, bos);
                byte[] data = bos.toByteArray();
                entity.addPart("photoId", new StringBody(getIntent()
                        .getStringExtra("photoId")));
                entity.addPart("returnformat", new StringBody("json"));
                entity.addPart("uploaded", new ByteArrayBody(data,
                        "myImage.jpg"));
                httpPost.setEntity(entity);
                HttpResponse response = httpClient.execute(httpPost,
                        localContext);
                BufferedReader reader = new BufferedReader(
                        new InputStreamReader(
                                response.getEntity().getContent(), "UTF-8"));

                String sResponse = reader.readLine();
                return sResponse;
            } catch (Exception e) {
                return null;
            }

            // (null);
        }

        @Override
        protected void onProgressUpdate(Void... unsued) {

        }

        @Override
        protected void onPostExecute(String sResponse) {
            try {
                if (sResponse != null) {
                    JSONObject JResponse = new JSONObject(sResponse);
                    int success = JResponse.getInt("SUCCESS");
                    String message = JResponse.getString("MESSAGE");
                    if (success == 0) {
                        Toast.makeText(getApplicationContext(), message,
                                Toast.LENGTH_LONG).show();
                    } else {

                    }
                }
            } catch (Exception e) {

            }
        }
    }
}
