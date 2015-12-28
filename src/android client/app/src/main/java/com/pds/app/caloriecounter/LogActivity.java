package com.pds.app.caloriecounter;

import android.app.ProgressDialog;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.content.Intent;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.json.simple.JSONObject;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

import static org.calorycounter.shared.Constants.network.*;
import butterknife.ButterKnife;
import butterknife.Bind;

public class LogActivity extends NotifiableActivity {
    @Bind(R.id.btn_login) Button _loginButton;
    @Bind(R.id.input_username) EditText _usernameText;
    @Bind(R.id.input_password) EditText _passwordText;
    @Bind(R.id.link_signup) TextView _linkSignup;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_log);
        ButterKnife.bind(this);
        initButtonListener();
    }

    @Override
    protected void onResume(){
        super.onResume();
    }

    private void initButtonListener(){
        _loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onLogin();
            }
        });

        _linkSignup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent signActivity = new Intent(getApplicationContext(), SignActivity.class);
                startActivity(signActivity);
            }
        });
    }

    /* Don't go back to launch activity ! */
    @Override
    public void onBackPressed() {
        moveTaskToBack(true);
    }

    public void handleMessage(JSONObject msg){
        String request = (String) msg.get(REQUEST_TYPE);
        JSONObject data = (JSONObject)msg.get(DATA);
        if(request.equals(LOG_IN_REQUEST)){
            onLoginResponse(data);
        }
        super.handleMessage(msg);
    }

    public boolean validate(String username, String password){
        boolean valid = true;
        if(username.isEmpty()) {
            _usernameText.setError("Username cannot be empty");
            valid = false;
        }
        else{
            _usernameText.setError(null);
        }

        if(password.isEmpty()){
            _passwordText.setError("Password cannot be empty");
            valid = false;
        }
        else{
            _passwordText.setError(null);
        }
        return valid;
    }

    public void onLogin() {
         /* Get credentials */
        String username = _usernameText.getText().toString();
        String password = _passwordText.getText().toString();

        /* Client side verification first */
        if(!validate(username, password)) {
            onLoginFailed();
            return;
        }
        /* Update GUI */
        _loginButton.setEnabled(false);

        JSONObject data = new JSONObject();
        data.put(USERNAME, username);
        try {
            send(networkJSON(LOG_IN_REQUEST, data));
        } catch (IOException e) {
            _loginButton.setEnabled(true);
            Toast toast = Toast.makeText(getBaseContext(), e.getMessage(), Toast.LENGTH_LONG);
            toast.show();
        }
    }

    public void onLoginResponse(JSONObject data){
        String response = (String) data.get(LOG_IN_RESPONSE);
        if(response.equals(LOG_IN_SUCCESS)){
            onLoginSuccess();
        }
        else if(response.equals(LOG_IN_FAILURE)){
            String reason = (String)data.get(REASON);
            if(reason.equals(LOG_IN_ALREADY_CONNECTED)){
                runOnUiThread(new Runnable() {
                    public void run() {
                        _usernameText.setError("Username already connected");
                    }
                });

            }
            else if(reason.equals(LOG_IN_USERNAME_NOT_FOUND)){
                runOnUiThread(new Runnable() {
                    public void run() {
                        _usernameText.setError("Username not found");
                    }
                });
            }
            onLoginFailed();
        }
    }

    public void onLoginSuccess() {
        runOnUiThread(new Runnable() {
            public void run() {
                Intent personalActivity = new Intent(LogActivity.this, PersonalDataActivity.class);
                startActivity(personalActivity);
                finish(); // If logged in, only come back to this activity when log out called, not when user presses back
            }
        });

    }

    public void onLoginFailed() {
        runOnUiThread(new Runnable() {
            public void run() {
                _loginButton.setEnabled(true);
                Toast toast = Toast.makeText(getBaseContext(), "Log in failed", Toast.LENGTH_LONG);
                toast.show();
            }
        });

    }




}
