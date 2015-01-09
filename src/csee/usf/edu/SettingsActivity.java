package csee.usf.edu;

import com.sensorcon.sensordrone.DroneEventHandler;
import com.sensorcon.sensordrone.DroneEventObject;
import com.sensorcon.sensordrone.android.Drone;
import com.sensorcon.sensordrone.android.tools.DroneConnectionHelper;

import android.os.Bundle;
import android.preference.PreferenceManager;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.support.v4.app.NavUtils;



public class SettingsActivity extends Activity {
	SharedPreferences settings;
	SharedPreferences.Editor editor;
	
	//Sensordrone Objects
	Drone myDrone;
	DroneEventHandler myDroneEventHandler;
	DroneConnectionHelper myHelper;	
	
	TextView tvStatus;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_settings);
		// Show the Up button in the action bar.
		setupActionBar();
		
		// Set up our Sensordrone object
        myDrone = new Drone();
		
        // Set up our DroneConnectionHelper
        myHelper = new DroneConnectionHelper();
        
		TextView email = (TextView) findViewById(R.id.account_email);
		SharedPreferences prefEmail = PreferenceManager.getDefaultSharedPreferences(this);
		prefEmail.getString("email", null);
		
		email.setText(prefEmail.getString("email", null));
		
		settings = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
		
    	myHelper.scanToConnect(myDrone, SettingsActivity.this, this, true);

		Button logoutButton = (Button) findViewById(R.id.btnLogout);
		logoutButton.setOnClickListener(new View.OnClickListener() {
		    public void onClick(View v) {
		    	editor = settings.edit();
		    	editor.clear();
		    	editor.commit();
		    	Intent logoutIntent = new Intent(SettingsActivity.this, LoginActivity.class);
		    	startActivity(logoutIntent);
		    }
		});
		
		Button disconnectBtn = (Button) findViewById(R.id.btnDisconnect);
		disconnectBtn.setOnClickListener(new View.OnClickListener() {
		    public void onClick(View v) {
		    	if (myDrone.isConnected) {
		            myDrone.disconnect();
		        }
		        
		        myDrone.unregisterDroneListener(myDroneEventHandler);
		    }
		});
		
		tvStatus = (TextView)findViewById(R.id.sd_status);
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
                    // Most all of these events are triggered from a background worker thread.
                    // you need to remember this if you plan to update UI elements!
                    // The method below shows how to easily do this.
                    updateTextViewFromUI(tvStatus, "Connected");
                    
                }
                else if (droneEventObject.matches(DroneEventObject.droneEventType.DISCONNECTED)) {
                    // This is triggered when the disconnect method is called.
                    updateTextViewFromUI(tvStatus, "Not connected");
                }
            }
        };
	}

	/**
	 * Set up the {@link android.app.ActionBar}.
	 */
	private void setupActionBar() {

		getActionBar().setDisplayHomeAsUpEnabled(true);

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.settings, menu);
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
	
	// A method to update a TextView from the UI thread
    public void updateTextViewFromUI(final TextView textView, final String text) {
    	SettingsActivity.this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                textView.setText(text);
            }
        });
    }
    
    @Override
    protected void onResume() {
        super.onResume();
        myDrone.registerDroneListener(myDroneEventHandler);

    }

    @Override
    protected void onPause() {
        super.onPause();
    
        if (myDrone.isConnected) {
            myDrone.disconnect();
        }
        
        myDrone.unregisterDroneListener(myDroneEventHandler);
    }

}
