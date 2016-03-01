package com.pds.app.caloriecounter;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.FragmentManager;
import android.view.View;
import android.widget.CalendarView;
import android.widget.FrameLayout;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

public class CalendarActivity extends HomeActivity implements DayFragment.BackListener {

    private final static SimpleDateFormat _sdf = new SimpleDateFormat("dd/MM/yyyy");

    private CalendarView calendar;
    private FragmentManager manager;
    private FrameLayout frag_layout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        v = getLayoutInflater().inflate(R.layout.activity_calendar,frameLayout);
        calendar = (CalendarView) v.findViewById(R.id.calendar);
        frag_layout = (FrameLayout) v.findViewById(R.id.day_frame);
        manager = getSupportFragmentManager();
        addDateListener();
    }

    private void addDateListener(){
        calendar.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @Override
            public void onSelectedDayChange(CalendarView view, int year, int month, int dayOfMonth) {
                DayFragment frag = new DayFragment();
                Bundle b = new Bundle();
                b.putString("day", getFormattedDateFromIntegers(year, month, dayOfMonth));
                frag.setArguments(b);
                switchCalendarAndFrameVisibility();
                replaceFragment(frag, "day");
            }
        });

    }

    private String getFormattedDateFromIntegers(int year, int month, int dayOfMonth){
        Calendar cal = new GregorianCalendar(year, month, dayOfMonth);
        Date date = cal.getTime();
        return _sdf.format(date);
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
