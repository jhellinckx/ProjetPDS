package com.pds.app.caloriecounter;



import android.content.Context;
import android.os.Bundle;

import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;

import com.github.mikephil.charting.charts.LineChart;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import static org.calorycounter.shared.Constants.network.*;
import static org.calorycounter.shared.Constants.date.*;

public class HistoryActivity extends MenuNavigableActivity {

    private LinearLayout historyTable;
    private Context context;
    private LineChart chart;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        v = getLayoutInflater().inflate(R.layout.activity_history, frameLayout);
        context = getBaseContext();

        historyTable = (LinearLayout) v.findViewById(R.id.history_layout);
        initChart();
    }

    private void initChart(){

        chart = new LineChart(context);
        historyTable.addView(chart);

    }
}
