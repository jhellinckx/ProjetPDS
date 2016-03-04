package com.pds.app.caloriecounter;

import android.content.Context;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.Space;

import org.json.simple.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class DayRecordingActivity extends MenuNavigableActivity  {
    private class SpaceView extends View{ // Needed to horizontally fill gaps between other views
        public SpaceView(Context context){
            super(context);
            int width = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 0, getResources().getDisplayMetrics());
            int height = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 1, getResources().getDisplayMetrics());
            this.setLayoutParams(new LinearLayout.LayoutParams(width, height, 1));
        }
    }
    private static String TITLE_CALORIES = "Calories";
    private static String TITLE_PROTEINS = "Protéines";
    private static String TITLE_CARBO = "Glucides";
    private static String TITLE_SALT = "Sels";
    private static String TITLE_FAT = "Lipides";

    private static String CALORIES_UNIT = "kcal";
    private static String DEFAULT_UNIT = "g";

    private static String[] INTAKES_TITLES = {
            TITLE_CALORIES, TITLE_CARBO, TITLE_PROTEINS, TITLE_SALT, TITLE_FAT
    };

    private LinearLayout topLayout;
    private LinearLayout intakesLayoutWrapper;
    private Map<String, DailyIntakeLayout> dailyIntakes;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        v = getLayoutInflater().inflate(R.layout.activity_day_recording,frameLayout);

        topLayout = (LinearLayout) v.findViewById(R.id.day_recording_layout);

        // Add horizontal layout to draw the daily intakes
        intakesLayoutWrapper = new LinearLayout(this);
        intakesLayoutWrapper.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.FILL_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        intakesLayoutWrapper.setOrientation(LinearLayout.HORIZONTAL);

        dailyIntakes = new LinkedHashMap<>();

        DailyIntakeLayout calorieIntake = new DailyIntakeLayout(this, TITLE_CALORIES, 250, 2000, CALORIES_UNIT);
        dailyIntakes.put(TITLE_CALORIES, calorieIntake);

        DailyIntakeLayout proteinIntake = new DailyIntakeLayout(this, TITLE_PROTEINS, 3.5f, 40, DEFAULT_UNIT);
        dailyIntakes.put(TITLE_PROTEINS, proteinIntake);

        DailyIntakeLayout carboIntake = new DailyIntakeLayout(this, TITLE_CARBO, 150, 300, DEFAULT_UNIT);
        dailyIntakes.put(TITLE_CARBO, carboIntake);


        for(String key : dailyIntakes.keySet()){
            intakesLayoutWrapper.addView(new SpaceView(this));
            intakesLayoutWrapper.addView(dailyIntakes.get(key));
        }
        intakesLayoutWrapper.addView(new SpaceView(this));

        topLayout.addView(intakesLayoutWrapper);
    }

    public void handleMessage(JSONObject msg){

    }



}
