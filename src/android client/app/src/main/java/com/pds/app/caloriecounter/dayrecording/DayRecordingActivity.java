package com.pds.app.caloriecounter.dayrecording;

import android.os.Bundle;
import android.widget.LinearLayout;

import com.pds.app.caloriecounter.MenuNavigableActivity;
import com.pds.app.caloriecounter.R;
import com.pds.app.caloriecounter.itemview.EdibleItemSticker;
import com.pds.app.caloriecounter.utils.EvenSpaceView;

import org.calorycounter.shared.models.EdibleItem;
import org.calorycounter.shared.models.Food;
import org.json.simple.JSONObject;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import static com.pds.app.caloriecounter.GraphicsConstants.Global.*;

public class DayRecordingActivity extends MenuNavigableActivity {

    private LinearLayout stickersLayout;
    private Map<String, IntakeProgress> dailyIntakes;
    private List<Food> dailyFoods;

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

        EdibleItem foodWithNutrInfos = new Food();
        foodWithNutrInfos.setImageUrl("https://colruyt.collectandgo.be/cogo/step/JPG/JPG/500x500/std.lang.all/41/55/asset-834155.jpg");
        foodWithNutrInfos.setProductName("nappage au chocolat 290 ml slt sa vaa a" +
                "");
        foodWithNutrInfos.setTotalCarbohydrates(119.4f);
        foodWithNutrInfos.setTotalEnergy(8100f);
        foodWithNutrInfos.setTotalProteins(31f);
        EdibleItemSticker card1 = new EdibleItemSticker(this, foodWithNutrInfos);

        EdibleItem foodWithoutNutrInfos = new Food();
        foodWithoutNutrInfos.setProductName("Kellogg's Frosties 600g");
        foodWithoutNutrInfos.setImageUrl("https://fic.colruytgroup.com/productinfo/step/JPG/JPG/320x320/std.lang.all/14/17/asset-741417.jpg");
        EdibleItemSticker card2 = new EdibleItemSticker(this, foodWithoutNutrInfos);

        /*SwipeMenuListView listView = new SwipeMenuListView(getApplicationContext());
        SwipeMenuCreator creator = new SwipeMenuCreator() {
            @Override
            public void create(SwipeMenu menu) {
                // create "delete" item
                SwipeMenuItem deleteItem = new SwipeMenuItem(
                        getApplicationContext());
                // set item background
                deleteItem.setBackground(new ColorDrawable(Color.rgb(0xF9,
                        0x3F, 0x25)));
                // set item width
                deleteItem.setWidth(Converter.dp(getApplicationContext(), 90));
                // set a icon
                deleteItem.setIcon(R.drawable.ic_delete_red_a700_18dp);
                // add to menu
                menu.addMenuItem(deleteItem);
            }
        };
        listView.setMenuCreator(creator);

        listView.setOnMenuItemClickListener(new SwipeMenuListView.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(int position, SwipeMenu mednu, int index) {
                switch (index) {
                    case 0:
                        // delete
                        break;
                    case 1:
                        // empty here
                        break;
                }
                // false : close the menu; true : not close the menu
                return false;
            }
        });
        listView.setSwipeDirection(SwipeMenuListView.DIRECTION_RIGHT);*/
        LinearLayout cardLayout = new LinearLayout(this);
        cardLayout.setOrientation(LinearLayout.VERTICAL);
        cardLayout.addView(card1);
        cardLayout.addView(card2);

        RecordingContainer foodsContainer = new RecordingContainer(this, TITLE_FOODS, cardLayout);

        stickersLayout.addView(foodsContainer);
    }

    public void requestDailyFoods(){

    }

    public void handleMessage(JSONObject msg){

    }



}
