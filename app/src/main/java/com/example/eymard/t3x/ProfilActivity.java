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
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.concurrent.ExecutionException;

import Interfaces.AsyncResponse;
import Tasks.HttpQuery;

public class ProfilActivity extends AppCompatActivity implements AsyncResponse {

    TextView nom,prenom,username,email,sep,username_profil,number;
    EditText et_email,et_number;
    Button bt_modif,bt_cancel;
    DrawerLayout mDrawerLayout;
    private static int id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profil);

        //on recupere les widgets
        nom= (TextView) findViewById(R.id.tv_pr_ltName);
        prenom= (TextView) findViewById(R.id.tv_pr_ftName);
        username=(TextView) findViewById(R.id.tv_pr_username);
        email=(TextView) findViewById(R.id.tv_pr_email);
        number=(TextView) findViewById(R.id.tv_pr_number);
        bt_modif= (Button) findViewById(R.id.bt_modif);
        et_email= (EditText) findViewById(R.id.et_pr_email);
        et_number= (EditText) findViewById(R.id.et_pr_number);
        sep=(TextView) findViewById(R.id.sep);
        bt_cancel=(Button) findViewById(R.id.bt_cancelE);
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        username_profil= (TextView) findViewById(R.id.tv_username_profil);

        //on recupere username du sharedpreferences
        SharedPreferences sharedpreferences = getSharedPreferences(LoginActivity.MyPREFERENCES, Context.MODE_PRIVATE);
        username_profil.setText(sharedpreferences.getString("username", "username"));
        id=sharedpreferences.getInt("user_id",0);


        //les listener
        bt_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(ProfilActivity.this, ProfilActivity.class));
            }
        });

        findViewById(R.id.fab).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sep.setVisibility(View.VISIBLE);
                et_email.setText(email.getText());
                et_number.setText(number.getText());
                et_email.setVisibility(View.VISIBLE);
                et_number.setVisibility(View.VISIBLE);
                bt_modif.setVisibility(View.VISIBLE);
                bt_cancel.setVisibility(View.VISIBLE);
                findViewById(R.id.fab).setVisibility(View.GONE);
            }
        });

        bt_modif.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                JSONObject jsonM = new JSONObject();

                try {
                    jsonM.put("ctrl", "modifUser");
                    jsonM.put("id", id);
                    jsonM.put("email", et_email.getText());
                    jsonM.put("number", et_number.getText());

                } catch (JSONException e) {
                    e.printStackTrace();
                }

                String req = jsonM.toString();
                Log.v("reqModifUser", req);
                HttpQuery modifUser = new HttpQuery(req,ProfilActivity.this);

                try {
                    String rep =modifUser.execute().get();
                    String repo=rep.replaceAll(" ","");
                    if (repo.equals("ok")) {
                        Toast.makeText(getApplicationContext(), "Modification effectuée", Toast.LENGTH_LONG);
                        Intent i=getIntent();
                        finish();
                        startActivity(i);
                        //startActivity(new Intent(ProfilActivity.this,ProfilActivity.class));
                    }else {
                        Toast.makeText(getApplicationContext(), "erreur",Toast.LENGTH_LONG);
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (ExecutionException e) {
                    e.printStackTrace();
                }


            }
        });





        Toolbar myToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(myToolbar);
        setTitle("Profil");
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
                switch (menuItem.getTitle().toString()){
                    case "Home":
                        finish();
                        startActivity(new Intent(ProfilActivity.this,MainActivity.class));
                        break;
                    case "Profile":
                        //finish();
                        startActivity(new Intent(ProfilActivity.this,ProfilActivity.class));
                        break;
                    case "Logout":
                        //finish();
                        //startActivity(new Intent(ProfilActivity.this, LoginActivity.class));
                        Intent intent = new Intent(ProfilActivity.this, LoginActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);
                        break;
                    case "Driver mode":
                        startActivity(new Intent(ProfilActivity.this, DriverActivity.class));
                        break;

                    default:
                        Toast.makeText(ProfilActivity.this, menuItem.getTitle(), Toast.LENGTH_LONG).show();

                }
                //Toast.makeText(ProfilActivity.this, menuItem.getTitle(), Toast.LENGTH_LONG).show();
                return true;
            }
        });

        //String nom,prenom,email,username;
        //on recupére les infos de l'user
        JSONObject json = new JSONObject();

        try {
            json.put("ctrl", "infosUserById");
            json.put("id",sharedpreferences.getInt("user_id",0));

        } catch (JSONException e) {
            e.printStackTrace();
        }

        String requete = json.toString();
        Log.v("reqAllinfosUser", requete);
        HttpQuery infosUser = new HttpQuery(requete, this);
        try {
            String reponse =infosUser.execute().get();
            JSONObject ob=new JSONObject(reponse);
            //JSONArray res = new JSONArray(ob);
            //JSONObject row = res.getJSONObject(0);
            nom.setText(ob.getString("nom"));
            prenom.setText(ob.getString("prenom"));
            username.setText(ob.getString("username"));
            email.setText(ob.getString("email"));
            number.setText(ob.getString("numero"));

        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }


    @Override
    public void processFinish(String output) {

    }

    /*
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_profil, menu);
        return true;
    }*/

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
