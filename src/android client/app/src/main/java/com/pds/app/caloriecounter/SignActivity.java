package com.pds.app.caloriecounter;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.json.simple.JSONObject;

import java.io.IOException;

import butterknife.Bind;
import butterknife.ButterKnife;

import static org.calorycounter.shared.Constants.network.*;
import static org.calorycounter.shared.Constants.client.*;

public class SignActivity extends NotifiableActivity {
    @Bind(R.id.btn_signup) Button _signupButton;
    @Bind(R.id.input_username) EditText _usernameText;
    @Bind(R.id.input_password) EditText _passwordText;
    @Bind(R.id.input_password_repeat) EditText _passwordRepeatText;
    @Bind(R.id.link_login) TextView _linkLogin;
    @Bind(R.id.connection_state) View _connectionState;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign);
        ButterKnife.bind(this);
        initButtonListener();
    }

    private void initButtonListener(){
        _signupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onSignup();
            }
        });

        _linkLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent logActivity = new Intent(getApplicationContext(), LogActivity.class);
                if(FUCK_DEFAULT_BEHAVIOUR)
                    logActivity.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                startActivity(logActivity);
            }
        });
    }

    public void handleMessage(JSONObject msg){
        String request = (String) msg.get(REQUEST_TYPE);
        JSONObject data = (JSONObject)msg.get(DATA);
        if(request.equals(SIGN_UP_REQUEST)){
            onSignupResponse(data);
        }
        super.handleMessage(msg);
    }

    public boolean validate(String username, String password, String passwordRepeat){
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
            if(! password.equals(passwordRepeat)){
                _passwordText.setError("Passwords not matching");
                _passwordRepeatText.setError("Passwords not matching");
                valid = false;
            }
            else {
                _passwordText.setError(null);
                _passwordRepeatText.setError(null);
            }
        }
        return valid;
    }

    public void onSignup(){
        String username = _usernameText.getText().toString();
        String password = _passwordText.getText().toString();
        String passwordRepeat = _passwordRepeatText.getText().toString();

        if(!validate(username, password, passwordRepeat)) {
            onSignupFailed();
            return;
        }
        _signupButton.setEnabled(false);

        JSONObject data = new JSONObject();
        data.put(USERNAME, username);
        try {
            send(networkJSON(SIGN_UP_REQUEST, data));
        } catch (IOException e) {
            _signupButton.setEnabled(true);
            Toast toast = Toast.makeText(getBaseContext(), e.getMessage(), Toast.LENGTH_LONG);
            toast.show();
        }
    }

    public void onSignupResponse(JSONObject data){
        String response = (String) data.get(SIGN_UP_RESPONSE);
        if(response.equals(SIGN_UP_SUCCESS)){
            onSignupSuccess();
        }
        else if(response.equals(SIGN_UP_FAILURE)){
            String reason = (String)data.get(REASON);
            if(reason.equals(SIGN_UP_USERNAME_EXISTS)){
                runOnUiThread(new Runnable() {
                    public void run() {
                        _usernameText.setError("Username already exists");
                    }
                });
            }
            else if(reason.equals(SIGN_UP_ALREADY_CONNECTED)){
                runOnUiThread(new Runnable() {
                    public void run() {
                        _usernameText.setError("Username already connected");
                    }
                });
            }
            onSignupFailed();
        }

    }

    public void onSignupSuccess() {
        runOnUiThread(new Runnable() {
            public void run() {
                Intent logActivity = new Intent(SignActivity.this, Home.class);
                if(FUCK_DEFAULT_BEHAVIOUR)
                    logActivity.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                startActivity(logActivity);
            }
        });
    }

    public void onSignupFailed() {
        runOnUiThread(new Runnable() {
            public void run() {
                _signupButton.setEnabled(true);
                Toast toast = Toast.makeText(getBaseContext(), "Sign up failed", Toast.LENGTH_LONG);
                toast.show();
            }
        });

    }
}
