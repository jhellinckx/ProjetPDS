package com.pds.app.caloriecounter;

import android.app.ProgressDialog;
import android.content.res.ColorStateList;
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

import static org.calorycounter.shared.Constants.network.*;
import butterknife.ButterKnife;
import butterknife.Bind;

public class LogActivity extends NotifiableActivity {
    @Bind(R.id.btn_login) Button _loginButton;
    @Bind(R.id.input_username) EditText _usernameText;
    @Bind(R.id.input_password) EditText _passwordText;
    @Bind(R.id.link_signup) TextView _linkSignup;
    @Bind(R.id.connection_state) View _connectionState;

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
                Intent signActivity = new Intent(LogActivity.this, SignActivity.class);
                startActivity(signActivity);

            }
        });
    }

    public void handleMessage(JSONObject msg){
        Log.d("LOGACTIVITY HANDLE MSG", msg.toString());
        String request = (String) msg.get(REQUEST_TYPE);
        JSONObject data = (JSONObject)msg.get(DATA);
        if(request.equals(CONNECTION_NOTIFIER)){
            String res = (String) data.get(CONNECTION_STATUS);
            if(res.equals(CONNECTION_SUCCESS)){
                setConnected();
            } else if (res.equals(CONNECTION_FAILURE)) {
                setDisconnected();
            }
        }
        else if(request.equals(LOG_IN_REQUEST)){
            onLoginResponse(data);
        }
        else if(request.equals(SIGN_UP_REQUEST)){
            onSignupResponse(data);
        }

    }

    public void onLogin() {
        /* Update GUI */
        _loginButton.setEnabled(false);
        final ProgressDialog progressDialog = new ProgressDialog(LogActivity.this);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage("Authenticating...");
        progressDialog.show();
        new android.os.Handler().postDelayed(
                new Runnable() {
                    public void run() {
                        onLoginFailed();
                        progressDialog.dismiss();
                    }
                }, 3000
        );

        /* Get credentials */
        String username = _usernameText.getText().toString();
        if (username.isEmpty()) {
            setErrorMsg("Empty field");
        } else {
            JSONObject data = new JSONObject();
            data.put(USERNAME, username);
            send(networkJSON(SIGN_UP_REQUEST, data));
        }
    }

    public void onLoginResponse(JSONObject data){
        String response = (String) data.get(LOG_IN_RESPONSE);
        if(response.equals(LOG_IN_SUCCESS)){
            Intent personalActivity = new Intent(LogActivity.this, PersonalDataActivity.class);
            startActivity(personalActivity);
        }
        else if(response.equals(LOG_IN_FAILURE)){
            String reason = (String)data.get(REASON);
            if(reason.equals(LOG_IN_ALREADY_CONNECTED)){
                setErrorMsg("This username is already connected");
            }
            else if(reason.equals(LOG_IN_USERNAME_NOT_FOUND)){
                setErrorMsg("Username not found");
            }

        }

    }

    public void onLoginSuccess() {
        _loginButton.setEnabled(true);
        finish();
    }

    public void onLoginFailed() {
        Toast.makeText(getBaseContext(), "Login failed", Toast.LENGTH_LONG).show();

        _loginButton.setEnabled(true);
    }


    public void setConnected(){
        runOnUiThread(new Runnable() {
            public void run() {
                connectionState.setText("Connection success");
                connectionState.setTextColor(Color.GREEN);
                retry.setVisibility(View.GONE);
            }
        });
    }

    public void setDisconnected(){
        runOnUiThread(new Runnable() {
            public void run() {
                connectionState.setText("Connection failure");
                connectionState.setTextColor(Color.RED);
                retry.setVisibility(View.VISIBLE);
            }
        });
    }

    public void setWaitConnect(){
        runOnUiThread(new Runnable() {
            public void run() {
                connectionState.setText("Connecting...");
                connectionState.setTextColor(Color.GRAY);
            }
        });
    }

    public void setErrorMsg(final String errormsg){
        runOnUiThread(new Runnable() {
            public void run() {
                errormessage.setText(errormsg);
                errormessage.setVisibility(View.VISIBLE);
            }
        });
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_log);
        ButterKnife.bind(this);
        initButtonListener();

        updateWithNetInfo(); //inherited from NotifiableActivity
    }
}

    /* Don't go back to main activity ! */
    @Override
    public void onBackPressed() {
        moveTaskToBack(true);
    }