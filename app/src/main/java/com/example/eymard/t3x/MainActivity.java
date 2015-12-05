package com.example.eymard.t3x;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class MainActivity extends AppCompatActivity {

    static final LatLng PARIS = new LatLng(48.858093, 2.294694);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //on récupère le fragment définit dans le layout qui va contenir la carte
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap googleMap) {

                //ajoute un marker sur Paris
                googleMap.addMarker(new MarkerOptions().title("Paris").position(PARIS));
                //on autorise l'api à afficher le bouton pour accéder à notre position courante
                googleMap.setMyLocationEnabled(true);
                //centre la google map sur Paris (avec animation de zoom)
                googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(PARIS, 17));
                googleMap.animateCamera(CameraUpdateFactory.zoomTo(15), 2000, null);

            }
        });
    }


}
