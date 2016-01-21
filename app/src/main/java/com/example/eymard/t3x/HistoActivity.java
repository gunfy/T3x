package com.example.eymard.t3x;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

import Adapters.CourseAdapter;
import Classes.Course;
import Tasks.HTTPRequest;

public class HistoActivity extends AppCompatActivity {

    TextView username_profil;
    DrawerLayout mDrawerLayout;
    private static int id;
    JSONArray donneesCourse= new JSONArray();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_histo);

        //on recupere les widgets
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        username_profil= (TextView) findViewById(R.id.tv_username_profil);
        //on recupere username du sharedpreferences
        SharedPreferences sharedpreferences = getSharedPreferences(LoginActivity.MyPREFERENCES, Context.MODE_PRIVATE);
        username_profil.setText(sharedpreferences.getString("username", "username"));
        id=sharedpreferences.getInt("user_id",0);

        //------------Toolbar

        Toolbar myToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(myToolbar);
        setTitle("Historiques");
        ActionBar actionBar = getSupportActionBar();
        try{
            actionBar.setHomeAsUpIndicator(R.drawable.ic_menu);
        }catch (NullPointerException e) {
            Log.i("erreur_menu", "pas de fichier ic_menu");
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
                switch (menuItem.getTitle().toString()) {
                    case "Home":
                        finish();
                        startActivity(new Intent(HistoActivity.this, MainActivity.class));
                        break;
                    case "Profile":
                        //finish();
                        startActivity(new Intent(HistoActivity.this, ProfilActivity.class));
                        break;
                    case "Logout":
                        //finish();
                        //startActivity(new Intent(ProfilActivity.this, LoginActivity.class));
                        Intent intent = new Intent(HistoActivity.this, LoginActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);
                        break;
                    case "Driver mode":
                        finish();
                        startActivity(new Intent(HistoActivity.this, DriverActivity.class));
                        break;
                    case "Historicals":
                        finish();
                        startActivity(new Intent(HistoActivity.this, HistoActivity.class));
                        break;

                    default:
                        Toast.makeText(HistoActivity.this, menuItem.getTitle(), Toast.LENGTH_LONG).show();

                }
                //Toast.makeText(ProfilActivity.this, menuItem.getTitle(), Toast.LENGTH_LONG).show();
                return true;
            }
        });


        //---------------------------
        // requete

        JSONObject json = new JSONObject();

        try {
            json.put("ctrl", "allCourseById");
            json.put("id",id);

        } catch (JSONException e) {
            e.printStackTrace();
        }

        String requete = json.toString();
        Log.v("reqAllCourseById", requete);
        HTTPRequest infosCourse = new HTTPRequest(requete);
        try {
            String reponse =infosCourse.execute().get();
            donneesCourse=new JSONArray(reponse);

        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        Log.i("*******Histo donneesCourse", donneesCourse.toString());

        // init de nos données
        // Construct the data source
        ArrayList<Course> arrayOfCourses = new ArrayList<Course>();
        // Create the adapter to convert the array to views
        CourseAdapter adapter = new CourseAdapter(this, arrayOfCourses);
       // Attach the adapter to a ListView
        ListView listView = (ListView) findViewById(R.id.listView);
        listView.setAdapter(adapter);

        // Add items to adapter
        Course newCourse = new Course("Depart","Arrivee", "Chauffeur","Note");
        adapter.add(newCourse);

        ArrayList<Course> newCourses = Course.fromJson(donneesCourse);
        adapter.addAll(newCourses);



        //-----------------------




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
}
