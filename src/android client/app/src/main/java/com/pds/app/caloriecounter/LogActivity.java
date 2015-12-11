package com.pds.app.caloriecounter;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.content.Intent;

public class LogActivity extends AppCompatActivity {

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_log);

        signup = (Button) findViewById(R.id.signup);
        login = (Button) findViewById(R.id.login);

        initButtonListener();
    }
}
