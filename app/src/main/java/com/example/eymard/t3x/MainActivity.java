package com.example.eymard.t3x;

import android.location.Location;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class MainActivity extends AppCompatActivity implements OnMapReadyCallback, GoogleMap.OnMapClickListener,GoogleMap.OnMapLongClickListener {

    final int RQS_GooglePlayServices = 1;
    GoogleMap myMap;

    TextView tvLocInfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        tvLocInfo = (TextView)findViewById(R.id.locinfo);

        //on récupère le fragment définit dans le layout qui va contenir la carte
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        myMap=mapFragment.getMap();

        myMap.setOnMapClickListener(this);
        myMap.setOnMapLongClickListener(this);




    }


    @Override
    public void onMapReady(GoogleMap map) {

        setUpMap(map);


        LatLng mtp = new LatLng(43.6, 3.8833);
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(mtp, 12));


    }

    @Override
    public void onMapClick(LatLng point) {
        tvLocInfo.setText(point.toString());
        myMap.animateCamera(CameraUpdateFactory.newLatLng(point));
    }

    @Override
    public void onMapLongClick(LatLng point) {
        tvLocInfo.setText("New marker added@" + point.toString());
        myMap.addMarker(new MarkerOptions().position(point).title(point.toString()));
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
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    /*
    Toutes les opgions de ma map
    */
    public void setUpMap(GoogleMap map)
    {
        //affichage de ma localisation
        map.setMyLocationEnabled(true);

    }




}
