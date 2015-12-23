package com.example.eymard.t3x;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

public class RegisterActivity extends AppCompatActivity implements View.OnClickListener, AsyncResponse{

    private Button bt_register,bt_cancel;
    private EditText et_firstname, et_lastname, et_username, et_email, et_password;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);


        //recuperation des widgets
        et_firstname =(EditText) findViewById(R.id.et_firstname);
        et_lastname =(EditText) findViewById(R.id.et_lastname);
        et_username =(EditText) findViewById(R.id.et_username);
        et_password=(EditText) findViewById(R.id.et_password);
        et_email=(EditText) findViewById(R.id.et_email);
        et_password=(EditText) findViewById(R.id.et_password);
        bt_register=(Button) findViewById(R.id.bt_register);
        bt_cancel=(Button) findViewById(R.id.bt_cancelR);

        bt_register.setOnClickListener(this);
        bt_cancel.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.bt_register:

                attemptRegister();
                break;

            case R.id.bt_cancelR:

                startActivity(new Intent(this, LoginActivity.class));
                break;


        }
    }

    /**
     * Attempts to sign in or register the accoun specified by the login form.
     * If there are form errors (invalid email, missing fields, etc.), the
     * errors are presented and no actual login attempt is made.
     */

    private void attemptRegister(){
        //reset error
        et_email.setError(null);
        et_password.setError(null);

        // Store values at the time of the login attempt.
        final String email = et_email.getText().toString();
        final String password = et_password.getText().toString();
        final String ft_name=et_firstname.getText().toString();
        final String lt_name=et_lastname.getText().toString();
        final String username=et_username.getText().toString();
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
        }else if (TextUtils.isEmpty(ft_name)){
            et_firstname.setError(getString(R.string.error_field_required));
            focusView = et_firstname;
            cancel = true;
        }else if (TextUtils.isEmpty(lt_name)){
            et_lastname.setError(getString(R.string.error_field_required));
            focusView = et_lastname;
            cancel = true;
        }else if (TextUtils.isEmpty(username)){
            et_username.setError(getString(R.string.error_field_required));
            focusView = et_username;
            cancel = true;
        }

        if (cancel) {
            // There was an error; don't attempt login and focus the first
            // form field with an error.
            focusView.requestFocus();
        } else {
            JSONObject json = new JSONObject();

            try {
                json.put("ctrl", "register");
                json.put("email", email);
                json.put("password", password);
                json.put("username",username);
                json.put("lastname",lt_name);
                json.put("firstname",ft_name);


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

            Toast.makeText(getApplicationContext(), "Connecter vous à présent",Toast.LENGTH_LONG).show();
            startActivity(new Intent(this, LoginActivity.class));

        }else if (out.equals("erreur")){

            Toast.makeText(getApplicationContext(),
                    "erreur",
                    Toast.LENGTH_LONG).show();
        }else if (out.equals("email")){

            Toast.makeText(getApplicationContext(),
                    "cet email existe déja",
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
