package com.pds.app.caloriecounter;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

/**
 * Created by jhellinckx on 13/12/15.
 */
public class LauncherActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //TODO Si on connait ses identifiants grâce au savedState, directement aller vers HomeActivity

        NetworkHandler.getInstance(getApplicationContext()).launchThreads();

        Intent logActivity = new Intent(LauncherActivity.this, LogActivity.class);
        startActivity(logActivity);
    }

    @Override
    protected void onDestroy(){
        Log.d("DESTROYING MAIN ACTIVITY !!!","");
        NetworkHandler.getInstance((getApplicationContext())).stop();
    }

}
