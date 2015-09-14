package com.darpa.adaptresttest;

import java.util.List;

import kdAlgorithm2.KDTree;
import android.app.Activity;
import android.content.Context;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.darpa.rest.RestHelper;
import com.google.android.gms.maps.model.LatLng;

public class MainActivity extends Activity {
	private LocationManager lm; 
	private Location location;
	private TextView editLat, editLon;
	private RestHelper rest;
	private static final String RESTURL = "http://192.168.0.156:5000/rest/api";
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		lm = (LocationManager)getSystemService(Context.LOCATION_SERVICE); 		
		editLat = (TextView) findViewById(R.id.editLocationLat);
		editLon = (TextView) findViewById(R.id.editLocationLon);
		
		rest = new RestHelper(this, RESTURL, 1);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	public void curLocOnClick(View view) {
		Toast.makeText(MainActivity.this, "Getting Location", Toast.LENGTH_SHORT).show();
		Location location= lm.getLastKnownLocation(LocationManager.GPS_PROVIDER);
		showMyAddress(location);
	}
	
	public void locUploadOnClick(View view) {
		Toast.makeText(MainActivity.this, "Sending Location", Toast.LENGTH_SHORT).show();
		Location loc = getLocation();
		
		rest.pushLocation(loc);
		
	}
	
	public void goalsOnClick(View view) {
		rest.pullGoals();
		Toast.makeText(MainActivity.this, "Button Clicked", Toast.LENGTH_SHORT).show();
	}
	
	public void uploadJPsOnClick(View view) {
		Location loc = getLocation();
		List<LatLng> jps = rest.genJPs(5, loc.getLatitude(), loc.getLongitude());
		rest.pushJumpPoints(jps);		
		Toast.makeText(MainActivity.this, "JumpPoints Sent", Toast.LENGTH_SHORT).show();
	}
	
	public void createTreeOnClick(View view) {
		Location loc = getLocation();
		KDTree<Integer> map = rest.genTree(500, loc.getLatitude(), loc.getLongitude());
		rest.pushObstacles(map);		
		Toast.makeText(MainActivity.this, "Map Generated", Toast.LENGTH_SHORT).show();
	}
	
	private void showMyAddress(Location location) {
	    double latitude = location.getLatitude();
	    double longitude = location.getLongitude();
	    editLat.setText(Double.toString(latitude));
	    editLon.setText(Double.toString(longitude));	    
	}
	
	private Location getLocation() {
		Location loc;
		String strLat = editLat.getText().toString();
		String strLon = editLon.getText().toString();		
		
		if((strLat == null || strLat.isEmpty()) || (strLon == null || strLon.isEmpty())) {
			loc= lm.getLastKnownLocation(LocationManager.GPS_PROVIDER);
			showMyAddress(loc);
		} else {
			Double lat = Double.parseDouble(strLat);
			Double lon = Double.parseDouble(strLon);
			loc = new Location("Current Location");
			loc.setLatitude(lat);
			loc.setLongitude(lon);
			loc.setSpeed((float) 5.2);
			loc.setBearing((float) 120);
		}		
		return loc;		
	}
	
}
