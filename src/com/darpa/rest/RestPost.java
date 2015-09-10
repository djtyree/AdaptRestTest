package com.darpa.rest;

import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.StatusLine;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONObject;

import android.os.AsyncTask;
import android.util.Log;

public class RestPost extends AsyncTask<String, String, JSONObject> {
	private int postType;
    private HashMap<String, String> mData = null;// post data
    private JSONObject jObject = null;
    private static final String rtag = "REST";
    private static final int POST_TYPE_MAP = 0;
    private static final int POST_TYPE_JSON = 1;
    
    /**
     * constructor
     */
    public RestPost(HashMap<String, String> data) {
        mData = data;
        postType =  POST_TYPE_MAP;
    //    Log.d(rtag, "Construtor: " + data.toString());
    }

    public RestPost(JSONObject data) {
    	jObject = data;
        postType =  POST_TYPE_JSON;
    }
    
    /**
     * background
     */
    @Override
    protected JSONObject doInBackground(String... params) {
        byte[] result = null;
        JSONObject json = null;
        String str = "";
        HttpClient client = new DefaultHttpClient();
        HttpPut put = new HttpPut(params[0]);// in this case, params[0] is URL
        try {
            // set up post data
        	
        	if(postType == POST_TYPE_MAP) {
				ArrayList<NameValuePair> nameValuePair = new ArrayList<NameValuePair>();
				Iterator<String> it = mData.keySet().iterator();
				while (it.hasNext()) {
					String key = it.next();
					nameValuePair.add(new BasicNameValuePair(key, mData.get(key)));
				}
				put.setEntity(new UrlEncodedFormEntity(nameValuePair, "UTF-8"));
				
			} else if(postType == POST_TYPE_JSON) {
		//		Log.d(rtag, jObject.toString());
				put.setEntity( new StringEntity(jObject.toString(), "UTF-8"));
				
				//put.setHeader("Accept", "application/json");
				//put.setHeader("Content-type", "application/json");
				
			}
            HttpResponse response = client.execute(put);
            
            StatusLine statusLine = response.getStatusLine();
            if(statusLine.getStatusCode() == HttpURLConnection.HTTP_OK){
                result = EntityUtils.toByteArray(response.getEntity());
                str = new String(result, "UTF-8");                
                json = new JSONObject(str); //Convert String to JSON Object
                JSONObject jsonResult = json.getJSONObject("result");
                String msg = jsonResult.getString("msg");
       //         Log.d(rtag, msg);
            }
            Log.d(rtag, "Post Successful");
        }
        catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        catch (Exception e) {
        }
        
        return json;
    }
}