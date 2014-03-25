package com.example.android.BluetoothChat;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;

/**
 * Created by home on 3/24/14.
 */
public class SourceDestination extends Activity {

       @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.source_dest);


        Button btn = (Button) findViewById(R.id.bluebutton);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(SourceDestination.this,BluetoothChat.class);
                startActivity(intent);
            }
        });

           LocationManager manager = (LocationManager)getSystemService(Context.LOCATION_SERVICE);
           LocationListener listen = new MyLocationListener();

           manager.requestLocationUpdates( LocationManager.GPS_PROVIDER, 0, 0, listen);


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

            double lat = loc.getLatitude();
            double lon = loc.getLongitude();

            String txt = "" + strLatitude + "\n " + strLongitude;
            TextView locationdata = (TextView) findViewById(R.id.location);
            locationdata.setText(txt);

        }


        @Override

        public void onProviderDisabled(String provider)

        {

            Toast.makeText( getApplicationContext(),"GPS Disabled", Toast.LENGTH_SHORT ).show();

        }


        @Override

        public void onProviderEnabled(String provider)

        {

            Toast.makeText(getApplicationContext(),"GPS Enabled", Toast.LENGTH_SHORT).show();

        }


        @Override

        public void onStatusChanged(String provider, int status, Bundle extras)

        {


        }

    }/* End of Class MyLocationListener */


}


/* End of UseGps Activity */
