package com.example.android.BluetoothChat;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;
import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.extensions.android.json.AndroidJsonFactory;
import com.google.api.client.http.GenericUrl;
import com.google.api.client.http.HttpRequest;
import com.google.api.client.http.HttpRequestFactory;
import com.google.api.client.http.HttpRequestInitializer;
import com.google.api.client.http.HttpResponse;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.JsonObjectParser;
import com.google.api.client.util.Key;

import org.apache.http.HttpEntity;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


/**
 * Created by Shikhar on 3/27/14.
 */
public class GetDirections extends Activity {

    TextView next;
    TextView start;
    String destination;
    String currentLoc;


    //JSON Connection Info

    final static String URL = "https://maps.googleapis.com/maps/api/directions/json";
    HttpClient client;


    //JSON Steps Array
    JSONArray stepsArr = null;

    //Location (Steps) Data Structure info
    ArrayList<HashMap<String, String>> Steps; //Each step (hashmap) has a Start Location (lat, lon) and End Location (lat,lon)

    //JSON Tags
    private static final String TAG_START = "start_location";
    private static final String TAG_START_LAT = "lat";
    private static final String TAG_START_LNG = "lng";
    private static final String TAG_END = "end_location";
    private static final String TAG_END_LAT = "lat";
    private static final String TAG_END_LNG = "lng";



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.directions);

        start = (TextView) findViewById(R.id.currentLocation);
        next = (TextView) findViewById(R.id.endLoc);


        Intent intent = this.getIntent();
        destination = intent.getExtras().getString("destination");

        Intent i = this.getIntent();
        currentLoc = i.getExtras().getString("current");
//        Log.d(URL,currentLoc);

        start.setText(currentLoc);
        next.setText("\n" + "End Location\n" + destination);


        client = new DefaultHttpClient();
    }

    //FetchDirections - Pulls JSONArray "legs" from URL
    public JSONArray FetchDirections (String origin, String destination) throws ClientProtocolException, IOException, JSONException {
        GenericUrl url = new GenericUrl(URL);
        url.put("origin", origin);
        url.put("destination", destination);
        url.put("mode", "walking");
        url.put("sensor", false);

        HttpGet get = new HttpGet(url.toString());
        org.apache.http.HttpResponse r = client.execute(get);
        int status = r.getStatusLine().getStatusCode();
        if(status == 200){
            HttpEntity e = r.getEntity();
            String data = EntityUtils.toString(e);
            JSONObject obj = new JSONObject(data);
            JSONArray routes = obj.getJSONArray("routes");
            JSONObject firstRoute = routes.getJSONObject(0);
            JSONArray legs = firstRoute.getJSONArray("legs");
            JSONObject obj2 = legs.getJSONObject(0);
            JSONArray stepsArr = obj2.getJSONArray("steps");
            return stepsArr;
        }
        else{
            Toast.makeText(getApplicationContext(), "ERROR " + status, Toast.LENGTH_SHORT);
            return null;

        }
    }

    //Async Task to Populate LatLng Data Structure



}
