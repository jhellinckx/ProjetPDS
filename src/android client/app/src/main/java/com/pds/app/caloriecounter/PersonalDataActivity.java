package com.pds.app.caloriecounter;

import android.os.Bundle;

import java.util.ArrayList;

import android.view.Gravity;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.Button;
import android.content.Intent;
import android.widget.Toast;

import com.pds.app.caloriecounter.dayrecording.DayRecordingActivity;

import org.json.simple.JSONObject;

import static org.calorycounter.shared.Constants.network.*;

public class PersonalDataActivity extends MenuNavigableActivity {

    private Spinner agebracket = null;
    private Button update = null;
    private EditText weight = null;
    private EditText height = null;
    private SeekBar bar = null;
    private EditText seekBarText = null;

    private static int _height = -1;
    private static float _weight = -1F;
    private static int id = 0;
    private static int _energy = 0;

    private void initButton() {

        update = (Button) v.findViewById(R.id.update);

        update.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v){
                String h = height.getText().toString();
                String w = weight.getText().toString();
                if(!validate(h,w)){
                    return;
                }
                id = (int) agebracket.getSelectedItemId();

                sendData((String) agebracket.getSelectedItem(), weight.getText().toString());

                Intent dayRecordingActivity = new Intent(PersonalDataActivity.this,DayRecordingActivity.class);

                startActivity(dayRecordingActivity);
            }

        });
    }

    private Boolean validate(String height, String weight){
        if(height.length()!=0 && weight.length()!=0){
            _height = Integer.parseInt(height);
            _weight = Float.parseFloat(weight);
            if(_height < 250 && _height >40){
                if(_weight < 250. && _weight >35.){
                    return true;
                }else{
                    showToast("Weight Must be between 35kg and 250kg");
                    return false;
                }
            }else{
                showToast("Height Must be between 40cm and 250cm");
                return false;
            }

        }else if(height.length()==0 && weight.length()!=0){
            showToast("Height Missing");
            return false;
        }else if(height.length()!=0 && weight.length()==0){
            showToast("Weight Missing");
            return false;
        } else {
            showToast("Height Missing");
            showToast("Weight Missing");
            return false;
        }
    }

    private void showToast(String message){
        final String m = message;
        runOnUiThread(new Runnable() {
            public void run() {
                Toast toast = Toast.makeText(getBaseContext(), m, Toast.LENGTH_LONG);
                toast.setGravity(Gravity.BOTTOM, 0, 170);
                toast.show();
            }
        });
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
        agebracket = (Spinner) v.findViewById(R.id.agebracket);

        ArrayAdapter<String> ageAdapter = new ArrayAdapter<String>(this,R.layout.spinner_item, createCategoriesList());
        agebracket.setAdapter(ageAdapter);
        agebracket.setSelection(id);

    }

    private void addSeekBarListener(SeekBar bar, final EditText text){
        bar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                text.setText(Integer.toString(progress));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
    }


    private void setText(EditText text, int progress){
        text.setText(Integer.toString(progress));
    }

    private SeekBar updateSeekBarAndText(){
        EditText text = null;
        seekBarText = (EditText) v.findViewById(R.id.constraints_cal);
        bar = (SeekBar) v.findViewById(R.id.bar_cal);
        bar.setMax(computeMaxEnergy());
        text = seekBarText;
        setText(text, bar.getMax());
        bar.setProgress(bar.getMax());
        return bar;

    }

    private int computeMaxEnergy(){
        id = (int) agebracket.getSelectedItemId();
        float max_energy;
        switch (id){
            case 0:
                max_energy = CHILD_DAILY_ENERGY;
                break;
            case 1:
                max_energy = TEEN_DAILY_ENERGY;
                break;
            case 2:
                max_energy = WOMEN_DAILY_ENERGY;
                break;
            default:
                max_energy = MEN_DAILY_ENERGY;
                break;

        }
        return (int) ((max_energy/CAL_TO_JOULE_FACTOR));

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
        initButton();
        bar = (SeekBar) v.findViewById(R.id.bar_cal);
        addSeekBarListener(updateSeekBarAndText(), seekBarText);

    }

    @Override
    public void onBackPressed(){

    }
}