package com.pds.app.caloriecounter;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import org.json.simple.JSONObject;

import butterknife.ButterKnife;

public class DayRecordingActivity extends MenuNavigableActivity  {
    private LinearLayout topLayout;
    private LinearLayout intakesLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        v = getLayoutInflater().inflate(R.layout.activity_day_recording,frameLayout);

        topLayout = (LinearLayout) v.findViewById(R.id.day_recording_layout);

        // Add horizontal layout to draw the daily intakes
        intakesLayout = new LinearLayout(this);
        intakesLayout.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        intakesLayout.setOrientation(LinearLayout.HORIZONTAL);
        topLayout.addView(intakesLayout);

        DailyIntakeLayout calorieIntake = new DailyIntakeLayout(this, "Calories", 50, 100, "kcal");
        intakesLayout.addView(calorieIntake);

        DailyIntakeLayout proteinIntake = new DailyIntakeLayout(this, "Protéines", 0, 1000, "g");
        intakesLayout.addView(proteinIntake);
    }

    public void handleMessage(JSONObject msg){

    }



}
