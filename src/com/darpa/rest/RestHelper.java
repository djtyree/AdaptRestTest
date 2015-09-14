package com.darpa.rest;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;
import java.util.Set;

import kdAlgorithm2.KDTree;
import kdAlgorithm2.KeyDuplicateException;
import kdAlgorithm2.KeySizeException;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.location.Location;
import android.os.Bundle;

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
	private KDTree<Integer> map;
	
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
		this.map=new KDTree<Integer>(2);
		
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
		data.put("speed", Float.toString(curLocation.getSpeed()));
		data.put("dir", Float.toString(curLocation.getBearing()));
		RestPost asyncHttpPost = new RestPost(data);
		asyncHttpPost.execute(url + "/nodes/" + id + "/location");
	}
	
	public void pushForces(Float speed, Float bearing) {
		HashMap<String, String> data = new HashMap<String, String>();
		data.put("force_speed", Float.toString(speed));
		data.put("force_dir", Float.toString(bearing));
		RestPost asyncHttpPost = new RestPost(data);
		asyncHttpPost.execute(url + "/nodes/" + id + "/force");
	}
	
	public void pushJumpPoints(List<LatLng> jumpPoints) {
		HashMap<String, String> data = buildJumpPoints(jumpPoints);
		RestPost asyncHttpPost = new RestPost(data);
		asyncHttpPost.execute(url + "/nodes/" + id + "/jumppoints");
	}

	public void pushObstacles(KDTree<Integer> map) {
		HashMap<String, String> data = mapToJSON(map);		
		RestPost asyncHttpPost = new RestPost(data);
		asyncHttpPost.execute(url + "/obstacles");
		
	}
	
	public HashMap<String, String> buildJumpPoints(List<LatLng> sinkList) {
		JSONArray jArray = new JSONArray();
		HashMap<String, String> data = new HashMap<String, String>();
		for(int i=0; i < sinkList.size(); i++) {
			jArray.put(buildJumpPoint(sinkList.get(i)));
		}
		data.put("jps", jArray.toString());
		return data;		
	}
	
	public JSONObject buildJumpPoint(LatLng sink) {
		JSONObject jp = new JSONObject();
		try {
			jp.put("lat", Double.toString(sink.latitude));
			jp.put("lng", Double.toString(sink.longitude));
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return jp;

	}
	
	public KDTree<Integer> genTree(int points, double lat, double lon) {
		Random r = new Random();
		for(int i=-0; i < points; i++) {
			double rlat = (lat-.5 + (i/10)) + .5 * r.nextDouble();
			double rlon = (lon-.5 + (i/10)) + .5 * r.nextDouble();
			double[] key = new double[]{rlat, rlon};
			try {
				map.insert(key, 1);
			} catch (KeySizeException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (KeyDuplicateException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return map;		
	}
	
	public List<LatLng> genJPs(int points, double lat, double lon) {
		List<LatLng> jps = new ArrayList<LatLng>();
		Random r = new Random();
		for(int i=-0; i < points; i++) {
			double rlat = (lat-.5 + (i/10)) + .5 * r.nextDouble();
			double rlon = (lon-.5 + (i/10)) + .5 * r.nextDouble();
			LatLng jp = new LatLng(rlat, rlon);
			jps.add(jp);
		}
		return jps;		
	}
	
	public HashMap<String, String> mapToJSON(KDTree<Integer> map) {
		HashMap<String, String> data = new HashMap<String, String>();
		JSONArray json = new JSONArray();
		
		Bundle bundle = map.writeToBundle();		
		Set<String> keys = bundle.keySet();
		
		for (String key : keys) {
		    try {
		    	// json.put(key, bundle.get(key)); see edit below
		    	double[] point = new double[2];
		    	if(key.startsWith("key")) {
		    		point = bundle.getDoubleArray(key);
			    	if(point != null) {
			    		JSONObject obj = new JSONObject();
			    		obj.put("lat", Double.toString(point[0]));
			    		obj.put("lon", Double.toString(point[1]));
			    		json.put(obj);
			    	}
		    	}
		    } catch(JSONException e) {
		        //Handle exception here
		    }
		}
		data.put("obstacles", json.toString());
		return data;
	}

	
}
