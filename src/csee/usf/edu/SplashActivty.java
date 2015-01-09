package csee.usf.edu;

import android.location.Criteria;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.util.Log;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

public class SplashActivty extends Activity {
	SharedPreferences settings;
	SharedPreferences.Editor editor;
	
	@Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_activty);
        settings = PreferenceManager.getDefaultSharedPreferences(this);
		int secondsDelayed = 3;
        new Handler().postDelayed(new Runnable() {
                public void run() {
                	
                	
                	if(settings.getString("email", null) != null) {
            			Intent homeIntentLoggedIn = new Intent(SplashActivty.this, HomeActivity.class);
            			startActivity(homeIntentLoggedIn);
            		} else {
            			startActivity(new Intent(SplashActivty.this, LoginActivity.class));
                        finish();
            		}
                        
                }
        }, secondsDelayed * 1000);
    }
}
