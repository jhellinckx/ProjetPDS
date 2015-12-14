package com.pds.app.caloriecounter;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
/**
 * Created by jhellinckx on 13/12/15.
 */
public class LauncherActivity extends Activity {
    private Button login = null;

    private void initButtonListener() {
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent logActivity = new Intent(LauncherActivity.this, LogActivity.class);
                startActivity(logActivity);
            }
        });
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_launch);

        login = (Button) findViewById(R.id.launch_login);


        //TODO Si on connait ses identifiants grâce au savedState, directement aller vers HomeActivity

        NetworkHandler.getInstance(getApplicationContext()).launchThreads();
        initButtonListener();
        Intent logActivity = new Intent(LauncherActivity.this, LogActivity.class);
        startActivity(logActivity);
    }

    @Override
    protected void onDestroy(){
        super.onDestroy();
        NetworkHandler.getInstance((getApplicationContext())).stop();
    }

}
