package com.pds.app.caloriecounter;


import android.content.Intent;
import android.os.Bundle;
import android.widget.CalendarView;

import com.pds.app.caloriecounter.dayrecording.DayRecordingActivity;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import static org.calorycounter.shared.Constants.date.*;

public class CalendarActivity extends MenuNavigableActivity {

    private CalendarView calendar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        v = getLayoutInflater().inflate(R.layout.activity_calendar,frameLayout);
        calendar = (CalendarView) v.findViewById(R.id.calendar);
        addDateListener();
    }

    private void addDateListener(){
        calendar.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @Override
            public void onSelectedDayChange(CalendarView view, int year, int month, int dayOfMonth) {
                Intent dayRecordingIntent = new Intent(CalendarActivity.this, DayRecordingActivity.class);
                Bundle b = new Bundle();
                b.putString("day", getFormattedDateFromIntegers(year, month, dayOfMonth));
                dayRecordingIntent.putExtras(b);
                startActivity(dayRecordingIntent);
            }
        });

    }

    private String getFormattedDateFromIntegers(int year, int month, int dayOfMonth){
        Calendar cal = new GregorianCalendar(year, month, dayOfMonth);
        Date date = cal.getTime();
        return SDFORMAT.format(date);
    }
}
