package com.example.eymard.t3x;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener  {

    private Button bt_login;
    private EditText et_email, et_password;
    private TextView tv_registerLink;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        et_email=(EditText) findViewById(R.id.et_email);
        et_password=(EditText) findViewById(R.id.et_password);
        tv_registerLink=(TextView) findViewById(R.id.tv_register);
        bt_login=(Button) findViewById(R.id.bt_login);

        bt_login.setOnClickListener(this);
        tv_registerLink.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.bt_login:



                break;

            case R.id.tv_register:
                //appel de l'activité de register...
                startActivity(new Intent(this, RegisterActivity.class));

                break;
        }
    }
}
