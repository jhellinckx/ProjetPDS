package com.pds.app.caloriecounter.dayrecording;

import android.os.Bundle;
import android.widget.LinearLayout;

import com.pds.app.caloriecounter.MenuNavigableActivity;
import com.pds.app.caloriecounter.R;
import com.pds.app.caloriecounter.itemview.EdibleItemActionCallback;
import com.pds.app.caloriecounter.itemview.EdibleItemList;
import com.pds.app.caloriecounter.utils.EvenSpaceView;

import org.calorycounter.shared.models.EdibleItem;
import org.calorycounter.shared.models.Food;
import org.json.simple.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import static com.pds.app.caloriecounter.GraphicsConstants.Global.*;
import static com.pds.app.caloriecounter.GraphicsConstants.ItemList.*;

public class DayRecordingActivity extends MenuNavigableActivity implements EdibleItemActionCallback {

    private LinearLayout stickersLayout;
    private Map<String, IntakeProgress> dailyIntakes;
    private List<Food> dailyFoods;

    private RecordingContainer foodsContainer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        v = getLayoutInflater().inflate(R.layout.activity_day_recording,frameLayout);
        stickersLayout = (LinearLayout) v.findViewById(R.id.day_recording_layout);
        initIntakesRecordingContainer();
        initFoodsRecordingContainer();
    }

    public void initIntakesRecordingContainer(){
        LinearLayout intakesStickersLayout = new LinearLayout(this);
        intakesStickersLayout.setOrientation(LinearLayout.HORIZONTAL);
        dailyIntakes = new LinkedHashMap<>();

        /* Intake placeholders */
        IntakeProgress calorieIntake = new IntakeProgress(this, 250, 2000, CALORIES_UNIT);
        dailyIntakes.put(TITLE_CALORIES, calorieIntake);
        IntakeProgress proteinIntake = new IntakeProgress(this, 3.5f, 40, DEFAULT_UNIT);
        dailyIntakes.put(TITLE_PROTEINS, proteinIntake);
        IntakeProgress carboIntake = new IntakeProgress(this, 150, 300, DEFAULT_UNIT);
        dailyIntakes.put(TITLE_CARBO, carboIntake);

        for(String key : dailyIntakes.keySet()){
            intakesStickersLayout.addView(new EvenSpaceView(this));
            intakesStickersLayout.addView(new IntakeProgressSticker(this, key, dailyIntakes.get(key)));
        }
        intakesStickersLayout.addView(new EvenSpaceView(this));

        RecordingContainer intakesContainer = new RecordingContainer(this, TITLE_RDI, intakesStickersLayout);
        stickersLayout.addView(intakesContainer);
    }

    public void initFoodsRecordingContainer(){
        dailyFoods = new ArrayList<>();

        /* EdibleItem placeholders */
        EdibleItem item1 = new Food();
        item1.setImageUrl("https://colruyt.collectandgo.be/cogo/step/JPG/JPG/500x500/std.lang.all/41/55/asset-834155.jpg");
        item1.setProductName("nappage au chocolat 290 ml test longueur");
        item1.setTotalCarbohydrates(119.4f);
        item1.setTotalEnergy(8100f);
        item1.setTotalProteins(31f);
        item1.setId(1000L);

        EdibleItem item2 = new Food();
        item2.setProductName("Kellogg's Frosties 600g");
        item2.setImageUrl("https://fic.colruytgroup.com/productinfo/step/JPG/JPG/320x320/std.lang.all/14/17/asset-741417.jpg");
        item2.setId(1001L);

        EdibleItem item3 = new Food();
        item3.setImageUrl("https://fic.colruytgroup.com/productinfo/step/JPG/JPG/320x320/std.lang.all/69/05/asset-396905.jpg");
        item3.setProductName("Aoste Stickado - Classique XL");
        item3.setTotalProteins(5f);
        item3.setTotalEnergy(1000f);
        item3.setId(1002L);

        EdibleItem[] itemsArray = {item1, item2, item3};
        List<EdibleItem> items = Arrays.asList(itemsArray);
        foodsContainer = new RecordingContainer(this, TITLE_FOODS, new EdibleItemList(this, items, this, FLAG_REMOVABLE, FLAG_ADDABLE));
        stickersLayout.addView(foodsContainer);
    }

    @Override
    public void onRemoveEdibleItem(EdibleItem item){

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



    public void handleMessage(JSONObject msg){

    }



}
