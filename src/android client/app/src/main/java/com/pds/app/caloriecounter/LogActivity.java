package com.pds.app.caloriecounter;

import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.content.Intent;
import android.widget.TextView;

import org.json.simple.JSONObject;

import static org.calorycounter.shared.Constants.network.*;

public class LogActivity extends NotifiableActivity {
    private Button signup = null;
    private Button login = null;
    private Button retry = null;
    private TextView usernametext = null;
    private TextView connectionState = null;
    private TextView errormessage = null;

    private void initButtonListener(){
        signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Todo Send to server and check id. true ? : next activity,show error;
                /*
                Intent personalActivity = new Intent(LogActivity.this, PersonalDataActivity.class);
                startActivity(personalActivity);
                */
                String username = usernametext.getText().toString();
                if (username.isEmpty()) {
                    setErrorMsg("Empty field");
                } else {
                    JSONObject data = new JSONObject();
                    data.put(USERNAME, username);
                    send(networkJSON(SIGN_UP_REQUEST, d            }
            }
        });

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Todo Send to server and check id. true ? : next activity,show error;
                String username = usernametext.getText().toString();
                if(username.isEmpty()){
                    setErrorMsg("Empty field");
                }
                else {
                    JSONObject data = new JSONObject();
                    data.put(USERNAME, username);
                    send(networkJSON(LOG_IN_REQUEST, data));
                }
            }
        });
        retry.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setWaitConnect();
                retryConnect();
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
        signup = (Button) findViewById(R.id.signup);
        login = (Button) findViewById(R.id.login);
        retry = (Button) findViewById(R.id.retry);
        usernametext = (TextView) findViewById(R.id.usernametext);
        connectionState = (TextView) findViewById(R.id.connectionState);
        errormessage = (TextView) findViewById(R.id.errormessage);
        initButtonListener();

        updateWithNetInfo(); //inherited from NotifiableActivity
    }
}
