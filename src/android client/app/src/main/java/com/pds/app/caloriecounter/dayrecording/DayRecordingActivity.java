package com.pds.app.caloriecounter.dayrecording;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.view.Gravity;
import android.widget.LinearLayout;
import android.widget.TextView;

import de.hdodenhof.circleimageview.CircleImageView;

import com.pds.app.caloriecounter.MenuNavigableActivity;
import com.pds.app.caloriecounter.R;
import com.pds.app.caloriecounter.utils.Converter;
import com.pds.app.caloriecounter.utils.EvenSpaceView;
import com.squareup.picasso.Picasso;

import org.calorycounter.shared.models.Food;
import org.json.simple.JSONObject;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import static com.pds.app.caloriecounter.dayrecording.GraphicsConstants.Global.*;

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

        CardView card = new CardView(this);

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,LinearLayout.LayoutParams.WRAP_CONTENT);
        card.setLayoutParams(params);

        LinearLayout inCard = new LinearLayout(this);
        inCard.setOrientation(LinearLayout.HORIZONTAL);
        inCard.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,LinearLayout.LayoutParams.WRAP_CONTENT));
        inCard.setPadding(Converter.dp(this, 16), Converter.dp(this, 16), Converter.dp(this, 16), Converter.dp(this, 16));

        CircleImageView menuImage = new CircleImageView(this);
        //ImageView menuImage = new ImageView(this);
        Picasso.with(this)
                .load("https://fic.colruytgroup.com/productinfo/step/JPG/JPG/320x320/std.lang.all/41/54/asset-834154.jpg")
                .into(menuImage);
        LinearLayout.LayoutParams imageParams = new LinearLayout.LayoutParams(Converter.dp(this, 60), Converter.dp(this, 60));
        imageParams.setMargins(0, 0, Converter.dp(this, 16), 0);
        menuImage.setLayoutParams(imageParams);
        //menuImage.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
        menuImage.setBorderColor(getResources().getColor(R.color.smalt));
        menuImage.setBorderWidth(Converter.dp(this, 1));



        LinearLayout textCont = new LinearLayout(this);
        LinearLayout.LayoutParams textContParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
        textCont.setLayoutParams(textContParams);
        textCont.setOrientation(LinearLayout.VERTICAL);
        textCont.setGravity(Gravity.CENTER_VERTICAL);

        TextView mainText = new TextView(this);
        LinearLayout.LayoutParams mainTextParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        mainText.setLayoutParams(mainTextParams);
        mainText.setTextSize(Converter.sp(this, 10));
        mainText.setText("Primary");
        mainText.setTextColor(Color.GRAY);
        //mainText.setGravity(Gravity.CENTER_VERTICAL);
        //mainText.setGravity(Gravity.LEFT);

        TextView secondaryText = new TextView(this);
        LinearLayout.LayoutParams secondaryTextParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        secondaryText.setLayoutParams(secondaryTextParams);
        secondaryText.setTextSize(Converter.sp(this, 8));
        secondaryText.setText("Secondary");
        //secondaryText.setGravity(Gravity.CENTER_VERTICAL);
        secondaryText.setTextColor(Color.LTGRAY);

        textCont.addView(mainText);
        textCont.addView(secondaryText);
        //textCont.addView(textLayout);

        inCard.addView(menuImage);
        inCard.addView(textCont);
        card.addView(inCard);


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


        RecordingContainer foodsContainer = new RecordingContainer(this, TITLE_FOODS, card);
        stickersLayout.addView(foodsContainer);
    }

    public void requestDailyFoods(){

    }

    public void handleMessage(JSONObject msg){

    }



}
