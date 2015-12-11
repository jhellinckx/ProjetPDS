package com.pds.app.caloriecounter;


import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.view.MenuItem;

public class HistoryActivity extends HomeActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        v = getLayoutInflater().inflate(R.layout.activity_history,frameLayout);


    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_home) {
            Intent homeactivity = new Intent(HistoryActivity.this, Home.class);

            startActivity(homeactivity);
        } else if (id == R.id.nav_data) {
            Intent dataactivity = new Intent(HistoryActivity.this, PersonalDataActivity.class);

            startActivity(dataactivity);

        } else if (id == R.id.nav_recommend) {
            Intent recommendactivity = new Intent(HistoryActivity.this, RecommendationActivity.class);

            startActivity(recommendactivity);

        } else if (id == R.id.nav_rating) {
            Intent ratingactivity = new Intent(HistoryActivity.this, RatingActivity.class);

            startActivity(ratingactivity);


        } else if (id == R.id.nav_camera) {

            Intent cameractivity = new Intent(HistoryActivity.this, ScanningActivity.class);

            startActivity(cameractivity);

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
