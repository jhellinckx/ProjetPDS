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
import java.util.List;

import org.json.simple.JSONObject;


import static org.calorycounter.shared.Constants.network.*;

public class RecommendationActivity extends HomeActivity implements RecommendationPastFragment.OnItemClickListener,
        RecommendationSportFragment.OnItemClickListener, RecommendationConstraintsFragment.OnItemClickListener,
        RecommendationResultsFragment.OnItemClickListener{

    private static ArrayList<String> _sportsname = new ArrayList<String>();
    private static ArrayList<String> _productNames = new ArrayList<String>();

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

        Bundle b = new Bundle();
        b.putStringArrayList("productNames",_productNames);
        FragmentTransaction transaction = manager.beginTransaction();
        RecommendationPastFragment pastFrag = new RecommendationPastFragment();
        pastFrag.setArguments(b);
        transaction.add(R.id.fragment_layout, pastFrag);
        transaction.commit();
    }

    public void handleMessage(JSONObject msg){
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
                replaceFragment(frag, "sport");
            }
        }
        else if(request.equals(FOOD_CODE_REQUEST)){
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
    }

    private void sendData(String sport, String duration){
        JSONObject data = new JSONObject();
        data.put(SPORT_NAME, sport);
        data.put(SPORT_DURATION, duration);
        send(networkJSON(CHOSEN_SPORT_REQUEST, data));
    }

    private void addConstraintsToJSON(String energy, String fat, String prot, String carbo){
        recom_data.put(MAX_ENERGY, energy);
        recom_data.put(MAX_FAT, fat);
        recom_data.put(MAX_PROT, prot);
        recom_data.put(MAX_CARBOHYDRATES, carbo);
    }

    public void sendCode(String code) {
        JSONObject data = new JSONObject();
        data.put(FOOD_CODE, code);
        send(networkJSON(FOOD_CODE_REQUEST, data));
    }

    public void onNextPastClick(List<String> foodnames){
        if (!foodnames.isEmpty()) {
            recom_data.put(PAST_FOODS_LIST, foodnames);
        }

        if (_sportsname.size() == SPORTS_LIST_SIZE){
            RecommendationSportFragment frag = new RecommendationSportFragment();
            Bundle b = new Bundle();
            b.putStringArrayList("names", _sportsname);
            frag.setArguments(b);
            replaceFragment(frag, "sport");
        }
        else {
            send(networkJSON(SPORTS_LIST_REQUEST, new JSONObject()));
        }
    }

    public void onNextSportClick(Spinner sports, EditText duration){
        if(duration.getText().toString() != ""){
            recom_data.put(SPORT_NAME, (String) sports.getSelectedItem());
            recom_data.put(SPORT_DURATION, duration.getText().toString());
        }
        replaceFragment(new RecommendationConstraintsFragment(), "constraints");
    }

    public void onResultsClick(String energy, String fat, String prot, String carbo){
        addConstraintsToJSON(energy, fat, prot, carbo);
        send(networkJSON(RECOMMEND_REQUEST, recom_data));
        replaceFragment(new RecommendationResultsFragment(), "results");
    }

    public void restart(){
        replaceFragment(new RecommendationPastFragment(), "past");
    }

    public void onActivityResult(int requestCode, int resultCode, Intent intent){
        IntentResult scanResults = IntentIntegrator.parseActivityResult(requestCode, resultCode, intent);
        if (scanResults != null && scanResults.getContents() != null){
            String scanContent = scanResults.getContents();
            sendCode(scanContent);
        } else{
            Toast toast = Toast.makeText(this, "Scan Cancelled", Toast.LENGTH_SHORT);
            toast.show();
        }
    }

}
