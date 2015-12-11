package com.pds.app.caloriecounter;


import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.view.MenuItem;
import android.util.Log;
import android.widget.Toast;

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

public class ScanningActivity extends HomeActivity{

    private IntentIntegrator integrator;

    private void initScan(){
        integrator = new IntentIntegrator(this);
        integrator.setDesiredBarcodeFormats(IntentIntegrator.ONE_D_CODE_TYPES);
        integrator.setPrompt("Scan a barcode");

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        v = getLayoutInflater().inflate(R.layout.activity_camera,frameLayout);
        initScan();
        integrator.initiateScan();

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if(result != null) {
            if(result.getContents() == null) {
                Log.d("MainActivity", "Cancelled scan");
                Toast.makeText(this, "Cancelled", Toast.LENGTH_LONG).show();
            } else {
                Log.d("MainActivity", "Scanned");
                Toast.makeText(this, "Scanned: " + result.getContents(), Toast.LENGTH_LONG).show();
            }
        } else {
            Log.d("MainActivity", "Weird");
            // This is important, otherwise the result will not be passed to the fragment
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_home) {
            Intent homeactivity = new Intent(ScanningActivity.this, Home.class);

            startActivity(homeactivity);
        } else if (id == R.id.nav_data) {
            Intent datactivity = new Intent(ScanningActivity.this, PersonalDataActivity.class);

            startActivity(datactivity);

        } else if (id == R.id.nav_recommend) {
            Intent recommendactivity = new Intent(ScanningActivity.this, RecommendationActivity.class);

            startActivity(recommendactivity);


        } else if (id == R.id.nav_rating) {
            Intent ratingactivity = new Intent(ScanningActivity.this, RatingActivity.class);

            startActivity(ratingactivity);

        } else if (id == R.id.nav_history) {
            Intent historyactivity = new Intent(ScanningActivity.this, HistoryActivity.class);

            startActivity(historyactivity);

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
