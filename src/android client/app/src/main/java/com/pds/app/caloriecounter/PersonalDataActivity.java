package com.pds.app.caloriecounter;

import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.os.Bundle;
import java.util.ArrayList;

import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Button;
import android.content.Intent;

public class PersonalDataActivity extends HomeActivity {

    private Spinner agebracket = null;
    private Button update = null;

    private void initButton() {

        update = (Button) v.findViewById(R.id.update);

        update.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v){

                // Todo send updated data to server.

                Intent homeActivity = new Intent(PersonalDataActivity.this, Home.class);

                startActivity(homeActivity);
            }

        });
    }

    private ArrayList<String> createCategoriesList(){
        ArrayList<String> categories = new ArrayList<String>();
        categories.add("Baby");
        categories.add("Child");
        categories.add("Teen");
        categories.add("Woman");
        categories.add("Man");

        return categories;
    }

    private void initSpinner(){
        agebracket = (Spinner) v.findViewById(R.id.agebracket);

        ArrayAdapter<String> ageAdapter = new ArrayAdapter<String>(this,android.R.layout.simple_spinner_item, createCategoriesList());
        ageAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        agebracket.setAdapter(ageAdapter);


        agebracket.setOnItemSelectedListener(new OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Todo Auto-generated method stub;
            }
        });

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        v = getLayoutInflater().inflate(R.layout.activity_personal,frameLayout);

        initSpinner();
        initButton();

    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_home) {
            Intent homeactivity = new Intent(PersonalDataActivity.this, Home.class);

            startActivity(homeactivity);
        } else if (id == R.id.nav_camera) {
            Intent cameractivity = new Intent(PersonalDataActivity.this, ScanningActivity.class);

            startActivity(cameractivity);

        } else if (id == R.id.nav_recommend) {
            Intent recommendactivity = new Intent(PersonalDataActivity.this, RecommendationActivity.class);

            startActivity(recommendactivity);


        } else if (id == R.id.nav_rating) {
            Intent ratingactivity = new Intent(PersonalDataActivity.this, RatingActivity.class);

            startActivity(ratingactivity);

        } else if (id == R.id.nav_history) {
            Intent historyactivity = new Intent(PersonalDataActivity.this, HistoryActivity.class);

            startActivity(historyactivity);

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
