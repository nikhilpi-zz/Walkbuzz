package com.example.android.BluetoothChat;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
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
 * This activity allows the user to add their destination address
 * It also pulls the current location of the phone (MyLocationListener)
 * and uses GeoCoder to get an address based on LatLng
 *
 * Additional button "Go to Bluetooth" starts up the Bluetooth Chat activity (Demo purposes)
 *
 */
public class SourceDestination extends Activity {
    //Debugging
    private TextView locationdata; //current location
    private Location currLoc; // current location object
    private double startLat;
    private double startLon;
    private EditText destination;
    final static String TAG = "SrcDest";

    //Use geocoder (1), 0 - default location used
    final private static int GEOC = 1;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.source_dest);

        locationdata = (TextView) findViewById(R.id.location);
        destination = (EditText) findViewById(R.id.dest);

        locationdata.setText("711 Church St, Evanston, IL 60201");

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

            //Convert Lat/Lon to address

            if(GEOC == 1) {
                //Server seems to be down - Update later
                Geocoder coder = new Geocoder(getApplicationContext(), Locale.getDefault());

                try {
                    List<Address> addresses = coder.getFromLocation(loc.getLatitude(), loc.getLongitude(), 1);

                    if (addresses != null && addresses.size() > 0) {

                        String txt = "Current Location: \n" + addresses.get(0).getAddressLine(0) + "\n" + addresses.get(0).getAddressLine(1) + ", " + addresses.get(0).getAddressLine(2);
                        locationdata.setText(txt);

                    }


                } catch (IOException e) {
                    e.printStackTrace();
                }
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
