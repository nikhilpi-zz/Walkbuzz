package com.example.android.BluetoothChat;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.gesture.Prediction;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;
import android.widget.Toast;

import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.extensions.android.json.AndroidJsonFactory;
import com.google.api.client.http.GenericUrl;
import com.google.api.client.http.HttpRequestInitializer;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.JsonObjectParser;
import com.google.api.client.util.Key;

import org.apache.http.HttpRequest;
import org.apache.http.HttpRequestFactory;
import org.apache.http.HttpResponse;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Created by Shikhar on 3/24/14.
 */
public class SourceDestination extends Activity {
    //Debugging
    private TextView locationdata; //current location
    private Location currLoc; // current location object
    private double startLat;
    private double startLon;
    private EditText destination;
    final static String TAG = "SrcDest";

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.source_dest);

        locationdata = (TextView) findViewById(R.id.location);
        destination = (EditText) findViewById(R.id.dest);

        //Start Bluetooth Chat

        Button btn = (Button) findViewById(R.id.bluebutton);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(SourceDestination.this, BluetoothChat.class);
                startActivity(intent);
            }
        });

        //End Bluetooth Chat


        //Current Location Listener

        LocationManager manager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        LocationListener listen = new MyLocationListener();
        currLoc = new Location(LocationManager.GPS_PROVIDER);
        manager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, listen);

        //End Current Location Listener

        //Get Directions Button
        Button getDir = (Button) findViewById(R.id.getdirections);
        getDir.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(SourceDestination.this, GetDirections.class);
                String dest = destination.getText().toString();
                String curr = locationdata.getText().toString();
                //Log.d(TAG,curr);
                i.putExtra("current",curr); //send info to GetDirections
                i.putExtra("destination",dest);
                startActivity(i);
            }
        });



    }

     /* My Location Listener */

    public class MyLocationListener implements LocationListener

    {
        @Override

        public void onLocationChanged(Location loc)

        {
            //Need in this format for Google Maps API
            String strLatitude = loc.convert(loc.getLatitude(), loc.FORMAT_SECONDS);
            String strLongitude = loc.convert(loc.getLongitude(), loc.FORMAT_SECONDS);

            startLat = loc.getLatitude();
            startLon = loc.getLongitude();


            currLoc = loc;


            //Convert Lat/Lon to address
            Geocoder coder = new Geocoder(getApplicationContext());
            ArrayList<Address> addresses = null; //store addresses
            try {
                addresses = (ArrayList<Address>) coder.getFromLocation(currLoc.getLatitude(),currLoc.getLongitude(),1);
            } catch (IOException e) {
                e.printStackTrace();
            }
            if (addresses != null && addresses.size() > 0) {
                //Toast.makeText(getApplicationContext(), "country: " + addresses.get(0).getCountryName(), Toast.LENGTH_LONG).show();

                String txt = "Current Location: \n" + addresses.get(0).getAddressLine(0) + "\n" + addresses.get(0).getAddressLine(1) + ", " + addresses.get(0).getAddressLine(2);
                locationdata.setText(txt);

            }
        }


        @Override

        public void onProviderDisabled(String provider)

        {
            Toast.makeText(getApplicationContext(), "GPS Disabled", Toast.LENGTH_SHORT).show();
        }


        @Override

        public void onProviderEnabled(String provider)

        {
            Toast.makeText(getApplicationContext(), "GPS Enabled", Toast.LENGTH_SHORT).show();
        }


        @Override

        public void onStatusChanged(String provider, int status, Bundle extras)

        {
        }

    }/* End of MyLocationListener */








}


/* End of SourceDestination Activity */
