package com.pds.app.caloriecounter;


import android.os.Bundle;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.view.MenuItem;
import android.content.Intent;

public class RatingActivity extends HomeActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        v = getLayoutInflater().inflate(R.layout.activity_rating,frameLayout);

    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_home) {
            Intent homeactivity = new Intent(RatingActivity.this, Home.class);

            startActivity(homeactivity);
        } else if (id == R.id.nav_data) {
            Intent dataactivity = new Intent(RatingActivity.this, PersonalDataActivity.class);

            startActivity(dataactivity);

        } else if (id == R.id.nav_recommend) {
            Intent recommendactivity = new Intent(RatingActivity.this, RecommendationActivity.class);

            startActivity(recommendactivity);

        } else if (id == R.id.nav_camera) {
            Intent cameractivity = new Intent(RatingActivity.this, ScanningActivity.class);

            startActivity(cameractivity);

        } else if (id == R.id.nav_history) {
            Intent historyactivity = new Intent(RatingActivity.this, HistoryActivity.class);

            startActivity(historyactivity);

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
