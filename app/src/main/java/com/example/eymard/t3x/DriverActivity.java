package com.example.eymard.t3x;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.Typeface;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.support.v7.widget.Toolbar;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.concurrent.ExecutionException;

public class DriverActivity extends AppCompatActivity implements OnMapReadyCallback, GoogleMap.OnMarkerClickListener,GoogleMap.OnInfoWindowClickListener, LocationListener {

    final int RQS_GooglePlayServices = 1;
    GoogleMap myMap;
    Marker myMarker;
    String title = null;
    DrawerLayout mDrawerLayout;
    TextView username_profil;
    int user_id;
    Location myLocation = null;
    JSONObject myCourse=new JSONObject();
    JSONArray donneesCourse= new JSONArray();
    Boolean isOk=true;
    Boolean notBusy=true;
    Boolean abord=true;
    LatLng arrivee;
    private HashMap<Marker, JSONObject> mHashMap = new HashMap<Marker, JSONObject>();
    String number;
    ShakeListener mShaker;





    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_driver);

        //on recupere les widgets
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        username_profil = (TextView) findViewById(R.id.tv_username_profil);
        gestionFab();

        //on recupere id et username du sharedpreferences
        SharedPreferences sharedpreferences = getSharedPreferences(LoginActivity.MyPREFERENCES, Context.MODE_PRIVATE);
        username_profil.setText(sharedpreferences.getString("username", "username"));
        user_id=sharedpreferences.getInt("user_id", 0);


        Toolbar myToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(myToolbar);
        setTitle("Driver");
        ActionBar actionBar = getSupportActionBar();
        try {
            actionBar.setHomeAsUpIndicator(R.drawable.ic_menu);
        } catch (NullPointerException e) {
            Log.i("erreur_menu", "pas de fichier ic_menu");
        }

        try {
            actionBar.setDisplayHomeAsUpEnabled(true);
        } catch (NullPointerException e) {
            Log.i("erreur_menu", "pas d'actionBar");
        }

        //gestion navigation menu
        NavigationView navigationView = (NavigationView) findViewById(R.id.navigation_view);
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem menuItem) {
                menuItem.setChecked(true);
                mDrawerLayout.closeDrawers();
                switch (menuItem.getTitle().toString()) {
                    case "Home":
                        finish();
                        startActivity(new Intent(DriverActivity.this,MainActivity.class));
                        break;

                    case "Profile":
                        //finish();
                        startActivity(new Intent(DriverActivity.this, ProfilActivity.class));

                        break;
                    case "Logout":
                        //finish();
                        //startActivity(new Intent(MainActivity.this, LoginActivity.class));
                        Intent intent = new Intent(DriverActivity.this, LoginActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);
                        break;

                    case "Driver mode":
                        startActivity(new Intent(DriverActivity.this, DriverActivity.class));
                        break;

                    default:
                        Toast.makeText(DriverActivity.this, menuItem.getTitle(), Toast.LENGTH_LONG).show();

                }
                return true;
            }
        });


        //on récupère le fragment définit dans le layout qui va contenir la carte
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        myMap = mapFragment.getMap();

        myMap.setMyLocationEnabled(true);

        // Getting LocationManager object from System Service LOCATION_SERVICE
        LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

        // Creating a criteria object to retrieve provider
        //Criteria criteria = new Criteria();

        // Getting the name of the best provider
        //String provider = locationManager.getBestProvider(criteria, true);
        // Getting Current Location
        if (ContextCompat.checkSelfPermission(this,android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    public void requestPermissions(@NonNull String[] permissions, int requestCode)
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for Activity#requestPermissions for more details.
            return;
        }
        myLocation = locationManager.getLastKnownLocation("network");



        //myMap.setOnMapClickListener(this);
        //myMap.setOnMapLongClickListener(this);
        myMap.setOnInfoWindowClickListener(this);

        mShaker = new ShakeListener(this);
        mShaker.setOnShakeListener(new ShakeListener.OnShakeListener() {
            public void onShake() {

                if(notBusy) {
                    Toast.makeText(DriverActivity.this, getString(R.string.msg_dr_maj), Toast.LENGTH_SHORT).show();
                    finish();
                    startActivity(new Intent(DriverActivity.this,DriverActivity.class));
                }


            }
        });


    }


    @Override
    public void onMapReady(GoogleMap map) {

        setUpMap(map);

        //on définit notre marqueur
        if (myLocation==null){
            LatLng mtp = new LatLng(43.6, 3.8833);

            map.moveCamera(CameraUpdateFactory.newLatLngZoom(mtp, 13));
        }else {
            LatLng defaut=new LatLng(myLocation.getLatitude(),myLocation.getLongitude());

            map.moveCamera(CameraUpdateFactory.newLatLngZoom(defaut, 15));

        }

        //on recupere les données
        JSONObject json = new JSONObject();
        try {
            json.put("ctrl","allCourse");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        HTTPRequest myR= new HTTPRequest(json.toString());
        try {

            String reponse =myR.execute().get();
            donneesCourse=new JSONArray(reponse);


        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }

        Log.i("******* donnes", donneesCourse.toString());

        for(int i=0;i<donneesCourse.length();i++){
            JSONObject jsonOb=null;
            try {
                jsonOb=donneesCourse.getJSONObject(i);
                //Log.i("******* emplacements", jsonOb.getString("depart"));
                String [] tab= mySplit(jsonOb.getString("depart"));
                LatLng def= new LatLng(Double.parseDouble(tab[0]),Double.parseDouble(tab[1]));
                Log.i("******* position marker", def.toString());
                Marker marker=map.addMarker(new MarkerOptions()
                                .position(def)
                                .draggable(false)
                                .title(jsonOb.getString("descDep"))
                                .snippet("Username : " + jsonOb.getString("username") + "\n" + "Destination : " + jsonOb.getString("descArr"))
                                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE))
                );
                mHashMap.put(marker, jsonOb);

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }




    }



    @Override
    public boolean onMarkerClick(Marker marker) {
        return false;
    }





    @Override
    protected void onResume() {
        // TODO Auto-generated method stub
        super.onResume();

        int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(getApplicationContext());

        if (resultCode == ConnectionResult.SUCCESS){
            Toast.makeText(getApplicationContext(),
                    "isGooglePlayServicesAvailable SUCCESS",
                    Toast.LENGTH_LONG).show();
        }else{
            GooglePlayServicesUtil.getErrorDialog(resultCode, this, RQS_GooglePlayServices);
        }

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        switch (id) {
            case android.R.id.home:
                mDrawerLayout.openDrawer(GravityCompat.START);
                return true;
            case R.id.action_settings:
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /*
    Toutes les options de ma map
    */
    public void setUpMap(GoogleMap map)
    {
        //affichage de ma localisation
        map.setMyLocationEnabled(true);


        map.setInfoWindowAdapter(new GoogleMap.InfoWindowAdapter() {

            @Override
            public View getInfoWindow(Marker arg0) {
                return null;
            }

            @Override
            public View getInfoContents(Marker marker) {

                Context context = getApplicationContext(); //or getActivity(), YourActivity.this, etc.

                LinearLayout info = new LinearLayout(context);
                info.setOrientation(LinearLayout.VERTICAL);

                TextView title = new TextView(context);
                title.setTextColor(Color.BLACK);
                title.setGravity(Gravity.CENTER);
                title.setTypeface(null, Typeface.BOLD);
                title.setText(marker.getTitle());

                TextView snippet = new TextView(context);
                snippet.setTextColor(Color.GRAY);
                snippet.setText(marker.getSnippet());

                info.addView(title);
                info.addView(snippet);

                return info;
            }
        });

    }

    public void gestionFab(){


        //fab call
        findViewById(R.id.fab_call_dr).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isOk) {
                    Intent callIntent = new Intent(Intent.ACTION_CALL);
                    callIntent.setData(Uri.parse("tel:" + number));
                    startActivity(callIntent);
                }
            }
        });

        //fab ok
        findViewById(R.id.fab_ok_dr).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!notBusy){

                    Snackbar snackbar = Snackbar
                            .make(findViewById(R.id.coordinatorLayoutDr), getString(R.string.msg_dr_custom), Snackbar.LENGTH_LONG)
                            .setAction(getString(R.string.snack_dr_msg1), new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    //***************
                                    LatLng p = new LatLng(myLocation.getLatitude(), myLocation.getLongitude());
                                    // Getting URL to the Google Directions API
                                    String url = getDirectionsUrl(p, arrivee);
                                    myMap.addMarker(new MarkerOptions()
                                            .position(arrivee)
                                            .draggable(false)
                                            .title("Destination")
                                            .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE)));

                                    DownloadTask downloadTask = new DownloadTask();

                                    // Start downloading json data from Google Directions API
                                    downloadTask.execute(url);

                                    notBusy=true;
                                }
                            });
                    snackbar.show();

                }

            }
        });
    }

    public String [] mySplit(String s){
        String[] parts = s.split(" ");
        String part= parts[1];
        part = part.substring(1,part.length()-1);
        String[] ret= part.split(",");
        return ret;


    }


    @Override
    public void onInfoWindowClick(Marker marker) {
        if (notBusy) {
            final JSONObject json = mHashMap.get(marker);
            myMarker=marker;

            Snackbar snackbar = Snackbar
                    .make(findViewById(R.id.coordinatorLayoutDr), getString(R.string.snack_dr_msg), Snackbar.LENGTH_LONG)
                    .setAction(getString(R.string.snack_dr_msg1), new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            JSONObject obj = new JSONObject();
                            try {
                                obj.put("ctrl", "acceptCourse");
                                obj.put("id_course", json.getInt("id"));
                                obj.put("id_user", user_id);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                            HTTPRequest acceptcourse = new HTTPRequest(obj.toString());

                            String rep = "";
                            try {
                                rep = acceptcourse.execute().get();
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            } catch (ExecutionException e) {
                                e.printStackTrace();
                            }

                            String out = rep.replaceAll(" ", "");
                            if (out.equals("ok")) {
                                findViewById(R.id.fab_call_dr).setVisibility(View.VISIBLE);
                                findViewById(R.id.fab_ok_dr).setVisibility(View.VISIBLE);
                                notBusy=false;

                                try {
                                    number = json.getString("numero");
                                    String [] t= mySplit(json.getString("arrivee"));
                                    arrivee=new LatLng(Double.parseDouble(t[0]),Double.parseDouble(t[1]));

                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }

                                myMap.clear();
                                myMap.addMarker(new MarkerOptions()
                                                .position(myMarker.getPosition())
                                                .draggable(false)
                                                .title(myMarker.getTitle())
                                                .snippet(myMarker.getSnippet())
                                                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_YELLOW))
                                );


                                //***************
                                LatLng pos=new LatLng(myLocation.getLatitude(),myLocation.getLongitude());
                                // Getting URL to the Google Directions API
                                String url = getDirectionsUrl(pos, myMarker.getPosition());

                                DownloadTask downloadTask = new DownloadTask();

                                // Start downloading json data from Google Directions API
                                downloadTask.execute(url);

                                //**************

                                Toast.makeText(DriverActivity.this,"Good Driving",Toast.LENGTH_LONG);
                            } else {
                                Snackbar snackbar1 = Snackbar.make(findViewById(R.id.coordinatorLayoutDr), "error", Snackbar.LENGTH_SHORT);
                                snackbar1.show();
                                findViewById(R.id.fab_call_dr).setVisibility(View.GONE);
                                findViewById(R.id.fab_ok_dr).setVisibility(View.GONE);
                                isOk = false;
                            }

                        }
                    });
            snackbar.show();
        }
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        // Always call the superclass so it can save the view hierarchy state
        super.onSaveInstanceState(savedInstanceState);

        // Save the user's current state

    }


    private String getDirectionsUrl(LatLng origin,LatLng dest){

        // Origin of route
        String str_origin = "origin="+origin.latitude+","+origin.longitude;

        // Destination of route
        String str_dest = "destination="+dest.latitude+","+dest.longitude;

        // Sensor enabled
        String sensor = "sensor=false";

        // Building the parameters to the web service
        String parameters = str_origin+"&"+str_dest+"&"+sensor;

        // Output format
        String output = "json";

        // Building the url to the web service
        String url = "https://maps.googleapis.com/maps/api/directions/"+output+"?"+parameters;

        return url;
    }

    /** A method to download json data from url */
    private String downloadUrl(String strUrl) throws IOException {
        String data = "";
        InputStream iStream = null;
        HttpURLConnection urlConnection = null;
        try{
            URL url = new URL(strUrl);

            // Creating an http connection to communicate with url
            urlConnection = (HttpURLConnection) url.openConnection();

            // Connecting to url
            urlConnection.connect();

            // Reading data from url
            iStream = urlConnection.getInputStream();

            BufferedReader br = new BufferedReader(new InputStreamReader(iStream));

            StringBuffer sb = new StringBuffer();

            String line = "";
            while( ( line = br.readLine()) != null){
                sb.append(line);
            }

            data = sb.toString();

            br.close();

        }catch(Exception e){
            Log.d("Exception while downloading url", e.toString());
        }finally{
            iStream.close();
            urlConnection.disconnect();
        }
        return data;
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }

    @Override
    public void onLocationChanged(Location location) {
        myLocation=location;

    }



    // Fetches data from url passed
    private class DownloadTask extends AsyncTask<String, Void, String>{

        // Downloading data in non-ui thread
        @Override
        protected String doInBackground(String... url) {

            // For storing data from web service
            String data = "";

            try{
                // Fetching the data from web service
                data = downloadUrl(url[0]);
            }catch(Exception e){
                Log.d("Background Task",e.toString());
            }
            return data;
        }

        // Executes in UI thread, after the execution of
        // doInBackground()
        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);

            ParserTask parserTask = new ParserTask();

            // Invokes the thread for parsing the JSON data
            parserTask.execute(result);
        }
    }

    /** A class to parse the Google Places in JSON format */
    private class ParserTask extends AsyncTask<String, Integer, List<List<HashMap<String,String>>> > {

        // Parsing the data in non-ui thread
        @Override
        protected List<List<HashMap<String, String>>> doInBackground(String... jsonData) {

            JSONObject jObject;
            List<List<HashMap<String, String>>> routes = null;

            try{
                jObject = new JSONObject(jsonData[0]);
                DirectionsJSONParser parser = new DirectionsJSONParser();

                // Starts parsing data
                routes = parser.parse(jObject);
            }catch(Exception e){
                e.printStackTrace();
            }
            return routes;
        }

        // Executes in UI thread, after the parsing process
        @Override
        protected void onPostExecute(List<List<HashMap<String, String>>> result) {
            ArrayList<LatLng> points = null;
            PolylineOptions lineOptions = null;
            MarkerOptions markerOptions = new MarkerOptions();

            // Traversing through all the routes
            for(int i=0;i<result.size();i++){
                points = new ArrayList<LatLng>();
                lineOptions = new PolylineOptions();

                // Fetching i-th route
                List<HashMap<String, String>> path = result.get(i);

                // Fetching all the points in i-th route
                for(int j=0;j<path.size();j++){
                    HashMap<String,String> point = path.get(j);

                    double lat = Double.parseDouble(point.get("lat"));
                    double lng = Double.parseDouble(point.get("lng"));
                    LatLng position = new LatLng(lat, lng);

                    points.add(position);
                }

                // Adding all the points in the route to LineOptions
                lineOptions.addAll(points);
                lineOptions.width(12);
                lineOptions.color(Color.RED);
            }

            // Drawing polyline in the Google Map for the i-th route
            myMap.addPolyline(lineOptions);
        }
    }









}
