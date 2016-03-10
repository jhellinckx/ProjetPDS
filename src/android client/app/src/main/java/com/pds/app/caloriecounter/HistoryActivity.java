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
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.formatter.PercentFormatter;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet;
import com.github.mikephil.charting.utils.ViewPortHandler;

import org.calorycounter.shared.models.EdibleItem;
import org.calorycounter.shared.models.Food;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import static org.calorycounter.shared.Constants.network.*;
import static org.calorycounter.shared.Constants.date.*;

public class HistoryActivity extends MenuNavigableActivity {

    private static final String Y_ENERGY_LABEL = "Calories";
    private static final String Y_FAT_LABEL = "Lipides";
    private static final String Y_PROT_LABEL = "Protéines";
    private static final int QUOTIENT = 100;
    private static final float AXIS_TEXT_SIZE = 10f;

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
        int size = foodsDatesRepr.size();
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
        initAxis(axis, context.getResources().getColor(R.color.primary_dark), AXIS_TEXT_SIZE);
    }

    private void initYAxis(){
        YAxis axis = chart.getAxisLeft();
        initAxis(axis, Color.BLUE, AXIS_TEXT_SIZE);
        axis.setValueFormatter(new PercentFormatter());
    }

    private void initChartAxis(){
        initXAxis();
        initYAxis();
    }

    private void initAndAddBarToDataSet(BarDataSet set, ArrayList<IBarDataSet> data_set, int color){
        set.setAxisDependency(YAxis.AxisDependency.LEFT);
        set.setColor(color);
        data_set.add(set);

    }

    private List<IBarDataSet> initIBarDataSet(ArrayList<BarEntry> energy_vals,
                                              ArrayList<BarEntry> fat_vals, ArrayList<BarEntry> prot_vals){

        ArrayList<IBarDataSet> data_set = new ArrayList<>();
        BarDataSet set1 = new BarDataSet(energy_vals, Y_ENERGY_LABEL);
        BarDataSet set2 = new BarDataSet(fat_vals, Y_FAT_LABEL);
        BarDataSet set3 = new BarDataSet(prot_vals, Y_PROT_LABEL);
        set1.setValueFormatter(new ItemValueFormatter(CHILD_DAILY_ENERGY/CAL_TO_JOULE_FACTOR));
        set2.setValueFormatter(new ItemValueFormatter(HUMAN_DAILY_FAT));
        set3.setValueFormatter(new ItemValueFormatter(HUMAN_DAILY_PROTEINS));
        initAndAddBarToDataSet(set1, data_set, context.getResources().getColor(R.color.primary));
        initAndAddBarToDataSet(set2, data_set, context.getResources().getColor(R.color.yellow_bar));
        initAndAddBarToDataSet(set3, data_set, context.getResources().getColor(R.color.blue_bar));

        return data_set;
    }

    private float infoToPercentage(float info, float max){
        float quotient = info/max;
        return quotient*QUOTIENT;
    }

    private List<IBarDataSet> castFoodsToBarDataSet(){
        ArrayList<BarEntry> y_energy_vals = new ArrayList<>();
        ArrayList<BarEntry> y_fat_vals = new ArrayList<>();
        ArrayList<BarEntry> y_prot_vals = new ArrayList<>();
        int size = past_items.size();
        for (int i = 0; i < size; i++){
            EdibleItem item = past_items.get(i);
            y_energy_vals.add(new BarEntry(infoToPercentage(item.getTotalEnergy(), CHILD_DAILY_ENERGY), i));
            y_fat_vals.add(new BarEntry(infoToPercentage(item.getTotalFat(), HUMAN_DAILY_FAT), i));
            y_prot_vals.add(new BarEntry(infoToPercentage(item.getTotalProteins(), HUMAN_DAILY_PROTEINS), i));
        }
        return initIBarDataSet(y_energy_vals, y_fat_vals, y_prot_vals);
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

    private class ItemValueFormatter implements ValueFormatter{

        private float max;
        private DecimalFormat mFormat;

        public ItemValueFormatter(float max_factor){
            max = max_factor;
            mFormat = new DecimalFormat("###,###,##0.0");
        }

        private float modifyValue(float value){
            float multiplication_factor = value/QUOTIENT;
            return max*multiplication_factor;
        }

        @Override
        public String getFormattedValue(float value, Entry entry, int dataSetIndex, ViewPortHandler viewPortHandler){
            float res = modifyValue(value);
            return mFormat.format(res);
        }
    }
}
