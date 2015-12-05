package com.example.eymard.t3x;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class RegisterActivity extends AppCompatActivity{

    private Button bt_register;
    private EditText et_firstname, et_lastname, et_username, et_email, et_password;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        et_firstname =(EditText) findViewById(R.id.et_firstname);
        et_lastname =(EditText) findViewById(R.id.et_lastname);
        et_username =(EditText) findViewById(R.id.et_username);
        et_password=(EditText) findViewById(R.id.et_password);
        et_email=(EditText) findViewById(R.id.et_email);
        et_password=(EditText) findViewById(R.id.et_password);
        bt_register=(Button) findViewById(R.id.bt_register);

        bt_register.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                //attemptLogin();
            }
        });
    }

}
