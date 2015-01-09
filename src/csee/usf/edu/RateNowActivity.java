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

import com.sensorcon.sensordrone.DroneEventHandler;
import com.sensorcon.sensordrone.DroneEventObject;
import com.sensorcon.sensordrone.android.Drone;
import com.sensorcon.sensordrone.android.tools.DroneConnectionHelper;

import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.text.format.Time;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;


public class RateNowActivity extends Activity {

	TextView tvTemperature;
	TextView tvStatus;
	TextView tvHumidity;
	TextView tvPressure;
	TextView tvLightIntensity;
	TextView tvCarbon;
	
	String temp;
	String humidity;
	String pressure;
	String lightIntensity;
	String carbon;

    // Sensordrone Objects
    Drone myDrone;
    DroneEventHandler myDroneEventHandler;
    DroneConnectionHelper myHelper;	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_rate_now);
		
		/******** ADDED *********/
		// Set up our Sensordrone object
        myDrone = new Drone();
		
        // Set up our DroneConnectionHelper
        myHelper = new DroneConnectionHelper();
        
        // Target text view for temperature
        tvTemperature = (TextView)findViewById(R.id.tv_temperature);
        tvStatus = (TextView)findViewById(R.id.sensor_status);
        tvHumidity = (TextView)findViewById(R.id.tv_humidity);
        tvPressure = (TextView)findViewById(R.id.tv_pressure);
        tvCarbon = (TextView)findViewById(R.id.tv_carbon);
        tvLightIntensity = (TextView)findViewById(R.id.tv_light_intensity);
        
        // Connect 
        if (myDrone.isConnected) {
            // Don't try to connect again if we are already connected!
            genericDialog("Whoa!","You are already connected to a Sensordrone.");
        }
        else {
            // Show a list of paired drones that can be selected to connect to.
            // If there are none, then a message will be displayed about how
            // to pair one.
            // You can check out the source code (mentioned above) to see how we do it,
            // if you need/want to implement your own style.
            
        	myHelper.scanToConnect(myDrone, RateNowActivity.this, this, true);
        	//myHelper.connectFromPairedDevices(myDrone, RateNowActivity.this);
        }
                
        myDroneEventHandler = new DroneEventHandler() {
            @Override
            public void parseEvent(DroneEventObject droneEventObject) {
                // We check the droneEventObject to see what type of event it is,
                // and then perform logic based upon the type.
                //
                // The DroneEventObject has available enum types that can be filtered,
                // and provides a boolean matches() method, that compares the
                // fired droneEventObject with supplied type. The names of the types
                // should be self explanatory.
                //
                // The (latest) version of the DroneEventObject class can be viewed at
                // https://github.com/Sensorcon/Sensordrone/blob/master/src/com/sensorcon/sensordrone/DroneEventObject.java

                if (droneEventObject.matches(DroneEventObject.droneEventType.CONNECTED)) {
                    // This is triggered when a connection is made.
                    // Set the LEDs blue
                    myDrone.setLEDs(0,0,126);
                    Log.e("NOW", "" + myDrone.isConnected);
                    // Most all of these events are triggered from a background worker thread.
                    // you need to remember this if you plan to update UI elements!
                    // The method below shows how to easily do this.
                    updateTextViewFromUI(tvStatus, "Connected");
                    
                    // Since we know we want to measure temperature, we will automatically enable the
                    // temperature sensor here at every connect. Now the user won't have to worry about it.
                    myDrone.enableTemperature();
                    myDrone.enableHumidity();
                    myDrone.enableRGBC();
                    myDrone.enablePressure();
                    myDrone.enableOxidizingGas();
                }
                else if (droneEventObject.matches(DroneEventObject.droneEventType.DISCONNECTED)) {
                    // This is triggered when the disconnect method is called.
                    updateTextViewFromUI(tvStatus, "Not connected");
                }
                else if (droneEventObject.matches(DroneEventObject.droneEventType.CONNECTION_LOST)) {
                    updateTextViewFromUI(tvStatus, "Connection lost!");
                    uiToast("Connection lost!");
                }
                else if (droneEventObject.matches(DroneEventObject.droneEventType.TEMPERATURE_ENABLED)) {
                    myDrone.measureTemperature();
                }
                else if (droneEventObject.matches(DroneEventObject.droneEventType.TEMPERATURE_MEASURED)) {
                    temp = String.format("%.1f",myDrone.temperature_Fahrenheit) + " \u00B0F";
                    updateTextViewFromUI(tvTemperature, temp);
                }
                else if (droneEventObject.matches(DroneEventObject.droneEventType.HUMIDITY_ENABLED)) {
                    myDrone.measureHumidity();
                }
                else if (droneEventObject.matches(DroneEventObject.droneEventType.HUMIDITY_MEASURED)) {
                	humidity = String.format("%.1f",myDrone.humidity_Percent) + "%";
                    updateTextViewFromUI(tvHumidity, humidity);
                }
                else if (droneEventObject.matches(DroneEventObject.droneEventType.PRESSURE_ENABLED)) {
                    myDrone.measurePressure();
                }
                else if (droneEventObject.matches(DroneEventObject.droneEventType.PRESSURE_MEASURED)) {
                	pressure = String.format("%.2f", myDrone.pressure_Pascals/1000) + "kPa";
                    updateTextViewFromUI(tvPressure, pressure);
                }
                else if (droneEventObject.matches(DroneEventObject.droneEventType.RGBC_ENABLED )) {
                    myDrone.measureRGBC();
                }
                else if (droneEventObject.matches(DroneEventObject.droneEventType.RGBC_MEASURED)) {
                	lightIntensity = String.format("%.0f", myDrone.rgbcLux) + "Lux";
                    updateTextViewFromUI(tvLightIntensity, lightIntensity);
                }
                else if (droneEventObject.matches(DroneEventObject.droneEventType.OXIDIZING_GAS_ENABLED  )) {
                    myDrone.measureOxidizingGas();
                }
                else if (droneEventObject.matches(DroneEventObject.droneEventType.OXIDIZING_GAS_MEASURED)) {
                	carbon = String.format("%.2f", myDrone.oxidizingGas_Ohm /1000) + " ppm";
                    updateTextViewFromUI(tvCarbon, carbon);
                }
            }
        };
		Button rate_now_submit = (Button) findViewById(R.id.rate_now_submit);
		rate_now_submit.setOnClickListener(new View.OnClickListener() {
		    public void onClick(View v) {
		    	submitRating();
		    	uiToast("Rating Submitted!");
		    	new Handler().postDelayed(new Runnable() {
	                public void run() {
	                	onBackPressed();	                        
	                }
	        }, 2 * 1000);
		    }
		});
	}
	
	public void submitRating()
	{
		SubmitRatingTask task = new SubmitRatingTask();
		Bundle extras = getIntent().getExtras();
		
		Time now = new Time();
		now.setToNow();
		Log.d("TIMEHOUR", ""+now.hour);
		
		String dayNight;
		
		if(now.hour > 5 && now.hour < 16) {
			dayNight = "true";
		} else {
			dayNight = "false";
		}
		task.execute(extras.getString("EXTRA_ID"), dayNight, lightIntensity, pressure, humidity, temp, carbon);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.rate_now, menu);
		return true;
	}
	
	@Override
    protected void onResume() {
        super.onResume();
        Log.e("SENSOR", "" + myDrone.isConnected);
        // The DroneEventHandler won't handle any notifications if it's not registered to a Sensordrone!
        myDrone.registerDroneListener(myDroneEventHandler);

        // You can have multiple drone objects (if your app connects to multiple sensordrones)
        // and you can register the same (or different) listeners to each one.
    }

    @Override
    protected void onPause() {
        super.onPause();
        // If you don't want your app to keep doing things in the background, then unregister
        // your DroneEventHandler.
        // This can get tricky if you're not careful!
        // For example, if you register your DroneEventHandler in onResume, but
        // don't unregister it in onPause, then the same listener can be added every onResume,
        // and you will start to get multiple notifications for the same event!
        // You also should consider the flow of events too, especially if you have any 'daisy-chained' events;
        // If your first event doesn't get re-triggered, then the rest of the chain won't either!
        myDrone.unregisterDroneListener(myDroneEventHandler);
        if (myDrone.isConnected) {
            // The LEDs will be on while connected, so turn them off before disconnecting.
            // The (latest) firmware will do this automatically once it processes the disconnect,
            // so it isn't critical.
            // The same goes for disabling sensors. It's jut a 'best practice' thing to do.
            myDrone.disableTemperature();
            myDrone.setLEDs(0,0,0);
            myDrone.disconnect();
        }
        else {
            // If we weren't connected, tell the user
            genericDialog("Whoa!","You are not currently connected to a Sensordrone");
        }
    }

    // A method to show a generic alert dialog with the supplied title and content
    public void genericDialog(String title, String msg) {
        Dialog dialog;
        AlertDialog.Builder builder = new AlertDialog.Builder(RateNowActivity.this);
        builder.setTitle(title);
        builder.setMessage(msg);
        builder.setIcon(R.drawable.ic_launcher);
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //
            }
        });
        dialog = builder.create();
        dialog.show();
    }

    // A method to update a TextView from the UI thread
    public void updateTextViewFromUI(final TextView textView, final String text) {
    	RateNowActivity.this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                textView.setText(text);
            }
        });
    }

    // A method to display a Toast notification from the UI thread
    public void uiToast(final String msg) {
        RateNowActivity.this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(RateNowActivity.this, msg, Toast.LENGTH_SHORT).show();
            }
        });
    }
    
	public class SubmitRatingTask extends AsyncTask<String, Void, Boolean> 
	{
		/**
		 * The URL to get ratings
		 */
		public static final String ratingUrl = "http://shawnhathaway.com/iAmbience/sql/submitRating.php";
		
		
		@Override
		protected Boolean doInBackground(String... params) 
		{
			// TODO: attempt authentication against a network service.
			InputStream is = null;
			String result = "";
			String id = params[0];
			
			java.util.ArrayList<NameValuePair> nameValuePairs = new java.util.ArrayList<NameValuePair>();
			nameValuePairs.add(new BasicNameValuePair("id",id));
			nameValuePairs.add(new BasicNameValuePair("day",params[1]));
			nameValuePairs.add(new BasicNameValuePair("light",params[2]));
			nameValuePairs.add(new BasicNameValuePair("pressure",params[3]));
			nameValuePairs.add(new BasicNameValuePair("humidity",params[4]));
			nameValuePairs.add(new BasicNameValuePair("temp",params[5]));
			nameValuePairs.add(new BasicNameValuePair("co",params[6]));
			
			
			try {
				HttpClient httpclient = new DefaultHttpClient();
                HttpPost httppost = new HttpPost(ratingUrl);
                Log.d("HTTP", "" + httppost);
                httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
                HttpResponse response = httpclient.execute(httppost);
                HttpEntity entity = response.getEntity();
                is = entity.getContent();
			} catch (Exception e) {
				Log.e("log", "Error posting to: " + ratingUrl);
				return false;
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
                
                Log.d("TEST", "" + result);
            } 
            catch (Exception e) 
            {
                Log.e("log", "Error converting following result to string: " + e.toString());
                return false;
            }
            
            result = result.replaceAll("\n", "");
            
            
            if(result.equals("true"))
            {
        		return true;
            }
            else
            {
            	//error
            	return false;
            }
            	
		}

		private void ParseErrorAndLog(String result) {
			return;
		}

		@Override
		protected void onPostExecute(Boolean success) {
			//How to handle false?
			
			//Go back to detail activity?
		}

		@Override
		protected void onCancelled() {
			//todo
		}
	}
}
