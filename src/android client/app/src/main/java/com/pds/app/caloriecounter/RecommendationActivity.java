package com.pds.app.caloriecounter;


import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.widget.EditText;
import android.widget.Spinner;
import java.util.ArrayList;

import org.json.simple.JSONObject;


import static org.calorycounter.shared.Constants.network.*;

public class RecommendationActivity extends HomeActivity implements RecommendationPastFragment.OnItemClickListener,
        RecommendationSportFragment.OnItemClickListener, RecommendationConstraintsFragment.OnItemClickListener,
        RecommendationResultsFragment.OnItemClickListener{

    private static ArrayList<String> _sportsname = new ArrayList<String>();
    private static ArrayList<String> _productNames = new ArrayList<String>();

    private FragmentManager manager = getSupportFragmentManager();

    private void replaceFragment(Fragment fragment){
        FragmentTransaction transaction = manager.beginTransaction();
        transaction.replace(R.id.fragment_layout, fragment);
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
                for(int i = 0; i < data.size()-1 ; ++i){
                    _sportsname.add(((String) data.get(SPORT_NAME + String.valueOf(i))));
                }
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
                replaceFragment(frag);
            }
        }
        if (_sportsname.size() == SPORTS_LIST_SIZE) {
            RecommendationSportFragment frag = new RecommendationSportFragment();
            Bundle b = new Bundle();
            b.putStringArrayList("names", _sportsname);
            frag.setArguments(b);
            replaceFragment(frag);
        }
    }

    private void sendData(String sport, String duration){
        JSONObject data = new JSONObject();
        data.put(SPORT_NAME, sport);
        data.put(SPORT_DURATION, duration);
        send(networkJSON(CHOSEN_SPORT_REQUEST, data));
    }

    public void sendCode(String code) {
        JSONObject data = new JSONObject();
        data.put(FOOD_CODE, code);
        send(networkJSON(FOOD_CODE_REQUEST, data));
        System.out.println("------------------CODE SENT -------------------");
    }

    public void onNextPastClick(){
        if (_sportsname.size() == SPORTS_LIST_SIZE){
            RecommendationSportFragment frag = new RecommendationSportFragment();
            Bundle b = new Bundle();
            b.putStringArrayList("names", _sportsname);
            frag.setArguments(b);
            replaceFragment(frag);
        }
        else {
            send(networkJSON(SPORTS_LIST_REQUEST, new JSONObject()));
        }
    }

    public void onNextSportClick(Spinner sports, EditText duration){
        if(duration.getText().toString() != ""){
            sendData((String) sports.getSelectedItem(), duration.getText().toString());
        }
        replaceFragment(new RecommendationConstraintsFragment());
    }

    public void onResultsClick(){
        send(networkJSON(RECOMMEND_REQUEST, new JSONObject()));
        replaceFragment(new RecommendationResultsFragment());
    }

    public void restart(){
        replaceFragment(new RecommendationPastFragment());
    }

}
