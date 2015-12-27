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

import java.util.concurrent.TimeoutException;

import static org.calorycounter.shared.Constants.network.*;
import butterknife.ButterKnife;
import butterknife.Bind;

public class LogActivity extends NotifiableActivity {
    @Bind(R.id.btn_login) Button _loginButton;
    @Bind(R.id.input_username) EditText _usernameText;
    @Bind(R.id.input_password) EditText _passwordText;
    @Bind(R.id.link_signup) TextView _linkSignup;
    @Bind(R.id.connection_state) View _connectionState;

    ProgressDialog _progressDialog = null;

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

            synchronized (_connectionState){
                Log.d("handle message","NOTIFYING CONNECT STATE");
                _connectionState.notify();
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
        final String username = _usernameText.getText().toString();
        final String password = _passwordText.getText().toString();

        /* Client side verification first */
        if(!validate(username, password)) {
            onLoginFailed();
            return;
        }
        /* Update GUI */
        _loginButton.setEnabled(false);
        _progressDialog = new ProgressDialog(LogActivity.this, R.style.AppTheme_Dark_Dialog);
        _progressDialog.setIndeterminate(true);
        _progressDialog.setMessage("Authenticating...");
        _progressDialog.show();

        /* By default, connection to server is always tested at client launch.
         * This connection could however have failed, hence the need to
         * check for connection state before we send data to server.
         * We only try every second after the first try. */
        final Thread sendAfterConnect =  new Thread(new Runnable(){
            @Override
            public void run() {
                synchronized (_connectionState) {
                    try {
                        int tries = 0;
                        while (!connected()) {
                            if(tries > 0) Thread.sleep(1000);
                            /* While not connected (and not interrupted, a timeout is set), retry and wait for status update */
                            retryConnect();
                            _connectionState.wait();
                            tries++;
                        }
                        /* When connected, send credentials for server side verification */
                        JSONObject data = new JSONObject();
                        data.put(USERNAME, username);
                        send(networkJSON(SIGN_UP_REQUEST, data));
                    }
                    /* Interrupted when timeout occurs */
                    catch (InterruptedException e) {
                        _progressDialog.dismiss();
                        _loginButton.setEnabled(true);
                        Toast toast = Toast.makeText(getBaseContext(), "Connection failure", Toast.LENGTH_LONG);
                        toast.getView().setBackgroundColor(Color.RED);
                        toast.show();
                    }
                }
            }
        });
        sendAfterConnect.start();

        /* Create a timeout thread */
        final Thread timeout = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    /* Set timeout */
                    Thread.sleep(5000);

                    /* If sendAfterConnect has not yet died, no connection could be established. */
                    if (sendAfterConnect.isAlive()) {
                        sendAfterConnect.interrupt();
                    }
                    /* Else, the server could be reached, but no response was received before timeout. */
                    else {
                        _progressDialog.dismiss();
                        _loginButton.setEnabled(true);
                        Toast toast = Toast.makeText(getBaseContext(), "Response timeout", Toast.LENGTH_LONG);
                        toast.getView().setBackgroundColor(Color.RED);
                        toast.show();
                    }
                }
                catch(InterruptedException e){}
            }
        });
        
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
        if(_progressDialog != null){
            _progressDialog.dismiss();
            _progressDialog = null;
        }
        _loginButton.setEnabled(true);
        Intent personalActivity = new Intent(LogActivity.this, PersonalDataActivity.class);
        startActivity(personalActivity);
    }

    public void onLoginFailed() {
        if(_progressDialog != null){
            _progressDialog.dismiss();
            _progressDialog = null;
        }
        _loginButton.setEnabled(true);
        Toast toast = Toast.makeText(getBaseContext(), "Login failed", Toast.LENGTH_LONG);
        toast.getView().setBackgroundColor(Color.RED);
        toast.show();
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
