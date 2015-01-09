package csee.usf.edu;

import java.util.ArrayList;
import java.util.HashMap;

import org.json.JSONArray;
import org.json.JSONObject;

import android.util.Log;

public class JSONParser {
	private String stringToParse;
	public static final String TAG_BUSINESSES = "businesses";
	public static final String TAG_NAME = "name";
	public static final String TAG_ADDRESS = "address";
	public static ArrayList<HashMap<String, String>> list;
	
	
	public JSONParser(String yelpResponse) {
		this.stringToParse = yelpResponse;
	}
	
	public ArrayList<HashMap<String, String>> businessList() {
		try {
			
			// Get JSON object
	        JSONObject jObject = new JSONObject(stringToParse);
	        
	        // Getting array of businesses
			JSONArray businesses = jObject.getJSONArray(TAG_BUSINESSES);
	    
			// Loop through each business and add it to hash table
			for(int i = 0; i < businesses.length(); i++) {
		        JSONObject set = businesses.getJSONObject(i);
		         
		        // Get business name
		        String name = set.getString("name");
		        JSONObject location = set.getJSONObject("location");
		        JSONArray addressArray = location.getJSONArray("address");
		        String address = addressArray.getString(0);
		        
		        
		        Log.d("NAME", name);
		        Log.d("ADDRESS", address);
		        
		        // Add to hash table
		        HashMap<String, String> map = new HashMap<String, String>();
		        map.put("name", name);
		        map.put("address", address);
		        list.add(map);
			}
			return list;
		} catch (Exception e) {
			return null;
		}
    }
}
