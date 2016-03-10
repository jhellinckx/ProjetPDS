package com.pds.app.caloriecounter;



import android.content.Context;
import android.os.Bundle;

import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;

import com.github.mikephil.charting.charts.LineChart;

import org.calorycounter.shared.models.EdibleItem;
import org.calorycounter.shared.models.Food;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.util.ArrayList;
import java.util.List;

import static org.calorycounter.shared.Constants.network.*;
import static org.calorycounter.shared.Constants.date.*;

public class HistoryActivity extends MenuNavigableActivity {

    private LinearLayout historyTable;
    private Context context;
    private LineChart chart;
    private List<EdibleItem> past_items;
    private List<String> past_dates;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        v = getLayoutInflater().inflate(R.layout.activity_history, frameLayout);
        context = getBaseContext();

        historyTable = (LinearLayout) v.findViewById(R.id.history_layout);
        past_items = new ArrayList<>();
        past_dates = new ArrayList<>();
        sendHistoryRequest();
    }

    private void sendHistoryRequest(){
        send(networkJSON(HISTORY_REQUEST, new JSONObject()));
    }

    @Override
    public void handleMessage(JSONObject msg){
        JSONObject data = (JSONObject) msg.get(DATA);
        JSONArray foodsDatesRepr = (JSONArray) data.get(HISTORY_FOODS_DATES);
        int size = data.size();
        for (int i = 0; i < size; i++){
            Food food = new Food();
            food.initFromJSON((JSONObject) (((JSONObject) foodsDatesRepr.get(i)).get(HISTORY_FOOD)));
            past_items.add(food);
            String date = (String) ((JSONObject) foodsDatesRepr.get(i)).get(HISTORY_DATE);
            past_dates.add(date);
        }

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                initChart();
            }
        });
    }

    private void initChart(){

        chart = new LineChart(context);
        chart.setLogEnabled(false);
        historyTable.addView(chart);

    }
}
