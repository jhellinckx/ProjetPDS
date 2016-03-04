package com.pds.app.caloriecounter;

import android.graphics.Color;
import android.os.Bundle;

import java.util.ArrayList;

import android.support.v4.content.res.ResourcesCompat;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Button;
import android.content.Intent;



import org.json.simple.JSONObject;

import static org.calorycounter.shared.Constants.network.*;

public class PersonalDataActivity extends MenuNavigableActivity {

    private Spinner agebracket = null;
    private Button update = null;
    private Button kid = null;
    private Button teen = null;
    private Button man = null;
    private Button woman = null;
    private EditText weight = null;
    private EditText height = null;

    private static int _height = -1;
    private static float _weight = -1F;
    private static int id = 0;

    private void initButtons() {

        update = (Button) v.findViewById(R.id.update);
        update.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v){
                _height = Integer.parseInt(height.getText().toString());
                _weight = Float.parseFloat(weight.getText().toString());
                id = (int) agebracket.getSelectedItemId();

                sendData((String) agebracket.getSelectedItem(), weight.getText().toString());

                Intent homeActivity = new Intent(PersonalDataActivity.this,RecommendationActivity.class);

                startActivity(homeActivity);
            }

        });



        kid = (Button) v.findViewById(R.id.kid);
        kid.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                kid.setBackgroundResource(R.drawable.background_button_round_left_pressed);
                kid.setTextColor(Color.parseColor("#FFFFFF"));
                shutdownOtherButton(kid);
            }
        });

        teen = (Button) v.findViewById(R.id.teen);
        teen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                teen.setBackgroundResource(R.drawable.background_button_pressed);
                teen.setTextColor(Color.parseColor("#FFFFFF"));
                shutdownOtherButton(teen);
            }
        });

        man = (Button) v.findViewById(R.id.man);
        man.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                man.setBackgroundResource(R.drawable.background_button_pressed);
                man.setTextColor(Color.parseColor("#FFFFFF"));
                shutdownOtherButton(man);
            }
        });

        woman = (Button) v.findViewById(R.id.woman);
        woman.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                woman.setBackgroundResource(R.drawable.background_button_round_right_pressed);
                woman.setTextColor(Color.parseColor("#FFFFFF"));
                shutdownOtherButton(woman);
            }
        });



    }


    private void shutdownOtherButton(Button b){
        if(b==kid){
            teen.setBackgroundResource(R.drawable.background_button2);
            teen.setTextColor(Color.parseColor("#000000"));
            man.setBackgroundResource(R.drawable.background_button2);
            man.setTextColor(Color.parseColor("#000000"));
            woman.setBackgroundResource(R.drawable.backgroud_button_round_right);
            woman.setTextColor(Color.parseColor("#000000"));
        }else if(b==teen){
            kid.setBackgroundResource(R.drawable.background_button_round_left);
            kid.setTextColor(Color.parseColor("#000000"));
            man.setBackgroundResource(R.drawable.background_button2);
            man.setTextColor(Color.parseColor("#000000"));
            woman.setBackgroundResource(R.drawable.backgroud_button_round_right);
            woman.setTextColor(Color.parseColor("#000000"));
        }else if(b==man){
            kid.setBackgroundResource(R.drawable.background_button_round_left);
            kid.setTextColor(Color.parseColor("#000000"));
            teen.setBackgroundResource(R.drawable.background_button2);
            teen.setTextColor(Color.parseColor("#000000"));
            woman.setBackgroundResource(R.drawable.backgroud_button_round_right);
            woman.setTextColor(Color.parseColor("#000000"));
        }else if(b==woman){
            kid.setBackgroundResource(R.drawable.background_button_round_left);
            kid.setTextColor(Color.parseColor("#000000"));
            teen.setBackgroundResource(R.drawable.background_button2);
            teen.setTextColor(Color.parseColor("#000000"));
            man.setBackgroundResource(R.drawable.background_button2);
            man.setTextColor(Color.parseColor("#000000"));
        }
    }

    private ArrayList<String> createCategoriesList(){
        ArrayList<String> categories = new ArrayList<String>();
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
        //agebracket = (Spinner) v.findViewById(R.id.agebracket);

        //ArrayAdapter<String> ageAdapter = new ArrayAdapter<String>(this,R.layout.spinner_item, createCategoriesList());
        //agebracket.setAdapter(ageAdapter);
        //agebracket.setSelection(id);

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        v = getLayoutInflater().inflate(R.layout.activity_personal,frameLayout);
        weight = (EditText) v.findViewById(R.id.data_weight);
        height = (EditText) v.findViewById(R.id.data_height);
        if(_weight >= 0){
            weight.setText(Float.toString(_weight));
        }
        if (_height >= 0){
            height.setText(Integer.toString(_height));
        }

        initSpinner();
        initButtons();

    }

    @Override
    public void onBackPressed(){

    }
}
