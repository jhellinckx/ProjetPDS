package com.pds.app.caloriecounter;


import android.content.Intent;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;


import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import static org.calorycounter.shared.Constants.network.*;
import static org.calorycounter.shared.Constants.date.*;

public class RecommendationActivity extends MenuNavigableActivity implements RecommendationConstraintsFragment.OnItemClickListener,
        RecommendationResultsFragment.OnItemClickListener{

    private static ArrayList<String> _sportsname = new ArrayList<String>();
    private static ArrayList<String> _foodCategories = new ArrayList<String>();
    private static ArrayList<String> _recipeCategories = new ArrayList<>();
    private ArrayList<String> _productNames = new ArrayList<String>();
    private ArrayList<String> _productCodes = new ArrayList<String>();
    private ArrayList<String> _productDates = new ArrayList<String>();
    private ArrayList<String> _recipeIds = new ArrayList<String>();
    private ArrayList<String> _recipeDates = new ArrayList<>();
    private Boolean isReceipt;
    private float maxCal;

    private static ArrayList<JSONObject> _recommendationsResults = new ArrayList<>();
    private static Calendar calendar = Calendar.getInstance();
    private String current_date;



    private FragmentManager manager = getSupportFragmentManager();
    private JSONObject recom_data = new JSONObject();

    private void replaceFragment(Fragment fragment, String tag){
        FragmentTransaction transaction = manager.beginTransaction();
        transaction.replace(R.id.fragment_layout, fragment, tag);
        transaction.commit();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        v = getLayoutInflater().inflate(R.layout.activity_recommendation, frameLayout);
        Bundle b_ = new Bundle();
        b_=getIntent().getExtras();
        isReceipt = b_.getBoolean("isReceipt");
        maxCal = b_.getFloat("maxCal");
        _productCodes = b_.getStringArrayList("pastFoodCodes");
        _productDates = b_.getStringArrayList("pastFoodDates");
        _recipeIds = b_.getStringArrayList("pastRecipeIds");
        _recipeDates = b_.getStringArrayList("pastRecipeDates");
        current_date = b_.getString("date");
        initPastFoodsInRecomData();
        initCategories();
        launchConstraintFragment();
    }

    public void launchConstraintFragment(){
        FragmentTransaction transaction = manager.beginTransaction();

        Bundle b = new Bundle();
        if(!isReceipt){
            b.putStringArrayList("categoriesNames",_foodCategories);
        }else{
            b.putStringArrayList("categoriesNames", _recipeCategories);
        }
        b.putBoolean("isReceipt", isReceipt);
        b.putFloat("maxCal", maxCal);
        RecommendationConstraintsFragment constrFrag = new RecommendationConstraintsFragment();
        constrFrag.setArguments(b);
        transaction.add(R.id.fragment_layout, constrFrag);
        transaction.commit();
    }

    public void handleMessage(JSONObject msg) {
        Log.d("SPORTFRAG HANDLE MSG", msg.toString());
        String request = (String) msg.get(REQUEST_TYPE);
        JSONObject data = (JSONObject)msg.get(DATA);
        if(request.equals(SPORTS_LIST_REQUEST)){
            String response =  (String)data.get(SPORTS_LIST_RESPONSE);
            if(response.equals(SPORTS_LIST_SUCCESS)){
                Log.d("SPORTS DATA LIST : ",data.toString());
                for(int i = 0; i < data.size()-1 ; ++i){
                    _sportsname.add(((String) data.get(SPORT_NAME + String.valueOf(i))));
                }
            }
            if (_sportsname.size() == SPORTS_LIST_SIZE) {
                RecommendationSportFragment frag = new RecommendationSportFragment();
                Bundle b = new Bundle();
                b.putStringArrayList("names", _sportsname);
                frag.setArguments(b);
                replaceFragment(frag, "sports");
            }
        } else if(request.equals(RECOMMEND_REQUEST)) {
            onRecommendResults(data);
        } else if(request.equals(FOOD_CATEGORIES_REQUEST)){
            for(int i=0; i<data.size(); i++) {
                _foodCategories.add(((String) data.get(CATEGORY_NAME + String.valueOf(i))));
            }
            launchConstraintFragment();
        } else if(request.equals(RECIPE_CATEGORIES_REQUEST)) {
            for (int i = 0; i < data.size(); i++) {
                _recipeCategories.add(((String) data.get(CATEGORY_NAME + String.valueOf(i))));
            }
            launchConstraintFragment();
        }else if(request.equals(FOOD_CODE_REQUEST)){
            String response =  (String)data.get(FOOD_CODE_RESPONSE);
            if(response.equals(FOOD_CODE_SUCCESS)){
                String product_name = (String) data.get(FOOD_NAME);
                _productNames.add(product_name);
                RecommendationPastFragment frag = new RecommendationPastFragment();
                Bundle b = new Bundle();
                b.putStringArrayList("productNames",_productNames);
                frag.setArguments(b);
                replaceFragment(frag,"past");
            }
        }
        else if(request.equals(UPDATE_DATA_REQUEST) || request.equals(DATA_REQUEST)){
            String gender = (String) data.get(UPDATE_DATA_GENDER);
            RecommendationConstraintsFragment frag = new RecommendationConstraintsFragment();
            Bundle b = new Bundle();
            b.putString("gender", gender);
            b.putStringArrayList("foodCategories",_foodCategories);
            frag.setArguments(b);
            replaceFragment(frag, "constraints");
        }

    }

    private void addConstraintsToJSON(String energy, String fat, String prot, String carbo, String recipeOrFood, String category) {
        recom_data.put(MAX_ENERGY, energy);
        recom_data.put(MAX_FAT, fat);
        recom_data.put(MAX_PROT, prot);
        recom_data.put(MAX_CARBOHYDRATES, carbo);
        recom_data.put(RECIPE_OR_FOOD, recipeOrFood);
        recom_data.put(FOOD_CATEGORY, category);
    }

    public void sendCode(String code, String date) {
        JSONObject data = new JSONObject();
        _productCodes.add(code);
        _productDates.add(date); //je l'ai mis ici cmme ca pr test sur emulateur on peut simplement ajouter la date en argument a la methode sendCode()
        data.put(FOOD_CODE, code);
        send(networkJSON(FOOD_CODE_REQUEST, data));
    }

    public void initPastFoodsInRecomData(){

        if (!_productCodes.isEmpty()) {
            recom_data.put(PAST_FOODS_LIST, _productCodes);
            recom_data.put(PAST_FOODS_DATES, _productDates);
        }
        if (!_recipeIds.isEmpty()){
            recom_data.put(PAST_RECIPES_LIST, _recipeIds);
            recom_data.put(PAST_RECIPES_DATES, _recipeDates);
        }
        else {
            recom_data.put(PAST_FOODS_LIST, null);
            recom_data.put(PAST_FOODS_DATES, null);
        }
    }

    public void initCategories(){
        if(isReceipt){
            if (! (_recipeCategories.size() == RECIPE_CATEGORIES_SIZE)){
                send(networkJSON(RECIPE_CATEGORIES_REQUEST, new JSONObject()));
            }
        }else {
            if (!(_foodCategories.size() == FOOD_CATEGORIES_SIZE)) {
                send(networkJSON(FOOD_CATEGORIES_REQUEST, new JSONObject()));
            }
        }
    }

    public void onResultsClick(String energy, String fat, String prot, String carbo, String recipeOrFood, String category){
        addConstraintsToJSON(energy, fat, prot, carbo, recipeOrFood, category);
        send(networkJSON(RECOMMEND_REQUEST, recom_data));
    }

    public void onRecommendResults(JSONObject data){
        JSONArray jsonFoods = (JSONArray) data.get(RECOMMENDED_FOOD_LIST);
        _recommendationsResults = new ArrayList<>();
        for (int i = 0; i < jsonFoods.size(); ++i) {
            _recommendationsResults.add((JSONObject) jsonFoods.get(i));
        }
        Log.d("RECOM LIST", _recommendationsResults.toString());
        RecommendationResultsFragment resFrag = new RecommendationResultsFragment();

        Bundle b = new Bundle();
        b.putString("date", current_date);
        resFrag.setArguments(b);
        replaceFragment(resFrag, "results");

    }

    public ArrayList<JSONObject> recommendationsResults(){
        Log.d("GET RECOM LIST", _recommendationsResults.toString());
        return _recommendationsResults;
    }

    public void restart(){
        replaceFragment(new RecommendationConstraintsFragment(), "past");
    }

    public void onActivityResult(int requestCode, int resultCode, Intent intent){
        IntentResult scanResults = IntentIntegrator.parseActivityResult(requestCode, resultCode, intent);
        if (scanResults != null && scanResults.getContents() != null){
            String scanContent = scanResults.getContents();
            Date date = calendar.getTime();
            sendCode(scanContent, SDFORMAT.format(date));
        } else{
            Toast toast = Toast.makeText(this, "Scan Annulé", Toast.LENGTH_SHORT);
            toast.show();
        }
    }

}
