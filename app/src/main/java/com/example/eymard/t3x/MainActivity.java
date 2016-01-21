package com.example.eymard.t3x;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.AsyncTask;
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
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
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


import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ExecutionException;

import Services.ShakeEventManager;
import Tasks.AddressLocation;
import Tasks.HTTPRequest;


public class MainActivity extends AppCompatActivity implements OnMapReadyCallback, GoogleMap.OnMarkerDragListener, GoogleMap.OnMarkerClickListener, ShakeEventManager.ShakeListener {

    final int RQS_GooglePlayServices = 1;
    GoogleMap myMap;
    Marker myMarker,myMarker2,driver;
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
    Boolean driv=false;
    //String depart,arrivee;
    LatLng depart,arrivee;
    String test="erreur";
    String res; String req;
    JSONObject myDriver=new JSONObject();
    String number;
    private ShakeEventManager sd;




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
                        break;

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
                        //finish();
                        startActivity(new Intent(MainActivity.this, DriverActivity.class));
                        break;

                    case "Historicals":
                        startActivity(new Intent(MainActivity.this, HistoActivity.class));
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
        sd = new ShakeEventManager();
        sd.setListener(this);
        sd.init(this);


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
    protected void onPause() {
        super.onPause();
        sd.deregister();
    }


    @Override
    protected void onResume() {
        // TODO Auto-generated method stub
        super.onResume();
        sd.register();
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
                                        Snackbar snackbar1 = Snackbar.make(findViewById(R.id.coordinatorLayout), getString(R.string.snack_order_send), Snackbar.LENGTH_SHORT);
                                        snackbar1.show();
                                        send=true;
                                        /*
                                        myProg= new ProgressDialog(MainActivity.this);
                                        myProg.setMessage(getString(R.string.msg_res_dr));
                                        myProg.setCancelable(true);
                                        myProg.show();
                                        Research research=new Research();
                                        research.execute();*/
                                        JSONObject jObj=new JSONObject();
                                        try {
                                            jObj.put("ctrl","checkDriver");
                                            jObj.put("user_id",user_id);
                                            jObj.put("depart",depart.toString());
                                            jObj.put("arrivee",arrivee.toString());

                                        } catch (JSONException e) {
                                            e.printStackTrace();
                                        }
                                        req=jObj.toString();



                                        BackgroundTask task = new BackgroundTask(MainActivity.this);
                                        task.execute();

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
                //Log.i("______valeur send du bouton annuler", send.toString());
                if (send) {
                    //remove in the database the order
                    JSONObject remove = new JSONObject();

                    try {
                        remove.put("ctrl", "removeCourse");
                        remove.put("user_id", user_id);
                        remove.put("depart", depart.toString());
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
                        Toast.makeText(getApplicationContext(), getString(R.string.toast_ordc), Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(getApplicationContext(), "error cancel", Toast.LENGTH_SHORT).show();
                    }


                }

                finish();
                startActivity(new Intent(MainActivity.this, MainActivity.class));


            }
        });


        //fab call
        findViewById(R.id.fab_call).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!number.isEmpty()) {
                    Intent callIntent = new Intent(Intent.ACTION_CALL);
                    callIntent.setData(Uri.parse("tel:" + number));
                    startActivity(callIntent);
                }
            }
        });

        //fab refresh
        findViewById(R.id.fab_refresh).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                BackgroundTask task = new BackgroundTask(MainActivity.this);
                task.execute();
            }
        });

    }

    //Every 10000 ms
    private void doSomethingRepeatedly() {
        Timer timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
            public void run() {

                try {

                    //new SendToServer().execute();

                } catch (Exception e) {
                    // TODO: handle exception
                }

            }
        }, 0, 10000);
    }

    @Override
    public void onShake() {
        if(driv) {
            Log.i("****onShakeMain", "*******Recuperation de la derniere position du driver");
            JSONObject o = new JSONObject();
            try {
                o.put("ctrl", "getPos");
                o.put("id_user", user_id);
                o.put("depart", depart.toString());
                o.put("arrivee", arrivee.toString());

            } catch (JSONException e) {
                e.printStackTrace();
            }
            String p = "";
            Log.i("------onShake req",o.toString());
            try {

                String r = new HTTPRequest(o.toString()).execute().get();
                Log.i("------onShake getPos",r);
                o = new JSONObject(r);
                p = o.getString("pos");
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }
            String[] tab;
            tab = mySplit(p);
            LatLng posi = new LatLng(Double.parseDouble(tab[0]), Double.parseDouble(tab[1]));
            driver.setPosition(posi);
            myMap.moveCamera(CameraUpdateFactory.newLatLngZoom(posi, 14));
        }



    }

    private class BackgroundTask extends AsyncTask <Void, Void, Void> {
        private ProgressDialog dialog;
        private int tm;
        private boolean b;

        public BackgroundTask(MainActivity activity) {
            dialog = new ProgressDialog(activity);
            tm=0;
            b=false;
        }

        @Override
        protected void onPreExecute() {
            dialog.setMessage(getString(R.string.msg_res_dr));
            dialog.show();
        }

        @Override
        protected void onPostExecute(Void result) {
            String testi = null;
            try {
                String resu = (new HTTPRequest(req)).execute().get();
                testi = resu.replaceAll(" ", "");
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            }

            if (dialog.isShowing()) {
                dialog.dismiss();
            }
            if (testi.equals("ok")){
                Log.i("------research driver", "************Driver finded");
                Toast.makeText(getApplicationContext(),getString(R.string.msg_dr_find), Toast.LENGTH_LONG).show();

                JSONObject j=new JSONObject();
                try {
                    j.put("ctrl","donneesDriver");
                    j.put("user_id",user_id);
                    j.put("depart", depart.toString());
                    j.put("arrivee",arrivee.toString());

                } catch (JSONException e) {
                    e.printStackTrace();
                }
                Log.i("------donnees requetes",j.toString());
                try {
                    String rep = (new HTTPRequest(j.toString())).execute().get();
                    Log.i("------donnees driver",rep);
                    myDriver= new JSONObject(rep);
                    number = myDriver.getString("numero");
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (ExecutionException e) {
                    e.printStackTrace();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                findViewById(R.id.fab_call).setVisibility(View.VISIBLE);
                findViewById(R.id.fab_m3).setVisibility(View.GONE);
                findViewById(R.id.fab_refresh).setVisibility(View.GONE);
                String [] tab= new String[0];
                String drName="";
                try {
                    tab = mySplit(myDriver.getString("pos"));
                    drName=myDriver.getString("username");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                LatLng def= new LatLng(Double.parseDouble(tab[0]),Double.parseDouble(tab[1]));
               driver= myMap.addMarker(new MarkerOptions()
                                .position(def)
                                .draggable(false)
                                .title("Driver: " + drName)
                                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_YELLOW))
                );

                myMap.moveCamera(CameraUpdateFactory.newLatLngZoom(def,14));

                driv=true;
            } else {
                Log.i("------research driver", "******************No driver");
                Toast.makeText(getApplicationContext(),getString(R.string.msg_dr_nofind),Toast.LENGTH_LONG).show();
                findViewById(R.id.fab_refresh).setVisibility(View.VISIBLE);
            }

        }

        @Override
        protected Void doInBackground(Void... params) {
            try {
                Thread.sleep(10000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            return null;
        }

    }

    public String [] mySplit(String s){
        String[] parts = s.split(" ");
        String part= parts[1];
        part = part.substring(1,part.length()-1);
        String[] ret= part.split(",");
        return ret;


    }


}


