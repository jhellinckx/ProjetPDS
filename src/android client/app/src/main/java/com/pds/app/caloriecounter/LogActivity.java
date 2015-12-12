package com.pds.app.caloriecounter;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.content.Intent;
import org.json.simple.JSONObject;

import java.io.IOException;

public class LogActivity extends NotifiableAppCompatActivity {
    private Button signup = null;
    private Button login = null;

    private void initButtonListener(){
        signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v){
                //Todo Send to server and check id. true ? : next activity,show error;

                Intent personalActivity = new Intent(LogActivity.this, PersonalDataActivity.class);
                startActivity(personalActivity);
            }
        });

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v){
                //Todo Send to server and check id. true ? : next activity,show error;

                Intent personalActivity = new Intent(LogActivity.this, PersonalDataActivity.class);

                startActivity(personalActivity);
            }
        });
    }

    public void handleMessage(JSONObject msg){

    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_log);

        signup = (Button) findViewById(R.id.signup);
        login = (Button) findViewById(R.id.login);

        initButtonListener();
    }
}
