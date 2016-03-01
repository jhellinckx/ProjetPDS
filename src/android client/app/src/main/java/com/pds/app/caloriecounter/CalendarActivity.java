package com.pds.app.caloriecounter;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.View;
import android.widget.CalendarView;
import android.widget.FrameLayout;
import android.widget.Toast;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import static org.calorycounter.shared.Constants.date.*;
import static org.calorycounter.shared.Constants.network.*;

public class CalendarActivity extends HomeActivity implements DayFragment.BackListener {

    private CalendarView calendar;
    private FragmentManager manager;
    private FrameLayout frag_layout;
    private Bundle frag_args;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        v = getLayoutInflater().inflate(R.layout.activity_calendar,frameLayout);
        calendar = (CalendarView) v.findViewById(R.id.calendar);
        frag_layout = (FrameLayout) v.findViewById(R.id.day_frame);
        manager = getSupportFragmentManager();
        frag_args = new Bundle();
        addDateListener();
    }

    private void addDateListener(){
        calendar.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @Override
            public void onSelectedDayChange(CalendarView view, int year, int month, int dayOfMonth) {
                frag_args.putString("day", getFormattedDateFromIntegers(year, month, dayOfMonth));
                sendHistoryRequest(getFormattedDateFromIntegers(year, month, dayOfMonth));
            }
        });

    }

    public void handleMessage(JSONObject msg){
        Log.d("CALENDARACTIVITY HANDLE MSG", msg.toString());
        String request = (String) msg.get(REQUEST_TYPE);
        final JSONObject data = (JSONObject)msg.get(DATA);
        if(request.equals(HISTORY_FOR_DATE_REQUEST)){
            runOnUiThread(new Runnable() {
                public void run() {
                    handleHistoryRequest(data);
                }
            });
        }
    }

    private void handleHistoryRequest(JSONObject data){
        JSONArray response = (JSONArray) data.get(FOOD_NAMES);
        ArrayList<String> foodNames = new ArrayList<>();
        for (Object object: response){
            JSONObject json = (JSONObject) object;
            foodNames.add((String) json.get(FOOD_NAME));
        }
        frag_args.putStringArrayList("names", foodNames);
        changeFragmentAfterResponse();
    }

    private void changeFragmentAfterResponse(){
        DayFragment frag = new DayFragment();
        frag.setArguments(frag_args);
        switchCalendarAndFrameVisibility();
        replaceFragment(frag, "day");

    }

    private void sendHistoryRequest(String date){
        JSONObject data = new JSONObject();
        data.put(HISTORY_DATE, date);
        send(networkJSON(HISTORY_FOR_DATE_REQUEST, data));
    }

    private String getFormattedDateFromIntegers(int year, int month, int dayOfMonth){
        Calendar cal = new GregorianCalendar(year, month, dayOfMonth);
        Date date = cal.getTime();
        return SDFORMAT.format(date);
    }

    private void replaceFragment(Fragment fragment, String tag){
        FragmentTransaction transaction = manager.beginTransaction();
        transaction.replace(R.id.day_frame, fragment, tag);
        transaction.addToBackStack(tag);
        transaction.commit();
    }

    @Override
    public void switchCalendarAndFrameVisibility(){
        int visibility = calendar.getVisibility();
        calendar.setVisibility(frag_layout.getVisibility());
        frag_layout.setVisibility(visibility);
    }
}
