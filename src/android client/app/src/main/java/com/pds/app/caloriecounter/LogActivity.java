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

public class LogActivity extends NotifiableActivity {
    private Button signup = null;
    private Button login = null;
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
    }

    public void handleMessage(JSONObject msg){
        Log.d("HANDLE LOG MESSAGE : ",msg.toString());
        String request = (String) msg.get("RequestType");
        if(request.equals("CONNECTION_NOTIFIER")){
            String res = (String) msg.get("Data");
            if(res.equals("CONNECTION_SUCCESS")){
                runOnUiThread(new Runnable() {
                    public void run() {
                        connectionState.setText("connection success");
                        connectionState.setTextColor(Color.GREEN);
                    }
                });
            } else if (res.equals("CONNECTION_FAILURE")) {
                runOnUiThread(new Runnable() {
                    public void run() {
                        connectionState.setText("connection failure");
                        connectionState.setTextColor(Color.RED);
                    }
                });
            }
        }

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_log);

        signup = (Button) findViewById(R.id.signup);
        login = (Button) findViewById(R.id.login);

        connectionState = (TextView) findViewById(R.id.connectionState);

        initButtonListener();

        NetworkHandler.getInstance(getApplicationContext()).launchThreads();

    }
}
