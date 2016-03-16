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
import org.calorycounter.shared.models.Recipe;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.text.DecimalFormat;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

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
    private HashMap<Date, IntakeTuple> past_intakes;
    private float max_energy;

    private final class IntakeTuple{
        private float energy;
        private float fat;
        private float proteins;

        public IntakeTuple(){
            energy = 0;
            proteins = 0;
            fat = 0;
        }

        public float getEnergy() {
            return energy;
        }

        public void addToEnergy(float energy) {
            this.energy += energy;
        }

        public float getFat() {
            return fat;
        }

        public void addToFat(float fat) {
            this.fat += fat;
        }

        public float getProteins() {
            return proteins;
        }

        public void addToProteins(float protein) {
            this.proteins += protein;
        }
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        v = getLayoutInflater().inflate(R.layout.activity_history, frameLayout);
        context = getBaseContext();

        historyTable = (FrameLayout) v.findViewById(R.id.history_layout);
        past_intakes = new HashMap<>();
        sendDataRequest();
    }

    private void sendHistoryRequest(){
        send(networkJSON(HISTORY_REQUEST, new JSONObject()));
    }

    private float findMaxEnergyForGender(String gender){
        float energy;
        switch (gender){
            case "C":
                energy = CHILD_DAILY_ENERGY;
                break;
            case "M":
                energy = MEN_DAILY_ENERGY;
                break;
            case "W":
                energy = WOMEN_DAILY_ENERGY;
                break;
            default:
                energy = TEEN_DAILY_ENERGY;
        }
        return energy;
    }

    @Override
    public void handleMessage(JSONObject msg){
        String request = (String) msg.get(REQUEST_TYPE);
        JSONObject data = (JSONObject)msg.get(DATA);
        if (request.equals(DATA_REQUEST)){
            String gender = (String) data.get(UPDATE_DATA_GENDER);
            max_energy = findMaxEnergyForGender(gender);
            sendHistoryRequest();
        }
        else if (request.equals(HISTORY_REQUEST)) {
            JSONArray foodsDatesRepr = (JSONArray) data.get(HISTORY_FOODS_DATES);
            JSONArray recipesDatesRepr = (JSONArray) data.get(HISTORY_RECIPES_DATES);
            initPastIntakes(foodsDatesRepr, recipesDatesRepr);

            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    initChart();
                }
            });
        }
    }

    private void sendDataRequest(){
        JSONObject data = new JSONObject();
        send(networkJSON(DATA_REQUEST, data));
    }

    private void retrieveDates(JSONArray datesRepr){
        int size = datesRepr.size();
        for (int i = 0; i < size; i++){
            String date = (String) ((JSONObject) datesRepr.get(i)).get(HISTORY_DATE);
            try {
                past_intakes.put(SDFORMAT.parse(date), new IntakeTuple());
            } catch(ParseException e){
                System.err.println(e.getMessage());
            }
        }
    }

    private void updateTuple(IntakeTuple tuple, EdibleItem item){
        tuple.addToEnergy(item.getTotalEnergy());
        tuple.addToFat(item.getTotalFat());
        tuple.addToProteins(item.getTotalProteins());
    }

    private Recipe initRecipeItem(JSONObject o){
        Recipe recipe = new Recipe();
        recipe.initFromJSON(o);
        return recipe;
    }

    private Food initFoodItem(JSONObject o){
        Food food = new Food();
        food.initFromJSON(o);
        return food;
    }

    private void updateIntakes(JSONArray itemsRepr){
        int size = itemsRepr.size();
        for (int i = 0; i < size; i++){
            JSONObject obj = (JSONObject) itemsRepr.get(i);
            EdibleItem item;
            if (obj.get(HISTORY_FOOD) == null){
                JSONObject o = (JSONObject) obj.get(HISTORY_RECIPE);
                item = initRecipeItem(o);
            } else{
                JSONObject o = (JSONObject) obj.get(HISTORY_FOOD);
                item = initFoodItem(o);
            }
            try{
                Date date = SDFORMAT.parse((String) obj.get(HISTORY_DATE));
                IntakeTuple tuple = past_intakes.get(date);
                updateTuple(tuple, item);
            } catch(ParseException e){
                System.err.println(e.getMessage());
            }
        }
    }

    private void initPastIntakes(JSONArray foodsDatesRepr, JSONArray recipesDatesRepr){
        retrieveDates(foodsDatesRepr);
        retrieveDates(recipesDatesRepr);
        updateIntakes(foodsDatesRepr);
        updateIntakes(recipesDatesRepr);
    }

    private void initChart(){

        chart = new BarChart(context);
        chart.setLogEnabled(false);
        initChartAxis();
        List<String> dates = pastDatesToStringList();
        chart.setData(new BarData(dates, castIntakesTupleToBarDataSet(dates)));
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
        set1.setValueFormatter(new ItemValueFormatter(max_energy/CAL_TO_JOULE_FACTOR));
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

    private List<IBarDataSet> castIntakesTupleToBarDataSet(List<String> dates){
        ArrayList<BarEntry> y_energy_vals = new ArrayList<>();
        ArrayList<BarEntry> y_fat_vals = new ArrayList<>();
        ArrayList<BarEntry> y_prot_vals = new ArrayList<>();
        int size = dates.size();
        for(int i = 0; i < size; i++){
            try {
                IntakeTuple intake = past_intakes.get(SDFORMAT.parse(dates.get(i)));
                y_energy_vals.add(new BarEntry(infoToPercentage(intake.getEnergy(), max_energy), i));
                y_fat_vals.add(new BarEntry(infoToPercentage(intake.getFat(), HUMAN_DAILY_FAT), i));
                y_prot_vals.add(new BarEntry(infoToPercentage(intake.getProteins(), HUMAN_DAILY_PROTEINS), i));
            }catch (ParseException e){
                System.err.println(e.getMessage());
            }
        }
        return initIBarDataSet(y_energy_vals, y_fat_vals, y_prot_vals);
    }

    private List<String> pastDatesToStringList(){
        List<Date> days = sortDates(past_intakes.keySet());
        List<String> dates = new ArrayList<>();
        for (Date past_date : days){
            dates.add(SDFORMAT.format(past_date));
        }
        return dates;
    }

    private List<Date> sortDates(Set<Date> dates){
        List<Date> days = new ArrayList<>(dates);
        Collections.sort(days);
        return days;
    }

    private final class ItemValueFormatter implements ValueFormatter{

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