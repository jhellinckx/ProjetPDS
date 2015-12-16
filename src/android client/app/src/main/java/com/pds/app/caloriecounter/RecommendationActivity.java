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

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_home) {
            Intent homeactivity = new Intent(RecommendationActivity.this, Home.class);

            startActivity(homeactivity);
        } else if (id == R.id.nav_data) {

        } else if (id == R.id.nav_camera) {
            Intent cameractivity = new Intent(RecommendationActivity.this, ScanningActivity.class);

            startActivity(cameractivity);

        } else if (id == R.id.nav_rating) {
            Intent ratingactivity = new Intent(RecommendationActivity.this, RatingActivity.class);

            startActivity(ratingactivity);

        } else if (id == R.id.nav_history) {
            Intent historyactivity = new Intent(RecommendationActivity.this, HistoryActivity.class);

            startActivity(historyactivity);

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
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
