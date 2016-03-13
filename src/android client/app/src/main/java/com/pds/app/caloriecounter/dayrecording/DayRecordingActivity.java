package com.pds.app.caloriecounter.dayrecording;

import android.app.ActionBar;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.LinearLayoutCompat;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.pds.app.caloriecounter.ItemInfoDialog;
import com.pds.app.caloriecounter.MenuNavigableActivity;
import com.pds.app.caloriecounter.R;
import com.pds.app.caloriecounter.RecommendationActivity;
import com.pds.app.caloriecounter.itemview.EdibleItemActionCallback;
import com.pds.app.caloriecounter.itemview.EdibleItemList;
import com.pds.app.caloriecounter.itemview.SportActionCallback;
import com.pds.app.caloriecounter.itemview.SportList;
import com.pds.app.caloriecounter.rawlibs.CircularButton;
import com.pds.app.caloriecounter.utils.Converter;
import com.pds.app.caloriecounter.utils.EvenSpaceView;
import com.shehabic.droppy.DroppyClickCallbackInterface;
import com.shehabic.droppy.DroppyMenuItem;
import com.shehabic.droppy.DroppyMenuPopup;
import com.shehabic.droppy.animations.DroppyFadeInAnimation;

import org.calorycounter.shared.models.EdibleItem;
import org.calorycounter.shared.models.Food;
import org.calorycounter.shared.models.Recipe;
import org.calorycounter.shared.models.Sport;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import static com.pds.app.caloriecounter.GraphicsConstants.Global.*;
import static com.pds.app.caloriecounter.GraphicsConstants.ItemList.*;
import static com.pds.app.caloriecounter.GraphicsConstants.ItemSticker.*;
import static com.pds.app.caloriecounter.GraphicsConstants.Recording.TITLE_COLOR;
import static org.calorycounter.shared.Constants.network.CAL_TO_JOULE_FACTOR;
import static org.calorycounter.shared.Constants.network.CHILD_DAILY_ENERGY;
import static org.calorycounter.shared.Constants.network.CHOSEN_SPORT_REQUEST;
import static org.calorycounter.shared.Constants.network.DATA;
import static org.calorycounter.shared.Constants.network.FOOD_IS_EATEN;
import static org.calorycounter.shared.Constants.network.FOOD_IS_NEW;
import static org.calorycounter.shared.Constants.network.FOOD_LIST;
import static org.calorycounter.shared.Constants.network.FOOD_NAME;
import static org.calorycounter.shared.Constants.network.FOOD_QUANTITY;
import static org.calorycounter.shared.Constants.network.FOOD_TOTAL_CARBOHYDRATES;
import static org.calorycounter.shared.Constants.network.FOOD_TOTAL_ENERGY;
import static org.calorycounter.shared.Constants.network.FOOD_TOTAL_FAT;
import static org.calorycounter.shared.Constants.network.FOOD_TOTAL_PROTEINS;
import static org.calorycounter.shared.Constants.network.FOOD_TOTAL_SATURATED_FAT;
import static org.calorycounter.shared.Constants.network.FOOD_TOTAL_SODIUM;
import static org.calorycounter.shared.Constants.network.FOOD_TOTAL_SUGARS;
import static org.calorycounter.shared.Constants.network.HISTORY_DATE;
import static org.calorycounter.shared.Constants.network.HISTORY_FOR_DATE_REQUEST;
import static org.calorycounter.shared.Constants.network.HUMAN_DAILY_CARBOHYDRATES;
import static org.calorycounter.shared.Constants.network.HUMAN_DAILY_PROTEINS;
import static org.calorycounter.shared.Constants.network.MEN_DAILY_ENERGY;
import static org.calorycounter.shared.Constants.network.REQUEST_TYPE;
import static org.calorycounter.shared.Constants.network.SPORTS_LIST_REQUEST;
import static org.calorycounter.shared.Constants.network.SPORTS_LIST_RESPONSE;
import static org.calorycounter.shared.Constants.network.SPORTS_LIST_SIZE;
import static org.calorycounter.shared.Constants.network.SPORTS_LIST_SUCCESS;
import static org.calorycounter.shared.Constants.network.SPORT_DURATION;
import static org.calorycounter.shared.Constants.network.SPORT_NAME;
import static org.calorycounter.shared.Constants.network.CHANGE_EATEN_STATUS_REQUEST;
import static org.calorycounter.shared.Constants.network.DELETE_FOOD_HISTORY_REQUEST;
import static org.calorycounter.shared.Constants.network.SPORT_LIST;
import static org.calorycounter.shared.Constants.network.DELETE_SPORT_HISTORY_REQUEST;
import static org.calorycounter.shared.Constants.network.TEEN_DAILY_ENERGY;
import static org.calorycounter.shared.Constants.network.WOMEN_DAILY_ENERGY;
import static org.calorycounter.shared.Constants.network.FOOD_CODE;
//import static org.calorycounter.shared.Constants.network.RECOMMENDED_FOOD_REQUEST;
//import static org.calorycounter.shared.Constants.network.FOOD;
import static org.calorycounter.shared.Constants.network.networkJSON;
import static org.calorycounter.shared.Constants.date.SDFORMAT;

public class DayRecordingActivity extends MenuNavigableActivity implements EdibleItemActionCallback {

    private LinearLayout stickersLayout;
    private Map<String, IntakeProgress> dailyIntakes;
    private List<EdibleItem> dailyFoods;
    private List<Sport> dailySports;
    private DailyRecording sportsContainer;
    private static List<String> _sportNames = new ArrayList<String>();
    private SportActionCallback sac;
    private Context context;
    private EditText date;

    private String gender;
    private static float maxCal;
    private String current_day;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        v = getLayoutInflater().inflate(R.layout.activity_day_recording,frameLayout);
        stickersLayout = (LinearLayout) v.findViewById(R.id.day_recording_layout);
        dailyFoods = new ArrayList<>();
        dailySports = new ArrayList<Sport>();

        sac = new SportActionCallback() {
            @Override
            public void onRemoveSport(Sport sport) {
                if (sport.getEnergyConsumed() != null) {
                    IntakeProgress calorieProgress = dailyIntakes.get(TITLE_CALORIES);
                    if(calorieProgress != null){
                        calorieProgress.setIntakeMax(calorieProgress.getIntakeMax() - sport.getEnergyConsumed()/CAL_TO_JOULE_FACTOR);
                    }
                }
                JSONObject data = new JSONObject();
                data.put(SPORT_NAME, sport.toJSON());
                data.put(HISTORY_DATE, date.getText().toString());
                dailySports.remove(sport);
                send(networkJSON(DELETE_SPORT_HISTORY_REQUEST, data));
            }

            @Override
            public void onAddSport(String sportName, int duration) {
                JSONObject request = new JSONObject();
                request.put(SPORT_NAME, sportName);
                request.put(SPORT_DURATION, Integer.toString(duration));
                request.put(HISTORY_DATE, date.getText().toString());
                send(networkJSON(CHOSEN_SPORT_REQUEST, request));
            }
        };
        context= v.getContext();
        initDate();
        initIntakesRecording();
        sendHistoryForCurrentDayRequest();
    }

    private void initDate(){
        Bundle b = getIntent().getExtras();
        if (!getIntent().hasExtra("day")){
            current_day = SDFORMAT.format(Calendar.getInstance().getTime());
        }
        else{
            current_day = b.getString("day");
        }
        date = new EditText(context);
        date.setText(current_day);
        date.setTextColor(TITLE_COLOR);
        date.setGravity(Gravity.CENTER_HORIZONTAL);
        date.setEnabled(false);
        stickersLayout.addView(date);

    }

    private void sendHistoryForCurrentDayRequest(){
        JSONObject data = new JSONObject();
        data.put(HISTORY_DATE, date.getText().toString());
        send(networkJSON(HISTORY_FOR_DATE_REQUEST, data));
    }

    private void postResponseInitialisations(){
        initFoodsRecording();
        initSportsRecording();

        setintakesProgress();
    }

    private void initIntakesRecording(){
        LinearLayout intakesStickersLayout = new LinearLayout(this);
        intakesStickersLayout.setOrientation(LinearLayout.HORIZONTAL);
        Bundle b = getIntent().getExtras();
        if (!getIntent().hasExtra("gender")){
            gender = "M";
        }
        else{
            gender = b.getString("gender");
            maxCal = computeMaxEnergy();
        }

        dailyIntakes = new LinkedHashMap<>();
        /* Intake placeholders */

        if(getIntent().hasExtra("maxCal")){
            maxCal = getIntent().getExtras().getInt("maxCal");
        }

        IntakeProgress calorieIntake = new IntakeProgress(this, 0, maxCal, CALORIES_UNIT);
        dailyIntakes.put(TITLE_CALORIES, calorieIntake);
        IntakeProgress proteinIntake = new IntakeProgress(this, 0, HUMAN_DAILY_PROTEINS, DEFAULT_UNIT);
        dailyIntakes.put(TITLE_PROTEINS, proteinIntake);
        IntakeProgress carboIntake = new IntakeProgress(this, 0, HUMAN_DAILY_CARBOHYDRATES, DEFAULT_UNIT);
        dailyIntakes.put(TITLE_CARBO, carboIntake);

        LinearLayout intakesLayout = new LinearLayout(this);
        intakesLayout.setOrientation(LinearLayout.HORIZONTAL);

        /* Distribute open space in invisible views */
        for(String intake : dailyIntakes.keySet()){
            intakesLayout.addView(new EvenSpaceView(this));
            intakesLayout.addView(new IntakeProgressSticker(this, intake, dailyIntakes.get(intake)));
        }
        intakesLayout.addView(new EvenSpaceView(this));

        DailyRecording intakes = new DailyRecording(this, TITLE_RDI, intakesLayout);
        stickersLayout.addView(intakes);
    }

    private int computeMaxEnergy(){
        switch (gender){
            case "C":
                maxCal = CHILD_DAILY_ENERGY;
                break;
            case "T":
                maxCal = TEEN_DAILY_ENERGY;
                break;
            case "W":
                maxCal = WOMEN_DAILY_ENERGY;
                break;
            default:
                maxCal = MEN_DAILY_ENERGY;
                break;

        }
        return (int) ((maxCal/CAL_TO_JOULE_FACTOR));

    }

    private void initFoodsRecording(){

        DailyRecording foodsContainer = new DailyRecording(this, TITLE_FOODS, new EdibleItemList(this, dailyFoods, this, FLAG_REMOVABLE, FLAG_CHECKABLE, FLAG_RATABLE, FLAG_EXPANDABLE));

        LinearLayout addMenuLayout = new LinearLayout(this);
        addMenuLayout.setOrientation(LinearLayout.HORIZONTAL);

        CircularButton openDropdown = new CircularButton(this);
        LinearLayout.LayoutParams buttonParams = new LinearLayout.LayoutParams(IMAGE_WIDTH, IMAGE_HEIGHT);
        buttonParams.gravity = Gravity.RIGHT;
        openDropdown.setLayoutParams(buttonParams);
        openDropdown.setImageResource(R.drawable.ic_add_white_18dp);
        openDropdown.setButtonColor(getResources().getColor(R.color.primary));
        openDropdown.setShadowColor(Color.BLACK);

        addMenuLayout.addView(new EvenSpaceView(this));
        addMenuLayout.addView(openDropdown);
        foodsContainer.setFooter(addMenuLayout);

        stickersLayout.addView(foodsContainer);

        DroppyMenuPopup.Builder droppyBuilder = new DroppyMenuPopup.Builder(this, openDropdown);
        droppyBuilder.fromMenu(R.menu.foodslist_dropdown_add)
                .setOnClick(new DroppyClickCallbackInterface() {
                    @Override
                    public void call(View v, int id) {
                        String menuId = getResources().getResourceName(id).split("/")[1];
                        Log.d("MENU : ",menuId);
                        if(menuId.equals("addfood_scan"))
                            onAddScan();
                        else if(menuId.equals("addfood_article"))
                            onAddArticle();
                        else if(menuId.equals("addfood_receipt"))
                            onAddReceipt();

                    }
                })
                .setPopupAnimation(new DroppyFadeInAnimation())
                .setXOffset(5)
                .setYOffset(5)
                .build();
    }

    private void initSportsRecording(){
        if(_sportNames.size() != SPORTS_LIST_SIZE) {
            send(networkJSON(SPORTS_LIST_REQUEST, new JSONObject()));
        }

        sportsContainer = new DailyRecording(this, TITLE_SPORTS, new SportList(this, dailySports, sac, _sportNames,  FLAG_REMOVABLE));

        stickersLayout.addView(sportsContainer);
    }

    public void setintakesProgress(){
        for(EdibleItem item : dailyFoods){
            if (item.isEaten()) {
                substractToProgresses(item);
            }
        }
        for(Sport sport : dailySports){
            if (sport.getEnergyConsumed() != null){
                IntakeProgress calorieProgress = dailyIntakes.get(TITLE_CALORIES);
                if(calorieProgress != null){
                    calorieProgress.setIntakeMax(calorieProgress.getIntakeMax()+sport.getEnergyConsumed()/CAL_TO_JOULE_FACTOR);
                }
            }
        }
    }

    private void addToProgresses(EdibleItem item){
        if (item.getTotalEnergy() != null) {
            IntakeProgress calorieProgress = dailyIntakes.get(TITLE_CALORIES);
            if(calorieProgress != null){
                calorieProgress.substractIntakeProgress((float) Math.round(item.getTotalEnergy() / CAL_TO_JOULE_FACTOR));
            }
        }
        if(item.getTotalProteins() != null){
            IntakeProgress proteinProgress = dailyIntakes.get(TITLE_PROTEINS);
            if(proteinProgress != null){
                proteinProgress.substractIntakeProgress(item.getTotalProteins());
            }
        }
        if(item.getTotalCarbohydrates() != null){
            IntakeProgress carboProgress = dailyIntakes.get(TITLE_CARBO);
            if(carboProgress != null){
                carboProgress.substractIntakeProgress(item.getTotalCarbohydrates());
            }
        }
        //add others if needed
    }

    private void substractToProgresses(EdibleItem item){
        if (item.getTotalEnergy() != null) {
            IntakeProgress calorieProgress = dailyIntakes.get(TITLE_CALORIES);
            if(calorieProgress != null){
                calorieProgress.addIntakeProgress((float) Math.round(item.getTotalEnergy() / CAL_TO_JOULE_FACTOR));
            }
        }
        if(item.getTotalProteins() != null){
            IntakeProgress proteinProgress = dailyIntakes.get(TITLE_PROTEINS);
            if(proteinProgress != null){
                proteinProgress.addIntakeProgress(item.getTotalProteins());
            }
        }
        if(item.getTotalCarbohydrates() != null){
            IntakeProgress carboProgress = dailyIntakes.get(TITLE_CARBO);
            if(carboProgress != null){
                carboProgress.addIntakeProgress(item.getTotalCarbohydrates());
            }
        }
        //add others if needed
    }

    public void onAddScan(){
        Log.d("CLICK : ", " SCAN");
    }

    public void onAddArticle(){
        Log.d("CLICK : ", " ARTICLE");
        startNewRecomActivityWithInfos(false);
    }

    private void startNewRecomActivityWithInfos(Boolean isReceipt){
        Intent recommendactivity = new Intent(DayRecordingActivity.this, RecommendationActivity.class);

        ArrayList<String> productCodes = new ArrayList<String>();
        ArrayList<String> recipeIds = new ArrayList<String>();
        ArrayList<String> productDates = new ArrayList<String>();
        ArrayList<String> recipeDates = new ArrayList<String>();
        if(!dailyFoods.isEmpty()){
            for(int i=0 ; i<dailyFoods.size(); ++i){
                if(dailyFoods.get(i) instanceof Food){
                    Food currFood = (Food) dailyFoods.get(i);
                    productCodes.add(currFood.getCode());
                    productDates.add(current_day);
                }else{
                    Recipe currRecipie = (Recipe) dailyFoods.get(i);
                    recipeIds.add(Long.toString(currRecipie.getId()));
                    recipeDates.add(current_day);
                }
            }
        }
        recommendactivity.putStringArrayListExtra("pastFoodCodes",productCodes);
        recommendactivity.putStringArrayListExtra("pastFoodDates",productDates);
        recommendactivity.putStringArrayListExtra("pastRecipeIds",recipeIds);
        recommendactivity.putStringArrayListExtra("pastRecipeDates",recipeDates);
        float maxCal = dailyIntakes.get(TITLE_CALORIES).getIntakeMax();
        recommendactivity.putExtra("maxCal",maxCal);
        recommendactivity.putExtra("isReceipt", isReceipt);
        recommendactivity.putExtra("date", current_day);

        startActivity(recommendactivity);
    }

    public void onAddReceipt(){
        Log.d("CLICK : ", " RECEIPT");
        startNewRecomActivityWithInfos(true);
    }

    public void addPotentialRecommendation() {

        Bundle b = getIntent().getExtras();
        if (!getIntent().hasExtra("selectedItem")) {

        } else {
            String recomCode = b.getString("selectedItem");
        }
    }

    @Override
    public void onRemoveEdibleItem(EdibleItem item) {
        if(item.isEaten()) {
            addToProgresses(item);
        }
        JSONObject data = new JSONObject();
        data.put(FOOD_NAME, item.toJSON());
        data.put(HISTORY_DATE, date.getText().toString());
        dailyFoods.remove(item);
        send(networkJSON(DELETE_FOOD_HISTORY_REQUEST, data));
    }

    @Override
    public void onAddEdibleItem(EdibleItem item){
    }

    @Override
    public void onRateEdibleItem(EdibleItem item){

    }

    @Override
    public void onExpandEdibleItem(EdibleItem item){
        Bundle b = new Bundle();
        b.putString(FOOD_NAME, item.getProductName());
        b.putString(FOOD_QUANTITY, item.getQuantity());
        b.putFloat(FOOD_TOTAL_ENERGY, item.getTotalEnergy());
        b.putFloat(FOOD_TOTAL_FAT, item.getTotalFat());
        b.putFloat(FOOD_TOTAL_SATURATED_FAT, item.getTotalSaturatedFat());
        b.putFloat(FOOD_TOTAL_PROTEINS, item.getTotalProteins());
        b.putFloat(FOOD_TOTAL_SUGARS, item.getTotalSugars());
        b.putFloat(FOOD_TOTAL_SODIUM, item.getTotalSalt());
        b.putFloat(FOOD_TOTAL_CARBOHYDRATES, item.getTotalCarbohydrates());
        ItemInfoDialog dialog = new ItemInfoDialog();
        dialog.setArguments(b);
        dialog.show(getFragmentManager(), "infos");
    }

    @Override
    public void onCheckEdibleItem(EdibleItem item){
        JSONObject data = new JSONObject();
        data.put(FOOD_NAME, item.toJSON(false));
        data.put(HISTORY_DATE, date.getText().toString());
        int status = (item.isEaten()) ? 1 : 0;
        data.put(FOOD_IS_EATEN, status);
        data.put(FOOD_IS_NEW,0);
        if(item.isEaten()){
            substractToProgresses(item);
        }else {
            addToProgresses(item);
        }
        send(networkJSON(CHANGE_EATEN_STATUS_REQUEST, data));
    }


    @Override
    public void handleMessage(JSONObject msg){
        Log.d("DAYRECORDING HANDLE MSG", msg.toString());
        String request = (String) msg.get(REQUEST_TYPE);
        JSONObject data = (JSONObject)msg.get(DATA);
        if(request.equals(SPORTS_LIST_REQUEST)) {
            String response = (String) data.get(SPORTS_LIST_RESPONSE);
            if (response.equals(SPORTS_LIST_SUCCESS)) {
                Log.d("SPORTS DATA LIST : ", data.toString());
                for (int i = 0; i < data.size() - 1; ++i) {
                    _sportNames.add(((String) data.get(SPORT_NAME + String.valueOf(i))));
                }
            }
        }else if(request.equals(CHOSEN_SPORT_REQUEST)){
            final Sport newSport = new Sport();
            newSport.initFromJSON(data);
            dailySports.add(newSport);
            runOnUiThread(new Runnable() {
                public void run() {
                    stickersLayout.removeView((sportsContainer));
                    sportsContainer = new DailyRecording(context, TITLE_SPORTS, new SportList(context, dailySports, sac, _sportNames, FLAG_REMOVABLE));
                    stickersLayout.addView(sportsContainer);
                }
            });

            if (newSport.getEnergyConsumed() != null){
                if(dailyIntakes.get(TITLE_CALORIES) != null){
                    runOnUiThread(new Runnable() {
                        public void run() {
                            IntakeProgress calorieProgress = dailyIntakes.get(TITLE_CALORIES);
                            calorieProgress.setIntakeMax(calorieProgress.getIntakeMax() + newSport.getEnergyConsumed()/CAL_TO_JOULE_FACTOR);
                        }
                    });
                }
            }
        } else if (request.equals(HISTORY_FOR_DATE_REQUEST)) {
            JSONArray response = (JSONArray) data.get(FOOD_LIST);
            JSONArray responseSport = (JSONArray) data.get(SPORT_LIST);
            for (int i = 0; i < response.size(); i++){
                Food f = new Food();
                f.initFromJSON((JSONObject) response.get(i));
                dailyFoods.add(f);
            }
            for (int j = 0; j < responseSport.size(); j++){
                Sport s = new Sport();
                s.initFromJSON((JSONObject) responseSport.get(j));
                dailySports.add(s);
            }
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    postResponseInitialisations();
                }
            });

        }
    }



}
