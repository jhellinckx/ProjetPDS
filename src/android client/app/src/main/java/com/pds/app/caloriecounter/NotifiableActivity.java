package com.pds.app.caloriecounter;

import android.app.Activity;
import android.support.v7.app.AppCompatActivity;

import org.json.simple.JSONObject;

/**
 * Created by jhellinckx on 13/12/15.
 */

public abstract class NotifiableActivity extends AppCompatActivity {

    abstract public void handleMessage(JSONObject msg);

    public void send(JSONObject msg){
        NetworkHandler.getInstance(getApplicationContext()).addOutgoingMessage(msg);
    }
}
