package com.pds.app.caloriecounter;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

public class LauncherActivity extends Activity {
    private Button login = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_launch);

        NetworkHandler.getInstance(getApplicationContext()).launchThreads();
        GraphicsConstants.setContext(getApplicationContext());

        Intent logActivity = new Intent(LauncherActivity.this, LogActivity.class);
        startActivity(logActivity);
    }

    @Override
    protected void onDestroy(){
        super.onDestroy();
        NetworkHandler.getInstance((getApplicationContext())).stop();
    }

}
