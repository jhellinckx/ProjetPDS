package com.pds.app.caloriecounter;

import android.app.ProgressDialog;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_log);
        ButterKnife.bind(this);
        initButtonListener();

        updateWithNetInfo(); //TODO deprecated when onHoldMessages handled in NetworkHandler!!
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
                Intent signActivity = new Intent(LogActivity.this, SignActivity.class);
                startActivity(signActivity);
            }
        });
    }

    /* Don't go back to main activity ! */
    @Override
    public void onBackPressed() {
        moveTaskToBack(true);
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

        /* Send credentials for server side verification */
        JSONObject data = new JSONObject();
        data.put(USERNAME, username);
        send(networkJSON(SIGN_UP_REQUEST, data));
    }

    public void onLoginResponse(JSONObject data){
        String response = (String) data.get(LOG_IN_RESPONSE);
        if(response.equals(LOG_IN_SUCCESS)){
            onLoginSuccess();
        }
        else if(response.equals(LOG_IN_FAILURE)){
            String reason = (String)data.get(REASON);
            if(reason.equals(LOG_IN_ALREADY_CONNECTED)){
                _usernameText.setError("Username already connected");
            }
            else if(reason.equals(LOG_IN_USERNAME_NOT_FOUND)){
                _usernameText.setError("Username not found");
            }
            onLoginFailed();
        }
    }

    public void onLoginSuccess() {
        _loginButton.setEnabled(true);
        Intent personalActivity = new Intent(LogActivity.this, PersonalDataActivity.class);
        startActivity(personalActivity);
    }

    public void onLoginFailed() {
        _loginButton.setEnabled(true);
        Toast.makeText(getBaseContext(), "Login failed", Toast.LENGTH_LONG).show();
    }


    public void setConnected(){
        runOnUiThread(new Runnable() {
            public void run() {
                final int sdk = android.os.Build.VERSION.SDK_INT;
                if(sdk < android.os.Build.VERSION_CODES.JELLY_BEAN) {
                    _connectionState.setBackgroundDrawable(getResources().getDrawable(R.drawable.connection_state_success));
                } else {
                    _connectionState.setBackground(getResources().getDrawable(R.drawable.connection_state_success));
                }
            }
        });
    }

    public void setDisconnected(){
        runOnUiThread(new Runnable() {
            public void run() {
                final int sdk = android.os.Build.VERSION.SDK_INT;
                if (sdk < android.os.Build.VERSION_CODES.JELLY_BEAN) {
                    _connectionState.setBackgroundDrawable(getResources().getDrawable(R.drawable.connection_state_failure));
                } else {
                    _connectionState.setBackground(getResources().getDrawable(R.drawable.connection_state_failure));
                }
            }
        });
    }


}
