package com.example.android.BluetoothChat;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
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
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Created by home on 3/24/14.
 */
public class SourceDestination extends Activity {
    //Debugging
    private TextView locationdata;
    private Location currLoc; // current location
    private double startLat;
    private double startLon;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.source_dest);

        locationdata = (TextView) findViewById(R.id.location);


        Button btn = (Button) findViewById(R.id.bluebutton);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(SourceDestination.this, BluetoothChat.class);
                startActivity(intent);
            }
        });

        LocationManager manager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        LocationListener listen = new MyLocationListener();
        currLoc = new Location(LocationManager.GPS_PROVIDER);
        manager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, listen);




    }


    /* Class My Location Listener */

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

    }/* End of Class MyLocationListener */


}


/* End of UseGps Activity */
