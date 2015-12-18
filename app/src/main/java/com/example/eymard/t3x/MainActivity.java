package com.example.eymard.t3x;

import android.location.Location;
import android.os.Handler;
import android.os.Message;
import android.support.design.widget.NavigationView;
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
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

public class MainActivity extends AppCompatActivity implements OnMapReadyCallback, GoogleMap.OnMapClickListener/*,
        GoogleMap.OnMapLongClickListener*/, GoogleMap.OnMarkerDragListener/*,GoogleMap.OnMarkerClickListener*/ {

    final int RQS_GooglePlayServices = 1;
    GoogleMap myMap;
    Marker myMarker;
    Button btLocInfo;
    String title=null;
    DrawerLayout mDrawerLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //on recupere le widget
        btLocInfo = (Button)findViewById(R.id.locinfo);
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);


        Toolbar myToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(myToolbar);
        setTitle("T3x");
        ActionBar actionBar = getSupportActionBar();
        try{
            actionBar.setHomeAsUpIndicator(R.drawable.ic_menu);
        }catch (NullPointerException e) {
            Log.i("erreur_menu","pas de fichier ic_menu");
        }

        try{
            actionBar.setDisplayHomeAsUpEnabled(true);
        }catch (NullPointerException e) {
            Log.i("erreur_menu","pas d'actionBar");
        }

        //gestion navigation menu
        NavigationView navigationView = (NavigationView) findViewById(R.id.navigation_view);
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem menuItem) {
                menuItem.setChecked(true);
                mDrawerLayout.closeDrawers();
                Toast.makeText(MainActivity.this, menuItem.getTitle(), Toast.LENGTH_LONG).show();
                return true;
            }
        });


        //on récupère le fragment définit dans le layout qui va contenir la carte
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        myMap=mapFragment.getMap();

        myMap.setMyLocationEnabled(true);

        myMap.setOnMapClickListener(this);
        //myMap.setOnMapLongClickListener(this);
        myMap.setOnMarkerDragListener(this);



    }


    @Override
    public void onMapReady(GoogleMap map) {

        setUpMap(map);
        LatLng mtp = new LatLng(43.6, 3.8833);
        //on definit notre marqueur
        myMarker= map.addMarker(new MarkerOptions().position(mtp).draggable(true));
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(mtp, 13));



    }

    @Override
    public void onMarkerDragStart(Marker mark){


    }

    @Override
    public void onMarkerDrag(Marker mark){

        myMap.animateCamera(CameraUpdateFactory.newLatLng(mark.getPosition()));
    }

    @Override
    public void onMarkerDragEnd(Marker mark){
        LatLng myPoint=mark.getPosition();
        LocationAddress locationAddress = new LocationAddress();
        locationAddress.getAddressFromLocation(myPoint.latitude, myPoint.longitude,
                getApplicationContext(), new GeocoderHandler());
        mark.setTitle(title);

    }
    /*
    @Override
    public boolean onMarkerClick(Marker marker) {
        return true;
    }

    public void setFormCourse(){
        if (onMarkerClick(myMarker))
        {
            Toast.makeText(getApplicationContext(),"Commander une course à partir de :"+title,Toast.LENGTH_LONG).show();
        }
    }*/

    @Override
    public void onMapClick(LatLng point) {
        btLocInfo.setText(point.toString());
        myMap.animateCamera(CameraUpdateFactory.newLatLng(point));
    }
    /*
    @Override
    public void onMapLongClick(LatLng point) {
        //tvLocInfo.setText("New marker added@" + point.toString());
        // on recupere l'adresse de localisation
        LocationAddress locationAddress = new LocationAddress();
        locationAddress.getAddressFromLocation(point.latitude , point.longitude ,
                getApplicationContext(), new GeocoderHandler());
        myMap.addMarker(new MarkerOptions().position(point).title(title));
    }*/


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

    //cette classe geohandler permet de recuperer le message envoyer par le thread LocationAddress
    private class GeocoderHandler extends Handler {

        //on surchage la methode pour l'adapter à notre besoin
        //ici elle nous sert à recupérer le message du thread qui traite geocoder
        @Override
        public void handleMessage(Message message) {
            String locationAddress;
            switch (message.what) {
                case 1:
                    Bundle bundle = message.getData();
                    locationAddress= bundle.getString("adresse");
                    break;
                default:
                    locationAddress= null;
            }
            title=locationAddress;
            btLocInfo.setText(locationAddress);
        }
    }
}


