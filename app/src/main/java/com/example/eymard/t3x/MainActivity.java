package com.example.eymard.t3x;

import android.app.ProgressDialog;
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

import org.json.JSONException;
import org.json.JSONObject;



import java.util.concurrent.ExecutionException;

public class MainActivity extends AppCompatActivity implements OnMapReadyCallback, GoogleMap.OnMarkerDragListener, GoogleMap.OnMarkerClickListener {

    final int RQS_GooglePlayServices = 1;
    GoogleMap myMap;
    Marker myMarker,myMarker2;
    Button btLocInfo;
    String title = null;
    DrawerLayout mDrawerLayout;
    TextView username_profil;
    int mode=0;   //0 normal/1 driver
    int user_id;
    Location myLocation = null;
    JSONObject myCourse=new JSONObject();
    Boolean ordre=false;
    Boolean send=false;
    //String depart,arrivee;
    LatLng depart,arrivee;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //on recupere le widget
        btLocInfo = (Button) findViewById(R.id.locinfo);
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        username_profil = (TextView) findViewById(R.id.tv_username_profil);
        gestionFab();

        //on recupere id et username du sharedpreferences
        SharedPreferences sharedpreferences = getSharedPreferences(LoginActivity.MyPREFERENCES, Context.MODE_PRIVATE);
        username_profil.setText(sharedpreferences.getString("username", "username"));
        user_id=sharedpreferences.getInt("user_id", 0);


        Toolbar myToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(myToolbar);
        setTitle("T3x");
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
                        startActivity(new Intent(MainActivity.this, MainActivity.class));

                    case "Profile":
                        //finish();
                        startActivity(new Intent(MainActivity.this, ProfilActivity.class));

                        break;
                    case "Logout":
                        //finish();
                        //startActivity(new Intent(MainActivity.this, LoginActivity.class));
                        Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);
                        break;

                    case "Driver mode":
                        startActivity(new Intent(MainActivity.this, DriverActivity.class));
                        break;

                    default:
                        Toast.makeText(MainActivity.this, menuItem.getTitle(), Toast.LENGTH_LONG).show();

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
        //Log.i("*****my provider", provider);
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
        //utilisation de du provider NETWORK car GPS met trop de temps
        myLocation = locationManager.getLastKnownLocation("network");



        //myMap.setOnMapClickListener(this);
        //myMap.setOnMapLongClickListener(this);
        myMap.setOnMarkerDragListener(this);



    }


    @Override
    public void onMapReady(GoogleMap map) {

        setUpMap(map);

        //on définit notre marqueur
        if (myLocation==null){
            LatLng mtp = new LatLng(43.6, 3.8833);

            myMarker= map.addMarker(new MarkerOptions()
                            .position(mtp)
                            .draggable(true)
                            .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE))
            );
            map.moveCamera(CameraUpdateFactory.newLatLngZoom(mtp, 13));
        }else {
            LatLng defaut=new LatLng(myLocation.getLatitude(),myLocation.getLongitude());
            myMarker = map.addMarker(new MarkerOptions()
                            .position(defaut)
                            .draggable(true)
                            //.title("Depart")
                            .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN))
            );
            map.moveCamera(CameraUpdateFactory.newLatLngZoom(defaut, 17));

            AddressLocation addressLocation=new AddressLocation(defaut.latitude, defaut.longitude,
                    getApplicationContext());

            try {
                String rep= addressLocation.execute().get();
                //Log.i("reponse geocoder",rep);
                if (rep.equals("")){
                    title="Unable to get address for this lat-long";
                }else {
                    title=rep;
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            }

            myMarker.setTitle(title);
            btLocInfo.setText(title);

        }


    }

    @Override
    public void onMarkerDragStart(Marker mark){
        LatLng myPoint=mark.getPosition();
        AddressLocation addressLocation=new AddressLocation(myPoint.latitude, myPoint.longitude,
                getApplicationContext());

        try {
            String rep= addressLocation.execute().get();
            //Log.i("reponse geocoder",rep);
            if (rep.equals("")){
                title="Unable to get address for this lat-long";
            }else {
                title=rep;
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }

        mark.setTitle(title);
        btLocInfo.setText(title);


    }

    @Override
    public boolean onMarkerClick(Marker marker) {
        return false;
    }

    @Override
    public void onMarkerDrag(Marker mark){

        myMap.animateCamera(CameraUpdateFactory.newLatLng(mark.getPosition()));
    }

    @Override
    public void onMarkerDragEnd(Marker mark){
        LatLng myPoint=mark.getPosition();
        AddressLocation addressLocation=new AddressLocation(myPoint.latitude, myPoint.longitude,
                getApplicationContext());

        try {
            String rep= addressLocation.execute().get();
            //Log.i("reponse geocoder",rep);
            if (rep.equals("")){
                title="Unable to get address for this lat-long";
            }else {
                title=rep;
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }

        mark.setTitle(title);
        btLocInfo.setText(title);

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


        //fab depart
        findViewById(R.id.fab_m1).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                depart=myMarker.getPosition();
                String titleDep=title;
                //depart=myMarker.getPosition().latitude+","+myMarker.getPosition().longitude;
                myMarker.setDraggable(false);
                Snackbar snackbar1 = Snackbar.make(findViewById(R.id.coordinatorLayout),getString(R.string.snack_msg_arrivee), Snackbar.LENGTH_LONG);
                snackbar1.show();
                //on crée le nouveau marqueur pour definir l'arrivée
                LatLng defaut=new LatLng(myLocation.getLatitude(),myLocation.getLongitude());
                myMarker2 = myMap.addMarker(new MarkerOptions()
                                .position(defaut)
                                .draggable(true)
                        //.title("Arrivee")
                );
                myMap.moveCamera(CameraUpdateFactory.newLatLngZoom(defaut, 17));
                AddressLocation addressLocation=new AddressLocation(defaut.latitude, defaut.longitude,
                        getApplicationContext());

                try {
                    String rep= addressLocation.execute().get();
                    //Log.i("reponse geocoder",rep);
                    if (rep.equals("")){
                        title="Unable to get address for this lat-long";
                    }else {
                        title=rep;
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (ExecutionException e) {
                    e.printStackTrace();
                }

                myMarker.setTitle(title);
                btLocInfo.setText(title);

                //on ajoute dans myCourse
                try {
                    myCourse.put("ctrl","course");
                    myCourse.put("user_id",user_id);
                    myCourse.put("depart",depart.toString());
                    myCourse.put("descDepart",titleDep);

                } catch (JSONException e) {
                    e.printStackTrace();
                }
                Log.i("********Valeurs course en cours",myCourse.toString());
                findViewById(R.id.fab_m1).setVisibility(View.GONE);
                findViewById(R.id.fab_m3).setVisibility(View.VISIBLE);
                ordre=true;
            }
        });

        //fab arrivee
        findViewById(R.id.fab_m2).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (ordre) {
                    Log.i("-------ordre", "on est bien dans le bon ordre");
                    arrivee = myMarker2.getPosition();
                    String titleArr=title;
                    //arrivee=myMarker2.getPosition().latitude+","+myMarker2.getPosition().longitude;
                    Log.i("current position of fab_m2", arrivee.toString());
                    //Toast.makeText(getApplicationContext(),"C'est bon c'est dans l'ordre",Toast.LENGTH_SHORT).show();
                    try {
                        myCourse.put("arrivee", arrivee);
                        myCourse.put("descArrivee", titleArr);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    Log.i("********Valeurs course en cours", myCourse.toString());

                    Snackbar snackbar = Snackbar
                            .make(findViewById(R.id.coordinatorLayout), getString(R.string.snack_msg), Snackbar.LENGTH_LONG)
                            .setAction(getString(R.string.snack_bt), new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    HTTPRequest course = new HTTPRequest(myCourse.toString());

                                    String rep = "";
                                    try {
                                        rep = course.execute().get();
                                    } catch (InterruptedException e) {
                                        e.printStackTrace();
                                    } catch (ExecutionException e) {
                                        e.printStackTrace();
                                    }

                                    String out = rep.replaceAll(" ", "");
                                    if (out.equals("ok")) {
                                        myMarker2.setDraggable(false);
                                        findViewById(R.id.fab_m2).setVisibility(View.GONE);
                                        send=true;
                                        Snackbar snackbar1 = Snackbar.make(findViewById(R.id.coordinatorLayout), getString(R.string.snack_order_send), Snackbar.LENGTH_SHORT);
                                        snackbar1.show();

                                        /*ProgressDialog.show(MainActivity.this,
                                                    "", "Recherche de driver", true);*/

                                    } else {
                                        Snackbar snackbar1 = Snackbar.make(findViewById(R.id.coordinatorLayout), "error", Snackbar.LENGTH_SHORT);
                                        snackbar1.show();
                                    }

                                }
                            });
                    snackbar.show();

                }
            }
        });

        //fab annulee
        findViewById(R.id.fab_m3).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (send){
                    //remove in the database the order
                    JSONObject remove =new JSONObject();

                    try {
                        remove.put("ctrl","removeCourse");
                        remove.put("user_id", user_id);
                        remove.put("depart",depart.toString());
                        remove.put("arrivee", arrivee.toString());
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    HTTPRequest cancel = new HTTPRequest(remove.toString());
                    String rep1 = "";
                    try {
                        rep1 = cancel.execute().get();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    } catch (ExecutionException e) {
                        e.printStackTrace();
                    }

                    String out1 = rep1.replaceAll(" ", "");
                    if (out1.equals("ok")) {
                        Toast.makeText(getApplicationContext(),"order canceled",Toast.LENGTH_SHORT).show();
                    }else{
                        Toast.makeText(getApplicationContext(),"error cancel",Toast.LENGTH_SHORT).show();
                    }


                }

                finish();
                startActivity(new Intent(MainActivity.this, MainActivity.class));


            }
        });

    }



}


