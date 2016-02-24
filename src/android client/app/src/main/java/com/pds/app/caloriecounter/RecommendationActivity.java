package com.pds.app.caloriecounter;


import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.util.Log;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;
import java.util.ArrayList;

import org.json.simple.JSONObject;

import java.io.IOException;

import static org.calorycounter.shared.Constants.network.*;

public class RecommendationActivity extends HomeActivity implements RecommendationPastFragment.OnItemClickListener,
        RecommendationSportFragment.OnItemClickListener, RecommendationConstraintsFragment.OnItemClickListener,
        RecommendationResultsFragment.OnItemClickListener{

    private static ArrayList<String> _sportsname = new ArrayList<String>();

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

        FragmentTransaction transaction = manager.beginTransaction();
        transaction.add(R.id.fragment_layout, new RecommendationPastFragment());
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
