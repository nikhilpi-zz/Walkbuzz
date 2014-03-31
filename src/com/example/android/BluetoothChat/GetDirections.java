package com.example.android.BluetoothChat;


import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import com.google.api.client.http.GenericUrl;

import org.apache.http.HttpEntity;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;


/**
 * Created by Shikhar on 3/27/14.
 */
public class GetDirections extends Activity {

        TextView ending;
        TextView start;
        TextView duration;
        TextView distance;

        String destination;
        String currentLoc;
        String time;
        String dist;


        //JSON Connection Info

        final static String URL = "https://maps.googleapis.com/maps/api/directions/json";
        HttpClient client;


        //JSON Steps Array
        JSONArray stepsArr = null;

        //Location (Steps) Data Structure info
        ArrayList<Step> stepsList; //Each step has a Start Location (lat, lon) and End Location (lat,lon) and angle and compass direction

        //JSON Tags
        private static final String TAG_START = "start_location";
        private static final String TAG_START_LAT = "lat";
        private static final String TAG_START_LNG = "lng";
        private static final String TAG_END = "end_location";
        private static final String TAG_END_LAT = "lat";
        private static final String TAG_END_LNG = "lng";
        private static final String TAG_DIST = "distance";
        private static final String TAG_TIME = "duration";
        private static final String TAG_TEXT = "text";



    @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.directions);

            start = (TextView) findViewById(R.id.currentLocation);
            ending = (TextView) findViewById(R.id.endLoc);
            distance = (TextView) findViewById(R.id.duration);
            duration = (TextView) findViewById(R.id.distance);

            Intent intent = this.getIntent();
            destination = intent.getExtras().getString("destination");

            Intent i = this.getIntent();
            currentLoc = i.getExtras().getString("current");
    //        Log.d(URL,currentLoc);

            //Initialize textviews

            start.setText("\n" + "Start Location\n" + currentLoc);
            ending.setText("\n" + "End Location\n" + destination);

            stepsList = new ArrayList<Step>();
            client = new DefaultHttpClient();

            new Read().execute();



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


                //set Distance and Duration values
                JSONObject obj2 = legs.getJSONObject(0);
                JSONObject dur = obj2.getJSONObject("duration");
                time = dur.getString("text");
                //Log.d(TAG_START,time);

                JSONObject len = obj2.getJSONObject("distance");
                dist = len.getString("text");






                //return the Steps array to background thread
                JSONArray arr = obj2.getJSONArray("steps");
                return arr;
            }
            else{
                Toast.makeText(getApplicationContext(), "ERROR " + status, Toast.LENGTH_SHORT);
                return null;

            }
        }



        public class Step{
            double start_lat;
            double start_lon;
            double end_lat;
            double end_lon;
            int angle;
            String comp_direction;


            public Step(double slat, double slon, double elat, double elon){
                start_lat = slat;
                start_lon = slon;
                end_lat = elat;
                end_lon = elon;
                angle = 0;
                comp_direction = "";
            }




        }

        //Async Task to Populate LatLng Data Structure

        private class Read extends AsyncTask<Void, Void, Void> {


            @Override
            protected Void doInBackground(Void... voids) {

                try {
                    stepsArr = FetchDirections(currentLoc, destination);

                    //create arraylist hash from JSONArray
                    for (int i = 0; i < stepsArr.length(); i++) {

                        //get i-th step
                        JSONObject step = stepsArr.getJSONObject(i);

                        //start step info
                        JSONObject startLocation = step.getJSONObject(TAG_START);
                        double startLat = startLocation.getDouble(TAG_START_LAT);
                        double startLon = startLocation.getDouble(TAG_START_LNG);

                        //end of step info

                        JSONObject endLocation = step.getJSONObject(TAG_END);
                        double endLat = endLocation.getDouble(TAG_END_LAT);
                        double endLon = endLocation.getDouble(TAG_START_LNG);

                        //create Step
                        Step s = new Step(startLat, startLon, endLat, endLon);

                        //add to global list of steps

                        stepsList.add(i, s);
                    }


                } catch (IOException e) {
                    e.printStackTrace();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                return null;
            }


            //Process angles or direction and send to Bluetooth device
            @Override
            protected void onPostExecute(Void aVoid) {
                super.onPostExecute(aVoid);

                distance.setText("\n" + "Distance\n" + dist);
                duration.setText("\n" + "Duration\n" + time);

            }
        }

}
