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
    private TextView connectionState = null;

    private void initButtonListener(){
        signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Todo Send to server and check id. true ? : next activity,show error;

                Intent personalActivity = new Intent(LogActivity.this, PersonalDataActivity.class);
                startActivity(personalActivity);
            }
        });

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Todo Send to server and check id. true ? : next activity,show error;

                Intent personalActivity = new Intent(LogActivity.this, PersonalDataActivity.class);
                startActivity(personalActivity);
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
        Log.d("LOGACTIVITY HANDLE MSG" + msg.toString(), "");
        String request = (String) msg.get(REQUEST_TYPE);
        if(request.equals(CONNECTION_STATUS)){
            String res = (String) msg.get(DATA);
            if(res.equals(CONNECTION_SUCCESS)){
                setConnected();
            } else if (res.equals(CONNECTION_FAILURE)) {
                setDisconnected();
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_log);
        signup = (Button) findViewById(R.id.signup);
        login = (Button) findViewById(R.id.login);
        retry = (Button) findViewById(R.id.retry);
        connectionState = (TextView) findViewById(R.id.connectionState);
        initButtonListener();

        updateWithNetInfo(); //inherited from NotifiableActivity
    }
}
