package com.pds.app.caloriecounter;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.InputType;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.pds.app.caloriecounter.dayrecording.DailyRecording;
import com.pds.app.caloriecounter.dayrecording.DayRecordingActivity;
import com.pds.app.caloriecounter.itemview.SportActionCallback;
import com.pds.app.caloriecounter.rawlibs.CircularButton;
import com.pds.app.caloriecounter.utils.EvenSpaceView;

import org.json.simple.JSONObject;

import java.util.ArrayList;

import static com.pds.app.caloriecounter.GraphicsConstants.Global.TITLE_INFOS;
import static com.pds.app.caloriecounter.GraphicsConstants.ItemSticker.IMAGE_HEIGHT;
import static com.pds.app.caloriecounter.GraphicsConstants.ItemSticker.IMAGE_WIDTH;
import static com.pds.app.caloriecounter.GraphicsConstants.ItemSticker.MAIN_TEXT_COLOR;
import static com.pds.app.caloriecounter.GraphicsConstants.ItemSticker.MAIN_TEXT_MAX_LINES;
import static com.pds.app.caloriecounter.GraphicsConstants.ItemSticker.MAIN_TEXT_SIZE;
import static org.calorycounter.shared.Constants.network.CAL_TO_JOULE_FACTOR;
import static org.calorycounter.shared.Constants.network.CHILD_DAILY_ENERGY;
import static org.calorycounter.shared.Constants.network.MEN_DAILY_ENERGY;
import static org.calorycounter.shared.Constants.network.TEEN_DAILY_ENERGY;
import static org.calorycounter.shared.Constants.network.UPDATE_DATA_GENDER;
import static org.calorycounter.shared.Constants.network.UPDATE_DATA_REQUEST;
import static org.calorycounter.shared.Constants.network.UPDATE_DATA_WEIGHT;
import static org.calorycounter.shared.Constants.network.UPDATE_DATA_HEIGHT;
import static org.calorycounter.shared.Constants.network.WOMEN_DAILY_ENERGY;
import static org.calorycounter.shared.Constants.network.DATA_REQUEST;
import static org.calorycounter.shared.Constants.network.REQUEST_TYPE;
import static org.calorycounter.shared.Constants.network.DATA;

import static org.calorycounter.shared.Constants.network.networkJSON;


public class PersonalDataActivity extends MenuNavigableActivity {

    private Spinner ageBracketSpinner = null;
    private SeekBar calorieSeekBar = null;
    private EditText calorieSeekBarEditText = null;
    private EditText heightEditText = null;
    private EditText weightEditText = null;

    private LinearLayout stickersLayout;
    private DailyRecording infosContainer = null;
    private LinearLayout infosLayout;

    private static float _height = -1F;
    private static float _weight = -1F;
    private static int id = 0;
    private static int _energy = 0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        v = getLayoutInflater().inflate(R.layout.activity_day_recording,frameLayout);
        stickersLayout = (LinearLayout) v.findViewById(R.id.day_recording_layout);
        stickersLayout.setOrientation(LinearLayout.VERTICAL);

        sendDataRequest();

        initInfosLayout();
        addAgeBracketLayout();
        addHeightLayout();
        addWeightLayout();
        addCalorieTextLayout();
        addCalorieLayout();

        _energy = computeMaxEnergy();

        infosContainer = new DailyRecording(this, TITLE_INFOS, infosLayout);
        addFooterButton();

        if(_weight >= 0){
            weightEditText.setText(Float.toString(_weight));
        }
        if (_height >= 0){
            heightEditText.setText(Float.toString(_height));
        }
        if(_energy>0){
            calorieSeekBarEditText.setText(Integer.toString(_energy));
            calorieSeekBar.setProgress(_energy);
        }
    }

    private void sendDataRequest(){
        JSONObject data = new JSONObject();
        send(networkJSON(DATA_REQUEST, data));
    }

    private void initInfosLayout(){
        infosLayout = new LinearLayout(this);
        LinearLayout.LayoutParams textContParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
        infosLayout.setLayoutParams(textContParams);
        infosLayout.setOrientation(LinearLayout.VERTICAL);
        infosLayout.setGravity(Gravity.CENTER_VERTICAL);

    }

    private void addHeightLayout(){
        LinearLayout heightTextLayout = new LinearLayout(this);
        LinearLayout.LayoutParams heightTextContParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
        heightTextLayout.setLayoutParams(heightTextContParams);
        heightTextLayout.setOrientation(LinearLayout.HORIZONTAL);
        heightTextLayout.setGravity(Gravity.CENTER_VERTICAL);
        //Taille - text
        TextView secondaryText = new TextView(this);
        LinearLayout.LayoutParams heightTextParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        secondaryText.setLayoutParams(heightTextParams);
        secondaryText.setTextSize(MAIN_TEXT_SIZE);
        secondaryText.setTextColor(MAIN_TEXT_COLOR);
        secondaryText.setText("Taille: ");
        secondaryText.setMaxLines(MAIN_TEXT_MAX_LINES);
        secondaryText.canScrollHorizontally(LinearLayout.HORIZONTAL);
        secondaryText.setEllipsize(TextUtils.TruncateAt.END);
        heightTextLayout.addView(secondaryText);
        //Taille - EditText
        heightEditText = new EditText(this);
        LinearLayout.LayoutParams heightEditTextParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        heightEditText.setLayoutParams(heightEditTextParams);
        heightEditText.setTextSize(MAIN_TEXT_SIZE);
        heightEditText.setTextColor(MAIN_TEXT_COLOR);
        heightEditText.setHintTextColor(MAIN_TEXT_COLOR);
        heightEditText.setHint("En cm");
        heightEditText.setInputType(InputType.TYPE_CLASS_NUMBER);
        heightEditText.setMaxLines(MAIN_TEXT_MAX_LINES);
        heightEditText.canScrollHorizontally(LinearLayout.HORIZONTAL);
        heightEditText.setEllipsize(TextUtils.TruncateAt.END);
        heightTextLayout.addView(heightEditText);
        //ajout Taille
        infosLayout.addView(heightTextLayout);
    }

    private void addWeightLayout() {
        LinearLayout weightTextLayout = new LinearLayout(this);
        LinearLayout.LayoutParams weightTextContParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
        weightTextLayout.setLayoutParams(weightTextContParams);
        weightTextLayout.setOrientation(LinearLayout.HORIZONTAL);
        weightTextLayout.setGravity(Gravity.CENTER_VERTICAL);

        //Poids - texte
        TextView weightText = new TextView(this);
        LinearLayout.LayoutParams weightTextParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        weightText.setLayoutParams(weightTextParams);
        weightText.setTextSize(MAIN_TEXT_SIZE);
        weightText.setTextColor(MAIN_TEXT_COLOR);
        weightText.setText("Poids: ");
        weightText.setMaxLines(MAIN_TEXT_MAX_LINES);
        weightText.canScrollHorizontally(LinearLayout.HORIZONTAL);
        weightText.setEllipsize(TextUtils.TruncateAt.END);
        weightTextLayout.addView(weightText);

        weightEditText = new EditText(this);
        LinearLayout.LayoutParams weightEditTextParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        weightEditText.setLayoutParams(weightEditTextParams);
        weightEditText.setTextSize(MAIN_TEXT_SIZE);
        weightEditText.setTextColor(MAIN_TEXT_COLOR);
        weightEditText.setHintTextColor(MAIN_TEXT_COLOR);
        weightEditText.setHint("En kg");
        weightEditText.setInputType(InputType.TYPE_CLASS_NUMBER);
        weightEditText.setMaxLines(MAIN_TEXT_MAX_LINES);
        weightEditText.canScrollHorizontally(LinearLayout.HORIZONTAL);
        weightEditText.setEllipsize(TextUtils.TruncateAt.END);
        weightTextLayout.addView(weightEditText);
        infosLayout.addView(weightTextLayout);


    }

    private void addAgeBracketLayout() {
        LinearLayout ageBracketTextLayout = new LinearLayout(this);
        LinearLayout.LayoutParams ageBracketTextContParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
        ageBracketTextLayout.setLayoutParams(ageBracketTextContParams);
        ageBracketTextLayout.setOrientation(LinearLayout.HORIZONTAL);
        ageBracketTextLayout.setGravity(Gravity.CENTER_VERTICAL);

        //Age Bracket - texte
        TextView ageBracketText = new TextView(this);
        LinearLayout.LayoutParams ageBracketTextParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        ageBracketText.setLayoutParams(ageBracketTextParams);
        ageBracketText.setTextSize(MAIN_TEXT_SIZE);
        ageBracketText.setTextColor(MAIN_TEXT_COLOR);
        ageBracketText.setText("Catégorie d'âge: ");
        ageBracketText.setMaxLines(MAIN_TEXT_MAX_LINES);
        ageBracketText.canScrollHorizontally(LinearLayout.HORIZONTAL);
        ageBracketText.setEllipsize(TextUtils.TruncateAt.END);
        ageBracketTextLayout.addView(ageBracketText);

        ageBracketSpinner = new Spinner(this);
        LinearLayout.LayoutParams ageBracketSpinnerParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        ageBracketSpinner.setLayoutParams(ageBracketSpinnerParams);

        ageBracketSpinner.canScrollHorizontally(LinearLayout.HORIZONTAL);
        initSpinner();
        ageBracketSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                updateSeekBarAndText();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                // your code here
            }
        });

        ageBracketTextLayout.addView(ageBracketSpinner);
        infosLayout.addView(ageBracketTextLayout);
    }

    private void addCalorieTextLayout(){
        LinearLayout calorieTextLayout = new LinearLayout(this);
        LinearLayout.LayoutParams calorieSliderContParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
        calorieTextLayout.setLayoutParams(calorieSliderContParams);
        calorieTextLayout.setOrientation(LinearLayout.HORIZONTAL);
        calorieTextLayout.setGravity(Gravity.CENTER_VERTICAL);

        TextView calorieText = new TextView(this);
        LinearLayout.LayoutParams calorieTextParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        calorieText.setLayoutParams(calorieTextParams);
        calorieText.setTextSize(MAIN_TEXT_SIZE);
        calorieText.setTextColor(MAIN_TEXT_COLOR);
        calorieText.setText("Calories du jour : ");
        calorieText.setMaxLines(MAIN_TEXT_MAX_LINES);
        calorieText.canScrollHorizontally(LinearLayout.HORIZONTAL);
        calorieText.setEllipsize(TextUtils.TruncateAt.END);
        calorieTextLayout.addView(calorieText);

        calorieSeekBarEditText = new EditText(this);
        LinearLayout.LayoutParams calorieSeekBarEditTextParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        calorieSeekBarEditText.setLayoutParams(calorieSeekBarEditTextParams);
        calorieSeekBarEditText.setTextSize(MAIN_TEXT_SIZE);
        calorieSeekBarEditText.setTextColor(MAIN_TEXT_COLOR);
        calorieSeekBarEditText.setHintTextColor(MAIN_TEXT_COLOR);
        calorieSeekBarEditText.setHint("");
        calorieSeekBarEditText.setInputType(InputType.TYPE_CLASS_NUMBER);
        calorieSeekBarEditText.setMaxLines(MAIN_TEXT_MAX_LINES);
        calorieSeekBarEditText.canScrollHorizontally(LinearLayout.HORIZONTAL);
        calorieSeekBarEditText.setEllipsize(TextUtils.TruncateAt.END);
        calorieSeekBarEditText.setFocusable(false);
        calorieTextLayout.addView(calorieSeekBarEditText);


        infosLayout.addView(calorieTextLayout);
    }

    private void addCalorieLayout() {
        //calorie seekBar+editText
        LinearLayout calorieSliderLayout = new LinearLayout(this);
        LinearLayout.LayoutParams calorieSliderContParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
        calorieSliderLayout.setLayoutParams(calorieSliderContParams);
        calorieSliderLayout.setOrientation(LinearLayout.HORIZONTAL);
        calorieSliderLayout.setGravity(Gravity.CENTER_VERTICAL);


        //calorie - seekBar
        calorieSeekBar = new SeekBar(this);
        LinearLayout.LayoutParams calorieSeekBarParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        calorieSeekBar.setLayoutParams(calorieSeekBarParams);
        calorieSeekBar.canScrollHorizontally(LinearLayout.HORIZONTAL);
        calorieSliderLayout.addView(calorieSeekBar);

        updateSeekBarAndText();
        addSeekBarListener();

        //ajout calorie
        infosLayout.addView(calorieSliderLayout);
    }

    public void addFooterButton(){
        LinearLayout validateLayout = new LinearLayout(this);
        validateLayout.setOrientation(LinearLayout.HORIZONTAL);
        CircularButton validate = new CircularButton(this);
        LinearLayout.LayoutParams buttonParams = new LinearLayout.LayoutParams(IMAGE_WIDTH, IMAGE_HEIGHT);
        buttonParams.gravity = Gravity.RIGHT;
        validate.setLayoutParams(buttonParams);
        validate.setImageResource(R.drawable.ic_check);
        validate.setButtonColor(getResources().getColor(R.color.primary));
        validate.setShadowColor(Color.BLACK);

        validateLayout.addView(new EvenSpaceView(this));
        validateLayout.addView(validate);
        infosContainer.setFooter(validateLayout);

        stickersLayout.addView(infosContainer);

        validate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!validate(heightEditText.getText().toString(), weightEditText.getText().toString())) {
                    return;
                }

                id = (int) ageBracketSpinner.getSelectedItemId();
                sendData();
                Intent dayRecordingActivity = new Intent(PersonalDataActivity.this, DayRecordingActivity.class);

                dayRecordingActivity.putExtra("maxCal", _energy);
                startActivity(dayRecordingActivity);

            }

        });
    }

    private Boolean validate(String height, String weight){
        if(height.length()!=0 && weight.length()!=0){
            _height = Float.parseFloat(height);
            _weight = Float.parseFloat(weight);
            if(_height < 250. && _height >40.){
                if(_weight < 250. && _weight >30.){
                    return true;
                }else{
                    showToast("Le Poids doit se retrouver entre 30kg et 250kg");
                    return false;
                }
            }else{
                showToast("La Taille doit se trouver entre 40cm et 250cm");
                return false;
            }

        }else if(height.length()==0 && weight.length()!=0){
            showToast("Taille manquante");
            return false;
        }else if(height.length()!=0 && weight.length()==0){
            showToast("Poids manquant");
            return false;
        } else {
            showToast("Taille manquante");
            showToast("Poids manquant");
            return false;
        }
    }

    private void showToast(String message) {
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
        categories.add("Enfant");
        categories.add("Adolescent");
        categories.add("Femme");
        categories.add("Homme");

        return categories;
    }

    private void sendData(){
        JSONObject data = new JSONObject();
        data.put(UPDATE_DATA_GENDER, ageBracketSpinner.getSelectedItem().toString());
        data.put(UPDATE_DATA_WEIGHT, weightEditText.getText().toString());
        data.put(UPDATE_DATA_HEIGHT, heightEditText.getText().toString());
        send(networkJSON(UPDATE_DATA_REQUEST, data));

    }

    private void initSpinner(){
        ArrayAdapter<String> ageAdapter = new ArrayAdapter<String>(this,R.layout.spinner_item, createCategoriesList());
        ageBracketSpinner.setAdapter(ageAdapter);
        ageBracketSpinner.setSelection(id);

    }


    private void addSeekBarListener(){
        calorieSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                calorieSeekBarEditText.setText(Integer.toString(progress));
                _energy = progress;
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
        calorieSeekBar.setMax(computeMaxEnergy());
        text = calorieSeekBarEditText;
        setText(text, calorieSeekBar.getMax());
        calorieSeekBar.setProgress(calorieSeekBar.getMax());
        return calorieSeekBar;

    }

    private int computeMaxEnergy(){
        id = (int) ageBracketSpinner.getSelectedItemId();
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
    public void onBackPressed(){

    }

    public void handleMessage(JSONObject msg){
        Log.d("Personal Acti HANDLE MSG", msg.toString());
        String request = (String) msg.get(REQUEST_TYPE);
        JSONObject data = (JSONObject)msg.get(DATA);
        if(request.equals(DATA_REQUEST) && (double) data.get(UPDATE_DATA_HEIGHT) != -1.0){
            String gender = (String) data.get(UPDATE_DATA_GENDER);
            genderToSpinerId(gender);
            initSpinner();

            heightEditText.setText(String.valueOf((double) data.get(UPDATE_DATA_HEIGHT))) ;
            weightEditText.setText(String.valueOf((double) data.get(UPDATE_DATA_WEIGHT))) ;
        }

    }

    public void genderToSpinerId(String gender){
        switch (gender){
            case "C":
                id = 0;
                break;
            case "T":
                id = 1;
                break;
            case "F":
                id = 2;
                break;
            default:
                id = 3;
                break;

        }

    }
}