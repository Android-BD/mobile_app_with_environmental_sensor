package csee.usf.edu;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Activity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;
import android.support.v4.app.NavUtils;
import android.annotation.TargetApi;
import android.content.Intent;
import android.os.Build;

public class DetailActivity extends Activity {

	TextView temperature;
	TextView humidity;
	TextView pressure;
	TextView light;
	TextView carbon;
	
	JSONObject jObject;
	float dayRawTemp;
	float dayCountTemp;
	float dayTemp;
	float dayRawHumidity;
	float dayCountHumidity;
	float dayHumidity;
	float dayRawPressure;
	float dayCountPressure;
	float dayPressure;
	float dayRawLight;
	float dayCountLight;
	float dayLight;
	float dayRawCarbon;
	float dayCountCarbon;
	float dayCarbon;
	
	float nightRawTemp;
	float nightCountTemp;
	float nightTemp;
	float nightRawHumidity;
	float nightCountHumidity;
	float nightHumidity;
	float nightRawPressure;
	float nightCountPressure;
	float nightPressure;
	float nightRawLight;
	float nightCountLight;
	float nightLight;
	float nightRawCarbon;
	float nightCountCarbon;
	float nightCarbon;
	
	String bus_id;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_detail);
		// Show the Up button in the action bar.
		Bundle extras = getIntent().getExtras();
		
		setupActionBar();
		
		TextView name = (TextView) findViewById(R.id.detail_business_name);
		TextView address = (TextView) findViewById(R.id.detail_business_address);
		final String business_id = extras.getString("EXTRA_ID");
		
		bus_id = business_id;
		name.setText(extras.getString("EXTRA_NAME"));
		address.setText(extras.getString("EXTRA_ADDRESS"));
		
		temperature = (TextView)findViewById(R.id.tv_getTemp);
		humidity = (TextView)findViewById(R.id.tv_getHumidity);
		pressure = (TextView)findViewById(R.id.tv_getPressure);
		light = (TextView)findViewById(R.id.tv_getLight);
		carbon = (TextView)findViewById(R.id.tv_getCarbon);
		
		Button rate_now = (Button) findViewById(R.id.rate_now_button);
		rate_now.setOnClickListener(new View.OnClickListener() {
		    public void onClick(View v) {
		    	Intent searchIntent = new Intent(DetailActivity.this, RateNowActivity.class);
		    	searchIntent.putExtra("EXTRA_ID", business_id);
		    	startActivity(searchIntent);
		    }
		});
		
		temperature.setText(String.format("%.1f", dayTemp) + " \u00B0F");
        humidity.setText(String.format("%.1f", dayHumidity) + "%");
        pressure.setText(String.format("%.2f", dayPressure) + "kPa");
		carbon.setText(String.format("%.2f", dayCarbon) + " ppm");
		light.setText(String.format("%.0f", dayLight) + " Lux");	
		
		GetRatingTask rateTask = new GetRatingTask();
		rateTask.execute(business_id);
		
		Switch toggle = (Switch) findViewById(R.id.dayNightSwitch);
		toggle.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
		    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
		        if (isChecked) {
		            Log.d("NIGHT", "NIGHT");
		            temperature.setText(String.format("%.1f", nightTemp) + " \u00B0F");
		            humidity.setText(String.format("%.1f", nightHumidity) + "%");
		            pressure.setText(String.format("%.2f", nightPressure) + "kPa");
					carbon.setText(String.format("%.2f", nightCarbon) + " ppm");
					light.setText(String.format("%.0f", nightLight) + " Lux");	
		        } else {
		        	Log.d("DAY", "DAY");
					temperature.setText(String.format("%.1f", dayTemp) + " \u00B0F");
		            humidity.setText(String.format("%.1f", dayHumidity) + "%");
		            pressure.setText(String.format("%.2f", dayPressure) + "kPa");
					carbon.setText(String.format("%.2f", dayCarbon) + " ppm");
					light.setText(String.format("%.0f", dayLight) + " Lux");	
		        }
		    }
		});
	}

	/**
	 * Set up the {@link android.app.ActionBar}, if the API is available.
	 */
	@TargetApi(Build.VERSION_CODES.HONEYCOMB)
	private void setupActionBar() {
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
			getActionBar().setDisplayHomeAsUpEnabled(true);
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.detail, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			// This ID represents the Home or Up button. In the case of this
			// activity, the Up button is shown. Use NavUtils to allow users
			// to navigate up one level in the application structure. For
			// more details, see the Navigation pattern on Android Design:
			//
			// http://developer.android.com/design/patterns/navigation.html#up-vs-back
			//
			NavUtils.navigateUpFromSameTask(this);
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
	
	public class GetRatingTask extends AsyncTask<String, Void, String> 
	{
		/**
		 * The URL to get ratings
		 */
		public static final String ratingUrl = "http://shawnhathaway.com/iAmbience/sql/getRating.php";
		
		
		@Override
		protected String doInBackground(String... params) 
		{
			// TODO: attempt authentication against a network service.
			InputStream is = null;
			String result = "";
			String id = params[0];
			
			java.util.ArrayList<NameValuePair> nameValuePairs = new java.util.ArrayList<NameValuePair>();
			nameValuePairs.add(new BasicNameValuePair("id",id));
			
			try {
				HttpClient httpclient = new DefaultHttpClient();
                HttpPost httppost = new HttpPost(ratingUrl);
                httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
                HttpResponse response = httpclient.execute(httppost);
                HttpEntity entity = response.getEntity();
                is = entity.getContent();
			} catch (Exception e) {
				Log.e("log", "Error posting to: " + ratingUrl);
				return null;
			}

            try 
            {
                BufferedReader reader = new BufferedReader(new InputStreamReader(is, "iso-8859-1"), 8);
                StringBuilder sb = new StringBuilder();
                String line = null;
                while ((line = reader.readLine()) != null) 
                {
                    sb.append(line + "\n");
                }
                is.close();

                result = sb.toString();
            } 
            catch (Exception e) 
            {
                Log.e("log", "Error converting following result to string: " + e.toString());
                return null;
            }
            
            result = result.replaceAll("\n", "");
            
            return result;
            	
		}

		private void ParseErrorAndLog(String result) {
			return;
		}

		@Override
		protected void onPostExecute(String result) {
			if (result == null) {
				//error
			} else if (result.equals("")) {
				//No ratings
				//How to handle this?
			} else {
				// Get JSON object
	            try {
	            	jObject = new JSONObject(result);
					
	            	// Day Temp
	            	dayRawTemp = Float.parseFloat(jObject.getString("temp_day_raw"));
					dayCountTemp = Float.parseFloat(jObject.getString("temp_day_count"));
					dayTemp = dayRawTemp / dayCountTemp;
					Log.d("DAY TEMP", "" + dayTemp);
					temperature.setText(String.format("%.1f", dayTemp) + " \u00B0F");
					
					// Day Humidity
					dayRawHumidity = Float.parseFloat(jObject.getString("hum_day_raw"));
					dayCountHumidity = Float.parseFloat(jObject.getString("hum_day_count"));
					dayHumidity = dayRawHumidity / dayCountHumidity;
					Log.d("DAY HUM", "" + dayHumidity);
					humidity.setText(String.format("%.1f", dayHumidity) + "%");
					
					// Day Pressure
					dayRawPressure = Float.parseFloat(jObject.getString("press_day_raw"));
					dayCountPressure = Float.parseFloat(jObject.getString("press_day_count"));
					dayPressure = dayRawPressure / dayCountPressure;
					Log.d("DAY PRESS", "" + dayPressure);
					pressure.setText(String.format("%.2f", dayPressure) + "kPa");
					
					//Day Carbon
					dayRawCarbon = Float.parseFloat(jObject.getString("co_day_raw"));
					dayCountCarbon = Float.parseFloat(jObject.getString("co_day_count"));
					dayCarbon = dayRawCarbon / dayCountCarbon;
					Log.d("DAY CARBON","" + dayCarbon);
					carbon.setText(String.format("%.2f", dayCarbon) + " ppm");
					
					// Day Light
					dayRawLight = Float.parseFloat(jObject.getString("light_day_raw"));
					dayCountLight = Float.parseFloat(jObject.getString("light_day_count"));
					dayLight = dayRawLight / dayCountLight;
					Log.d("DAY LIGHT", "" + dayLight);
					light.setText(String.format("%.0f", dayLight) + " Lux");		
					
					// night Temp
	            	nightRawTemp = Float.parseFloat(jObject.getString("temp_night_raw"));
					nightCountTemp = Float.parseFloat(jObject.getString("temp_night_count"));
					nightTemp = nightRawTemp / nightCountTemp;
					Log.d("night TEMP", "" + nightTemp);
					
					
					// night Humidity
					nightRawHumidity = Float.parseFloat(jObject.getString("hum_night_raw"));
					nightCountHumidity = Float.parseFloat(jObject.getString("hum_night_count"));
					nightHumidity = nightRawHumidity / nightCountHumidity;
					Log.d("night HUM", "" + nightHumidity);
					
					
					// night Pressure
					nightRawPressure = Float.parseFloat(jObject.getString("press_night_raw"));
					nightCountPressure = Float.parseFloat(jObject.getString("press_night_count"));
					nightPressure = nightRawPressure / nightCountPressure;
					Log.d("night PRESS", "" + nightRawPressure);
										
					// night Light
					nightRawLight = Float.parseFloat(jObject.getString("light_night_raw"));
					nightCountLight = Float.parseFloat(jObject.getString("light_night_count"));
					nightLight = nightRawLight / nightCountLight;
					Log.d("night LIGHT", "" + nightLight);
					
					//Night Carbon
					nightRawCarbon = Float.parseFloat(jObject.getString("co_night_raw"));
					nightCountCarbon = Float.parseFloat(jObject.getString("co_night_count"));
					nightCarbon = nightRawCarbon / nightCountCarbon;
					Log.d("night CARBON","" + nightCarbon);
					
					
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
	            
	            
				
			}
		}

		@Override
		protected void onCancelled() {
			//todo
		}
	}
	
	@Override
    protected void onResume() {
		super.onResume();
		GetRatingTask rateTask = new GetRatingTask();
		rateTask.execute(bus_id);
    }

}