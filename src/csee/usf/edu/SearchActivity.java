package csee.usf.edu;

import java.util.ArrayList;
import java.util.HashMap;

import org.json.JSONArray;
import org.json.JSONObject;

import csee.usf.edu.HomeActivity.GetBusinessList;
import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Activity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.support.v4.app.NavUtils;
import android.annotation.TargetApi;
import android.content.Intent;
import android.os.Build;

public class SearchActivity extends Activity {
	public static final String TAG_BUSINESSES = "businesses";
	public static final String TAG_NAME = "name";
	public static final String TAG_LOCATION = "location";
	public static final String TAG_ADDRESS = "address";
	public String yelpSearchTerm;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_search);
		// Show the Up button in the action bar.
		setupActionBar();
		
		Button searchButton = (Button)findViewById(R.id.searchButton);
	    final EditText searchTerm = (EditText)findViewById(R.id.searchText);
	    
	    
	    searchButton.setOnClickListener(
	            new View.OnClickListener()
	            {
	                public void onClick(View view)
	                {
	                	Log.d("CLICK", "CLICK");
	                	yelpSearchTerm = searchTerm.getText().toString();
	                	GPSTracker gps = new GPSTracker(SearchActivity.this);
	                	   
	                    if(gps.canGetLocation()){
	                    	new GetBusinessList().execute(gps.getGPSCoordinates());
	                    } else {
	                    	//TODO: Implement error message
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
		getMenuInflater().inflate(R.menu.search, menu);
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
	
	public class GetBusinessList extends AsyncTask<ArrayList<Double>, Void, ArrayList<HashMap<String, String>>> {
		@Override
		protected ArrayList<HashMap<String, String>> doInBackground(ArrayList<Double>... params) {
			ArrayList<Double> arguments = params[0];
			try {
	        	// Yelp API Keys
	        	String consumerKey = "14d8v3TTyDXGENrHaxE8QA";
	            String consumerSecret = "10y4tAqVpMhZI0y3kOwbQ6p_CZw";
	            String token = "RwRfXRHP9aBx3G6GFJw_cdiiNKHbLugc";
	            String tokenSecret = "M10jJmM45obncsyQDKGRuEvWpNA";
	            
	            //	Initialize Business List
	            ArrayList<HashMap<String, String>> businessList = new ArrayList<HashMap<String, String>>();
	        	
	        	// Make API search call
	            Yelp yelp = new Yelp(consumerKey, consumerSecret, token, tokenSecret);
	            String response = yelp.search(yelpSearchTerm, arguments.get(0), arguments.get(1));
	            
	            // Get JSON object
	            JSONObject jObject = new JSONObject(response);
	            
	            // Getting array of businesses
				JSONArray businesses = jObject.getJSONArray(TAG_BUSINESSES);
	        
				// Loop through each business and add it to hash table
				for(int i = 0; i < businesses.length(); i++){
			        JSONObject set = businesses.getJSONObject(i);
			         
			        // Get business name
			        String name = set.getString(TAG_NAME);
			        JSONObject location = set.getJSONObject(TAG_LOCATION);
			        JSONArray addressArray = location.getJSONArray(TAG_ADDRESS);
			        String address = addressArray.getString(0);
			        
			        
			        Log.d("NAME", name);
			        Log.d("ADDRESS", address);
			        
			        // Add to hash table
			        HashMap<String, String> map = new HashMap<String, String>();
			        map.put("name", name);
			        map.put("address", address);
			        businessList.add(map);
				}
	            
	            return businessList;
	        } catch (Exception e) {
	            return null;
	        }
	    }

	    protected void onPostExecute(ArrayList<HashMap<String, String>> bussinessList) {
	    	ListView businessListObj = (ListView) findViewById(R.id.bussinessListView);
	    	
	    	ListAdapter adapter = new SimpleAdapter(SearchActivity.this, bussinessList,
	                R.layout.business_list_item,
	                new String[] { TAG_NAME, TAG_ADDRESS }, new int[] {
	                        R.id.name, R.id.address});
	 
	    	businessListObj.setAdapter(adapter);
	    	businessListObj.setClickable(true);
	    	businessListObj.setOnItemClickListener(new AdapterView.OnItemClickListener() {

	    		@Override
	    		  public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3) {
	    			  Intent detailsIntent = new Intent(SearchActivity.this, DetailActivity.class);
	    			  detailsIntent.putExtra("EXTRA_NAME", ((TextView) arg1.findViewById(R.id.name)).getText());
	    			  detailsIntent.putExtra("EXTRA_ADDRESS", ((TextView) arg1.findViewById(R.id.address)).getText());
	    			  detailsIntent.putExtra("EXTRA_ID", ((TextView) arg1.findViewById(R.id.businessId)).getText());
	    			  
  			  	  startActivity(detailsIntent);
	    		  }
	    		});
	    }
	}

}
