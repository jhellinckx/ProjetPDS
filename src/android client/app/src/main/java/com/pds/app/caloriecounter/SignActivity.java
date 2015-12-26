package com.pds.app.caloriecounter;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;

import org.json.simple.JSONObject;

import static org.calorycounter.shared.Constants.network.REASON;
import static org.calorycounter.shared.Constants.network.SIGN_UP_ALREADY_CONNECTED;
import static org.calorycounter.shared.Constants.network.SIGN_UP_FAILURE;
import static org.calorycounter.shared.Constants.network.SIGN_UP_RESPONSE;
import static org.calorycounter.shared.Constants.network.SIGN_UP_SUCCESS;
import static org.calorycounter.shared.Constants.network.SIGN_UP_USERNAME_EXISTS;

public class SignActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
    }

    public void onSignupResponse(JSONObject data){
        String response = (String) data.get(SIGN_UP_RESPONSE);
        if(response.equals(SIGN_UP_SUCCESS)){
            Intent personalActivity = new Intent(LogActivity.this, PersonalDataActivity.class);
            startActivity(personalActivity);
        }
        else if(response.equals(SIGN_UP_FAILURE)){
            String reason = (String)data.get(REASON);
            if(reason.equals(SIGN_UP_USERNAME_EXISTS)){
                setErrorMsg("This username already exists");
            }
            else if(reason.equals(SIGN_UP_ALREADY_CONNECTED)){
                setErrorMsg("This username is already connected");
            }
        }

    }
}
