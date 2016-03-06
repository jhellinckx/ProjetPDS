package com.pds.app.caloriecounter.dayrecording;

import android.app.ActionBar;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutCompat;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
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
import org.json.simple.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import static com.pds.app.caloriecounter.GraphicsConstants.Global.*;
import static com.pds.app.caloriecounter.GraphicsConstants.ItemList.*;
import static com.pds.app.caloriecounter.GraphicsConstants.ItemSticker.*;

public class DayRecordingActivity extends MenuNavigableActivity implements EdibleItemActionCallback, SportActionCallback {

    private LinearLayout stickersLayout;
    private Map<String, IntakeProgress> dailyIntakes;
    private List<EdibleItem> dailyFoods;
    private List<Sport> dailySports;
    private DailyRecording sportsContainer;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        v = getLayoutInflater().inflate(R.layout.activity_day_recording,frameLayout);
        stickersLayout = (LinearLayout) v.findViewById(R.id.day_recording_layout);
        initIntakesRecording();
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
        dailyFoods = new ArrayList<>();

        /* EdibleItem placeholders */
        EdibleItem item1 = new Food();
        item1.setImageUrl("https://colruyt.collectandgo.be/cogo/step/JPG/JPG/500x500/std.lang.all/41/55/asset-834155.jpg");
        item1.setProductName("nappage au chocolat 290 ml test longueur");
        item1.setTotalCarbohydrates(119.4f);
        item1.setTotalEnergy(1000f);
        item1.setTotalProteins(31f);
        item1.setId(1000L);

        EdibleItem item2 = new Food();
        item2.setProductName("Kellogg's Frosties 600g");
        item2.setImageUrl("https://fic.colruytgroup.com/productinfo/step/JPG/JPG/320x320/std.lang.all/14/17/asset-741417.jpg");
        item2.setTotalCarbohydrates(20.4f);
        item2.setTotalEnergy(400f);
        item2.setTotalProteins(2f);
        item2.setId(1001L);

        EdibleItem item3 = new Food();
        item3.setImageUrl("https://fic.colruytgroup.com/productinfo/step/JPG/JPG/320x320/std.lang.all/69/05/asset-396905.jpg");
        item3.setProductName("Aoste Stickado - Classique XL");
        item3.setTotalProteins(5f);
        item3.setTotalEnergy(1000f);
        item3.setId(1002L);

        dailyFoods.add(item1);
        dailyFoods.add(item2);
        dailyFoods.add(item3);

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

        dailySports = new ArrayList<Sport>();

        Sport test = new Sport(1L,"Basket",120,2300f);

        dailySports.add(test);
        sportsContainer = new DailyRecording(this, TITLE_SPORTS, new SportList(this, dailySports, this, FLAG_REMOVABLE));

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

    }

    @Override
    public void onRemoveSport(Sport sport){
        if (sport.getEnergyConsumed() != null) {
            IntakeProgress calorieProgress = dailyIntakes.get(TITLE_CALORIES);
            if(calorieProgress != null){
                calorieProgress.setIntakeMax(calorieProgress.getIntakeMax() - sport.getEnergyConsumed());
            }
        }
    }

    @Override
    public void onAddSport(String sportName, int duration){
        Sport newSport = new Sport(sportName, duration, 1400f);
        dailySports.add(newSport);
        stickersLayout.removeView((sportsContainer));
        sportsContainer = new DailyRecording(this, TITLE_SPORTS, new SportList(this, dailySports, this, FLAG_REMOVABLE));
        stickersLayout.addView(sportsContainer);
        if (newSport.getEnergyConsumed() != null){
            IntakeProgress calorieProgress = dailyIntakes.get(TITLE_CALORIES);
            if(calorieProgress != null){
                calorieProgress.setIntakeMax(calorieProgress.getIntakeMax()+newSport.getEnergyConsumed());
            }
        }

    }


}
