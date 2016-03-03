package com.pds.app.caloriecounter;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;

import org.json.simple.JSONObject;

import butterknife.ButterKnife;

public class DayRecordingActivity extends MenuNavigableActivity  {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        v = getLayoutInflater().inflate(R.layout.activity_day_recording,frameLayout);
    }

    public void handleMessage(JSONObject msg){

    }



}
