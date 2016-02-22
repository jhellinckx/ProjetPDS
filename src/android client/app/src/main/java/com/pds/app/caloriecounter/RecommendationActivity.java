package com.pds.app.caloriecounter;


import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.view.MenuItem;

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

    public void onStartButtonClick(){
        replaceFragment(new RecommendationTypeFragment());
    }

    public void onNextTypeClick(){
        replaceFragment(new RecommendationPastFragment());
    }

    public void onNextPastClick(){
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
