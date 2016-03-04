package com.pds.app.caloriecounter;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutCompat;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.Space;
import android.widget.TextView;

import org.json.simple.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static org.calorycounter.shared.Constants.date.SDFORMAT;

public class DayRecordingActivity extends MenuNavigableActivity  {
    private class SpaceView extends View{ // Needed to horizontally fill gaps between other views
        public SpaceView(Context context){
            super(context);
            int width = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 0, getResources().getDisplayMetrics());
            int height = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 1, getResources().getDisplayMetrics());
            this.setLayoutParams(new LinearLayout.LayoutParams(width, height, 1));
        }
    }
    private class DayRecordingContainer extends LinearLayout {
        private TextView title = null;
        private View content = null;

        public DayRecordingContainer(Context context, String title, View content){
            super(context);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
            int marginLeft = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 5, getResources().getDisplayMetrics());
            int marginTop = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 5, getResources().getDisplayMetrics());
            int marginRight = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 5, getResources().getDisplayMetrics());
            int marginBot = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 5, getResources().getDisplayMetrics());
            params.setMargins(marginLeft, marginTop, marginRight, marginBot);
            this.setLayoutParams(params);
            this.setOrientation(LinearLayout.VERTICAL);
            this.setBackgroundColor(getResources().getColor(R.color.white));
            setTitle(title);
            setContent(content);
        }

        public void setTitle(String title){
            boolean added = false;
            if(this.title == null) {
                added = true;
                this.title = new TextView(getContext());
            }
            this.title.setText(title);
            this.title.setTextAppearance(getContext(), android.R.style.TextAppearance_DeviceDefault_Small);
            this.title.setTextColor(Color.LTGRAY);
            this.title.setGravity(Gravity.LEFT);
            int marginLeft = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 5, getResources().getDisplayMetrics());
            int marginTop = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 5, getResources().getDisplayMetrics());
            int marginRight = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 5, getResources().getDisplayMetrics());
            int marginBot = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 5, getResources().getDisplayMetrics());
            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
            layoutParams.setMargins(marginLeft, marginTop, marginRight, marginBot);
            this.title.setLayoutParams(layoutParams);
            if(added) this.addView(this.title);
        }

        public void setContent(View content){
            this.content = content;
            this.addView(this.content);
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
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        int marginLeft = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 10, getResources().getDisplayMetrics());
        int marginTop = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 10, getResources().getDisplayMetrics());
        int marginRight = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 10, getResources().getDisplayMetrics());
        int marginBot = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 10, getResources().getDisplayMetrics());
        params.setMargins(marginLeft,marginTop,marginRight,marginBot);
        intakesLayoutWrapper.setLayoutParams(params);
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



        DayRecordingContainer intakesContainer = new DayRecordingContainer(this, "APPORTS JOURNALIERS", intakesLayoutWrapper);
        topLayout.addView(intakesContainer);

    }

    public void handleMessage(JSONObject msg){

    }



}
