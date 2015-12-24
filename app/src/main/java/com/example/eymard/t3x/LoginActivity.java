package com.example.eymard.t3x;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;


import org.json.JSONException;
import org.json.JSONObject;

import java.util.concurrent.ExecutionException;


public class LoginActivity extends AppCompatActivity implements View.OnClickListener, AsyncResponse {

    private Button bt_login;
    private EditText et_email, et_password;
    private TextView bt_registerLink;
    public static final String MyPREFERENCES = "MyPrefs";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        et_email=(EditText) findViewById(R.id.et_email);
        et_password=(EditText) findViewById(R.id.et_password);
        bt_registerLink=(Button) findViewById(R.id.bt_go_register);
        bt_login=(Button) findViewById(R.id.bt_login);

        bt_login.setOnClickListener(this);
        bt_registerLink.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.bt_login:

                attemptLogin();

                break;

            case R.id.bt_go_register:
                //appel de l'activité de register...
                startActivity(new Intent(this, RegisterActivity.class));

                break;
        }
    }

    /**
     * Attempts to sign in or register the accoun specified by the login form.
     * If there are form errors (invalid email, missing fields, etc.), the
     * errors are presented and no actual login attempt is made.
     */

    private void attemptLogin(){
        //reset error
        et_email.setError(null);
        et_password.setError(null);

        // Store values at the time of the login attempt.
        final String email = et_email.getText().toString();
        final String password = et_password.getText().toString();
        final boolean finish = false;
        boolean cancel = false;
        View focusView = null;

        // Check for a valid password, if the user entered one.
        if (!TextUtils.isEmpty(password) && !isPasswordValid(password)) {
            et_password.setError(getString(R.string.error_invalid_password));
            focusView = et_password;
            cancel = true;
        }else if (!isEmailValid(email)) {
            et_email.setError(getString(R.string.error_invalid_email));
            focusView = et_email;
            cancel = true;
        }

        if (cancel) {
            // There was an error; don't attempt login and focus the first
            // form field with an error.
            focusView.requestFocus();
        } else {
            JSONObject json = new JSONObject();

            try {
                json.put("ctrl", "login");
                json.put("email", email);
                json.put("password", password);

            } catch (JSONException e) {
                e.printStackTrace();
            }

            String requete = json.toString();
            Log.v("req", requete);
            HttpQuery login = new HttpQuery(requete, this);
            login.execute();
        }


    }


    @Override
    public void processFinish(String output) {
        Log.v("result", output);
        String out=output.replaceAll(" ", "");

        if(out.equals("ok")){

            //on recupere les infos pour le sharedPreferences
            String mail;

            JSONObject jsonPref = new JSONObject();

            try {
                jsonPref.put("ctrl", "infosUserByMail");
                jsonPref.put("email", et_email.getText().toString());

            } catch (JSONException e) {
                e.printStackTrace();
            }

            String req = jsonPref.toString();
            Log.v("reqAllinfosUser", req);
            HttpQuery infosUser = new HttpQuery(req, this);
            SharedPreferences sharedpreferences = getSharedPreferences(LoginActivity.MyPREFERENCES, Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedpreferences.edit();
            try {
                String reponse =infosUser.execute().get();
                JSONObject ob=new JSONObject(reponse);
                //on ecrit dans le shared preferences
                editor.putInt("user_id", ob.getInt("id"));
                editor.putString("username", ob.getString("username"));
                editor.commit();

            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }
            finish();
            startActivity(new Intent(this, MainActivity.class));


        }else if (out.equals("erreur")){

            Toast.makeText(getApplicationContext(),
                    "email ou mot de passe incorrect",
                    Toast.LENGTH_LONG).show();
        }

    }


    private boolean isEmailValid(String email){
        //TODO: Replace this with your own logic
        return email.contains("@");
    }

    private boolean isPasswordValid(String password){
        //TODO: Replace this with your own logic
        return password.length() > 2; //mot de passe de longueur sup à 2
    }
}
