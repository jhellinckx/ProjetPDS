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
import android.widget.Toast;

import org.json.simple.JSONObject;

import java.io.IOException;

import static org.calorycounter.shared.Constants.network.*;

public class RecommendationActivity extends HomeActivity implements RecommendationProcessFragment.OnItemClickListener,
        RecommendationTypeFragment.OnItemClickListener, RecommendationPastFragment.OnItemClickListener,
        RecommendationSportFragment.OnItemClickListener, RecommendationConstraintsFragment.OnItemClickListener,
        RecommendationResultsFragment.OnItemClickListener{

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
        transaction.add(R.id.fragment_layout, new RecommendationProcessFragment());
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
                    System.out.println(((String) data.get(SPORT_NAME + String.valueOf(i))));
                }
            }
        }
    }

    public void onStartButtonClick(){
        replaceFragment(new RecommendationTypeFragment());
    }

    public void onNextTypeClick(){
        replaceFragment(new RecommendationPastFragment());
    }

    public void onNextPastClick(){
        try {
            send(networkJSON(SPORTS_LIST_REQUEST, new JSONObject()));
        } catch (IOException e) {
            Toast toast = Toast.makeText(getBaseContext(), e.getMessage(), Toast.LENGTH_LONG);
            toast.show();
        }
        replaceFragment(new RecommendationSportFragment());
    }

    public void onNextSportClick(){
        replaceFragment(new RecommendationConstraintsFragment());
    }

    public void onResultsClick(){
        replaceFragment(new RecommendationResultsFragment());
    }

    public void restart(){
        replaceFragment(new RecommendationProcessFragment());
    }

}
