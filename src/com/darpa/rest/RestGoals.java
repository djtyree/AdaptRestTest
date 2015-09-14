package com.darpa.rest;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.AsyncTask;

import com.google.android.gms.maps.model.LatLng;


public class RestGoals extends AsyncTask<String, String, JSONObject> {
	
	
	//URL to get JSON Array
    private static String url;
    private static int id;
    private static Activity act;
 
	//JSON Node Names
    private static final String rtag = "REST";
 
    JSONArray user = null;

    public RestGoals(String ip, int nid) {
		url = ip;
		id = nid;
	}
    
    public RestGoals(Activity main, String ip, int nid) {
		url = ip;
		id = nid;
		act = main;
	}

	@Override
	protected JSONObject doInBackground(String... params) {
		JSONParser jParser = new JSONParser();

		// Getting JSON from URL
		JSONObject json = jParser.getJSONFromUrl(url + "/nodes/" + id + "/goals");
		//Log.d(rtag, "JSON returned. json=" + json);
		try {
			// Getting JSON Array
			JSONArray goals = json.getJSONArray("goals");
			//Log.d(rtag, "GET Successful");
			
			ArrayList<LatLng> listdata = new ArrayList<LatLng>();   
			LatLng latlon;
			for (int i = 0; i < goals.length(); i++) {
				JSONObject goal = goals.getJSONObject(i);
				latlon = new LatLng(goal.getDouble("lat"), goal.getDouble("lon"));
				listdata.add(latlon);										
			}
			
			// used in uvade
			// Settings.setGoals(listdata);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return json;
	}
	
	@Override
	protected void onPostExecute(JSONObject result) {
		// TODO Auto-generated method stub
		super.onPostExecute(result);
		String msg = "Index: (Lat, Lon)\n";
		
		try {
			JSONArray goals = (JSONArray) result.get("goals");
			for(int i =0; i< goals.length(); i++) {
				JSONObject goal = (JSONObject) goals.get(i);
				msg += i + ": (" + goal.getString("lat") + ", " + goal.getString("lon") + ")\n";				
			}
			
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		AlertDialog alertDialog = new AlertDialog.Builder(act).create();
		alertDialog.setTitle("Goals");
		alertDialog.setMessage(msg);
		alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
		    new DialogInterface.OnClickListener() {
		        public void onClick(DialogInterface dialog, int which) {
		            dialog.dismiss();
		        }
		    });
		alertDialog.show();
	}
}
