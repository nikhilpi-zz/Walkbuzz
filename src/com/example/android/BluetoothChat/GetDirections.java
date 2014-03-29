package com.example.android.BluetoothChat;


import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;


/**
 * Created by Shikhar on 3/27/14.
 */
public class GetDirections extends Activity {

    final static String URL = "http://maps.googleapis.com/maps/api/directions/json";
    TextView next;
    TextView start;
    String destination;
    String currentLoc;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.directions);

        next = (TextView) findViewById(R.id.test);
        start = (TextView) findViewById(R.id.location);

        Intent intent = this.getIntent();
        destination = intent.getExtras().getString("destination");
        next.setText(destination);

    }




}
