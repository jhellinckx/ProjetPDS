package com.pds.app.caloriecounter;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import org.json.simple.JSONObject;

import butterknife.Bind;

import static org.calorycounter.shared.Constants.network.REASON;
import static org.calorycounter.shared.Constants.network.SIGN_UP_ALREADY_CONNECTED;
import static org.calorycounter.shared.Constants.network.SIGN_UP_FAILURE;
import static org.calorycounter.shared.Constants.network.SIGN_UP_RESPONSE;
import static org.calorycounter.shared.Constants.network.SIGN_UP_SUCCESS;
import static org.calorycounter.shared.Constants.network.SIGN_UP_USERNAME_EXISTS;

public class SignActivity extends AppCompatActivity {
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
    }

    public void onSignupResponse(JSONObject data){
        String response = (String) data.get(SIGN_UP_RESPONSE);
        if(response.equals(SIGN_UP_SUCCESS)){
            Intent logActivity = new Intent(SignActivity.this, LogActivity.class);
            startActivity(logActivity);
        }
        else if(response.equals(SIGN_UP_FAILURE)){
            String reason = (String)data.get(REASON);
            if(reason.equals(SIGN_UP_USERNAME_EXISTS)){
                //setErrorMsg("This username already exists");
            }
            else if(reason.equals(SIGN_UP_ALREADY_CONNECTED)){
                //setErrorMsg("This username is already connected");
            }
        }

    }
}
