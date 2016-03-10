package com.pds.app.caloriecounter;



import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;

import android.widget.FrameLayout;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.*;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;

import org.calorycounter.shared.models.EdibleItem;
import org.calorycounter.shared.models.Food;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import static org.calorycounter.shared.Constants.network.*;
import static org.calorycounter.shared.Constants.date.*;

public class HistoryActivity extends MenuNavigableActivity {

    private static final String yLabel = "Calories";

    private FrameLayout historyTable;
    private Context context;
    private BarChart chart;
    private List<EdibleItem> past_items;
    private List<Date> past_dates;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        v = getLayoutInflater().inflate(R.layout.activity_history, frameLayout);
        context = getBaseContext();

        historyTable = (FrameLayout) v.findViewById(R.id.history_layout);
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
            Date date = null;
            try {
                date = SDFORMAT.parse((String) ((JSONObject) foodsDatesRepr.get(i)).get(HISTORY_DATE));
            } catch (java.text.ParseException e){
                System.err.println(e.getMessage());
            }
            past_dates.add(date);
        }

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                sortDatesAndRelatedFoods();
                initChart();
            }
        });
    }

    private void initChart(){

        chart = new BarChart(context);
        chart.setLogEnabled(false);
        initChartAxis();
        chart.setData(new BarData(pastDatesToStringList(), castFoodsToBarDataSet()));
        chart.invalidate();
        historyTable.addView(chart);

    }

    private void initAxis(AxisBase axis, int color, float text_size){
        axis.setTextSize(text_size);
        axis.setTextColor(color);
        axis.setDrawAxisLine(true);
        axis.setDrawGridLines(false);
    }

    private void initXAxis(){
        XAxis axis = chart.getXAxis();
        axis.setPosition(XAxis.XAxisPosition.BOTTOM);
        initAxis(axis, Color.GREEN, 10f);
    }

    private void initYAxis(){
        YAxis axis = chart.getAxisLeft();
        initAxis(axis, Color.BLUE, 10f);
    }

    private void initChartAxis(){
        initXAxis();
        initYAxis();
    }

    private BarDataSet castFoodsToBarDataSet(){
        ArrayList<BarEntry> yVals = new ArrayList<>();
        int size = past_items.size();
        for (int i = 0; i < size; i++){
            EdibleItem item = past_items.get(i);
            yVals.add(new BarEntry(item.getTotalEnergy(), i));
        }
        BarDataSet set = new BarDataSet(yVals, yLabel);
        set.setAxisDependency(YAxis.AxisDependency.LEFT);
        return set;
    }

    private List<String> pastDatesToStringList(){
        List<String> dates = new ArrayList<>();
        for (Date past_date : past_dates){
            dates.add(SDFORMAT.format(past_date));
        }
        return dates;
    }

    private void sortDatesAndRelatedFoods(){
        Collections.sort(past_items, new Comparator<EdibleItem>() {
            @Override
            public int compare(EdibleItem item_1, EdibleItem item_2) {
                int index_1 = past_items.indexOf(item_1);
                int index_2 = past_items.indexOf(item_2);
                Date date_1 = past_dates.get(index_1);
                Date date_2 = past_dates.get(index_2);
                return (date_1.compareTo(date_2));
            }
        });
        Collections.sort(past_dates);
    }
}
