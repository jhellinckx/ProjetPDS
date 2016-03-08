package com.pds.app.caloriecounter.dayrecording;

import android.app.ActionBar;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutCompat;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.pds.app.caloriecounter.MenuNavigableActivity;
import com.pds.app.caloriecounter.R;
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
import static org.calorycounter.shared.Constants.network.CHOSEN_SPORT_REQUEST;
import static org.calorycounter.shared.Constants.network.DATA;
import static org.calorycounter.shared.Constants.network.FOOD_LIST;
import static org.calorycounter.shared.Constants.network.HISTORY_DATE;
import static org.calorycounter.shared.Constants.network.HISTORY_FOR_DATE_REQUEST;
import static org.calorycounter.shared.Constants.network.REQUEST_TYPE;
import static org.calorycounter.shared.Constants.network.SPORTS_LIST_REQUEST;
import static org.calorycounter.shared.Constants.network.SPORTS_LIST_RESPONSE;
import static org.calorycounter.shared.Constants.network.SPORTS_LIST_SIZE;
import static org.calorycounter.shared.Constants.network.SPORTS_LIST_SUCCESS;
import static org.calorycounter.shared.Constants.network.SPORT_DURATION;
import static org.calorycounter.shared.Constants.network.SPORT_NAME;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        v = getLayoutInflater().inflate(R.layout.activity_day_recording,frameLayout);
        stickersLayout = (LinearLayout) v.findViewById(R.id.day_recording_layout);
        dailyFoods = new ArrayList<>();
        sac = new SportActionCallback() {
            @Override
            public void onRemoveSport(Sport sport) {
                if (sport.getEnergyConsumed() != null) {
                    IntakeProgress calorieProgress = dailyIntakes.get(TITLE_CALORIES);
                    if(calorieProgress != null){
                        calorieProgress.setIntakeMax(calorieProgress.getIntakeMax() - sport.getEnergyConsumed());
                    }
                }
            }

            @Override
            public void onAddSport(String sportName, int duration) {
                JSONObject request = new JSONObject();
                request.put(SPORT_NAME, sportName);
                request.put(SPORT_DURATION, Integer.toString(duration));
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
        String current_day;
        if (b == null){
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

        dailyIntakes = new LinkedHashMap<>();
        /* Intake placeholders */
        IntakeProgress calorieIntake = new IntakeProgress(this, 0, 2000, CALORIES_UNIT);
        dailyIntakes.put(TITLE_CALORIES, calorieIntake);
        IntakeProgress proteinIntake = new IntakeProgress(this, 0, 40, DEFAULT_UNIT);
        dailyIntakes.put(TITLE_PROTEINS, proteinIntake);
        IntakeProgress carboIntake = new IntakeProgress(this, 0, 300, DEFAULT_UNIT);
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

    private void initFoodsRecording(){

        DailyRecording foodsContainer = new DailyRecording(this, TITLE_FOODS, new EdibleItemList(this, dailyFoods, this, FLAG_REMOVABLE, FLAG_ADDABLE, FLAG_RATABLE));

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
        if(_sportNames.size() != SPORTS_LIST_SIZE){
            send(networkJSON(SPORTS_LIST_REQUEST, new JSONObject()));
        }
        dailySports = new ArrayList<Sport>();

        sportsContainer = new DailyRecording(this, TITLE_SPORTS, new SportList(this, dailySports, sac, _sportNames,  FLAG_REMOVABLE));

        stickersLayout.addView(sportsContainer);
    }

    public void setintakesProgress(){
        for(EdibleItem item : dailyFoods){
            if (item.getTotalEnergy() != null) {
                IntakeProgress calorieProgress = dailyIntakes.get(TITLE_CALORIES);
                if(calorieProgress != null){
                    calorieProgress.addIntakeProgress(item.getTotalEnergy());
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
        }
        for(Sport sport : dailySports){
            if (sport.getEnergyConsumed() != null){
                IntakeProgress calorieProgress = dailyIntakes.get(TITLE_CALORIES);
                if(calorieProgress != null){
                    calorieProgress.setIntakeMax(calorieProgress.getIntakeMax()+sport.getEnergyConsumed());
                }
            }
        }
    }

    public void onAddScan(){
        Log.d("CLICK : "," SCAN");
    }

    public void onAddArticle(){
        Log.d("CLICK : "," ARTICLE");
    }

    public void onAddReceipt(){
        Log.d("CLICK : "," RECEIPT");
    }

    @Override
    public void onRemoveEdibleItem(EdibleItem item) {
        if (item.getTotalEnergy() != null) {
            IntakeProgress calorieProgress = dailyIntakes.get(TITLE_CALORIES);
            if(calorieProgress != null){
                calorieProgress.substractIntakeProgress(item.getTotalEnergy());
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

    @Override
    public void onAddEdibleItem(EdibleItem item){

    }

    @Override
    public void onRateEdibleItem(EdibleItem item){

    }

    @Override
    public void onExpandEdibleItem(EdibleItem item){

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
                IntakeProgress calorieProgress = dailyIntakes.get(TITLE_CALORIES);
                if(calorieProgress != null){
                    runOnUiThread(new Runnable() {
                        public void run() {
                            IntakeProgress calorieProgress = dailyIntakes.get(TITLE_CALORIES);
                            calorieProgress.setIntakeMax(calorieProgress.getIntakeMax() + newSport.getEnergyConsumed());
                        }
                    });
                }
            }
        } else if (request.equals(HISTORY_FOR_DATE_REQUEST)) {
            JSONArray response = (JSONArray) data.get(FOOD_LIST);
            for (int i = 0; i < response.size(); i++){
                Food f = new Food();
                f.initFromJSON((JSONObject) response.get(i));
                dailyFoods.add(f);
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
