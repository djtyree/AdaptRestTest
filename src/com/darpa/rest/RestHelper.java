package com.darpa.rest;

import java.util.HashMap;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.location.Location;

import com.google.android.gms.maps.model.LatLng;

public class RestHelper {
	/* 
	 * -------------------------------------------
	 * Variables
	 * ------------------------------------------- 
	 */
	private static String url;
	private static int id;
	private static Activity act;
	private static final String tag = "REST";
	
	/* 
	 * -------------------------------------------
	 * Constructor
	 * ------------------------------------------- 
	 */
	public RestHelper(String base, int nid) {
		url = base;
		id = nid;
	}
	
	public RestHelper(Activity main, String base, int nid) {
		url = base;
		id = nid;
		act = main;
	}
	
	/* 
	 * -------------------------------------------
	 * Helpers 
	 * ------------------------------------------- 
	 */
	
	public void pullGoals() {
		if(act != null) {
			new RestGoals(act, url, id).execute();
		} else {
			new RestGoals(url, id).execute();
		}
	}
	
	public void pushLocation(Location curLocation) {
		HashMap<String, String> data = new HashMap<String, String>();
		data.put("lat", Double.toString(curLocation.getLatitude()));
		data.put("lon", Double.toString(curLocation.getLongitude()));
		RestPost asyncHttpPost = new RestPost(data);
		asyncHttpPost.execute(url + "/" + id + "/location");
	}
	
	public void pushJumpPoints(List<LatLng> jumpPoints) {
		HashMap<String, String> data = buildJumpPoints(jumpPoints);
		RestPost asyncHttpPost = new RestPost(data);
		asyncHttpPost.execute(url + "/" + id + "/jumppoints");
	}
/*
	public void pushMap(WorldMap map) {
		HashMap<String, String> data = buildMap(map);
		//RestPost asyncHttpPost = new RestPost(data);
		//asyncHttpPost.execute(url + "/" + Settings.selfID() + "/obstacles");
	//	Log.d(tag,"Pushed Map");
	}
*/	
	public HashMap<String, String> buildJumpPoints(List<LatLng> sinkList) {
		JSONArray jArray = new JSONArray();
		HashMap<String, String> data = new HashMap<String, String>();
		for(int i=0; i < sinkList.size(); i++) {
			jArray.put(buildJumpPoint(sinkList.get(i)));
		}
	//	data.put("jps", jArray.toString());
		return data;		
	}
	
	public JSONObject buildJumpPoint(LatLng sink) {
		JSONObject jp = new JSONObject();
		try {
			jp.put("lat", Double.toString(sink.latitude));
			jp.put("lng", Double.toString(sink.longitude));
	//		Log.d(tag,"JP Lat: " + Double.toString(sink.latitude));
	//		Log.d(tag,"JP Lng: " + Double.toString(sink.longitude));
		} catch (JSONException e) {
			e.printStackTrace();
		}
	//	Log.d(tag,"JumpPoint String: " + jp.toString());
		return jp;

	}
/*
	public HashMap<String, String> buildMap(WorldMap wMap) {
	//	Log.d(tag,"Building Map Map: " +  wMap.toString());
		HashMap<String, String> data = new HashMap<String, String>();
		//KDTree<Integer> kdtree = wMap.getMap();
		//ArrayList<double[]> objs = wMap.getCircularSubset(new LatLng(0.0,0.0), 100);
		//data.put("obstacles", kdtree.toString());
		//for(int i=0; i < objs.size(); i++) {
		//	Log.d(tag,"Map Objects: " +  objs.get(i).toString());	
		//}		
		return data;		
	}
*/	
	
}
