package com.pds.app.caloriecounter;

import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.os.Bundle;

import java.io.IOException;
import java.util.ArrayList;

import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Button;
import android.content.Intent;

import org.json.simple.JSONObject;

import static org.calorycounter.shared.Constants.network.*;

public class PersonalDataActivity extends HomeActivity {

    private Spinner agebracket = null;
    private Button update = null;
    private EditText weight = null;

    private void initButton() {

        update = (Button) v.findViewById(R.id.update);

        update.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v){

                sendData((String) agebracket.getSelectedItem(), weight.getText().toString());

                Intent homeActivity = new Intent(PersonalDataActivity.this,RecommendationActivity.class);

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

    private void sendData(String gender, String weight){
        JSONObject data = new JSONObject();
        data.put(UPDATE_DATA_GENDER, gender);
        data.put(UPDATE_DATA_WEIGHT, weight);
        send(networkJSON(UPDATE_DATA_REQUEST, data));

    }

    private void initSpinner(){
        agebracket = (Spinner) v.findViewById(R.id.agebracket);

        ArrayAdapter<String> ageAdapter = new ArrayAdapter<String>(this,R.layout.spinner_item, createCategoriesList());
        agebracket.setAdapter(ageAdapter);

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        v = getLayoutInflater().inflate(R.layout.activity_personal,frameLayout);
        weight = (EditText) v.findViewById(R.id.data_weight);

        initSpinner();
        initButton();

    }

    @Override
    public void onBackPressed(){

    }
}
