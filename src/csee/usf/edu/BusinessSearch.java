package csee.usf.edu;

import java.util.ArrayList;
import java.util.HashMap;

import org.json.JSONArray;
import org.json.JSONObject;

import android.os.AsyncTask;
import android.util.Log;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;

public class BusinessSearch extends AsyncTask<Void, Void, String> {
	// Yelp API Keys
	final String consumerKey = "14d8v3TTyDXGENrHaxE8QA";
    final String consumerSecret = "10y4tAqVpMhZI0y3kOwbQ6p_CZw";
    final String token = "RwRfXRHP9aBx3G6GFJw_cdiiNKHbLugc";
    final String tokenSecret = "M10jJmM45obncsyQDKGRuEvWpNA";
    private String searchTerm;
    private double latitude;
    private double longitude;
    ArrayList<HashMap<String, String>> businessList;
	
    public BusinessSearch(String st, ArrayList<Double> gpsCoords) {
    	this.searchTerm = st;
    	this.latitude = gpsCoords.get(0);
    	this.longitude = gpsCoords.get(1);
    	this.businessList = new ArrayList<HashMap<String, String>>();
    }
    
    @Override
	protected String doInBackground(Void...params) {
		try {
        	// Make API search call
            Yelp yelp = new Yelp(consumerKey, consumerSecret, token, tokenSecret);
            String response = yelp.search(searchTerm, latitude, longitude);
            return response;
		} catch (Exception e) {
			return "EMPTY";
		}           
    }

    @Override
    protected void onPostExecute(String yelpResponse) {
    	
    }
}

