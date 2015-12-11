package com.pds.app.caloriecounter;


import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.view.MenuItem;

public class RecommendationActivity extends HomeActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        v = getLayoutInflater().inflate(R.layout.activity_recommendation,frameLayout);


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
}
