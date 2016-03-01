package com.pds.app.caloriecounter;


import android.os.Bundle;
import android.widget.CalendarView;
import android.widget.Toast;

public class CalendarActivity extends HomeActivity {

    private CalendarView calendar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        v = getLayoutInflater().inflate(R.layout.activity_calendar,frameLayout);
        calendar = (CalendarView) v.findViewById(R.id.calendar);

        calendar.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @Override
            public void onSelectedDayChange(CalendarView view, int year, int month, int dayOfMonth) {
                Toast toast = Toast.makeText(getBaseContext(), Integer.toString(dayOfMonth)+"/"+Integer.toString(month)
                +"/"+Integer.toString(year), Toast.LENGTH_SHORT);
                toast.show();
            }
        });

    }
}
