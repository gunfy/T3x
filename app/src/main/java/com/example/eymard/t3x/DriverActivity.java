package com.example.eymard.t3x;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.os.Handler;
import android.os.Message;
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
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;



import java.util.concurrent.ExecutionException;

public class DriverActivity extends AppCompatActivity implements OnMapReadyCallback, GoogleMap.OnMarkerClickListener {

    final int RQS_GooglePlayServices = 1;
    GoogleMap myMap;
    Marker myMarker,myMarker2;
    String title = null;
    DrawerLayout mDrawerLayout;
    TextView username_profil;
    static int mode=0;   //0 normal/1 driver
    int user_id;
    Location myLocation = null;
    JSONObject myCourse=new JSONObject();
    JSONArray donneesCourse= new JSONArray();
    Boolean ordre=false;
    Boolean send=false;
    //String depart,arrivee;
    LatLng depart,arrivee;




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
                        //finish();
                        startActivity(new Intent(DriverActivity.this,MainActivity.class));

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

            map.moveCamera(CameraUpdateFactory.newLatLngZoom(defaut, 16));

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
                map.addMarker(new MarkerOptions()
                        .position(def)
                        .draggable(false)
                        .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE))
                );

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

    }

    public void gestionFab(){


        //fab call
        findViewById(R.id.fab_call_dr).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        //fab annulee
        findViewById(R.id.fab_cancel_dr).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

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



}
