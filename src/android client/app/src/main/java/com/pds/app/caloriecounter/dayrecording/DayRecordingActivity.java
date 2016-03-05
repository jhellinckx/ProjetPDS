package com.pds.app.caloriecounter.dayrecording;

import android.os.Bundle;
import android.widget.LinearLayout;

import com.pds.app.caloriecounter.MenuNavigableActivity;
import com.pds.app.caloriecounter.R;
import com.pds.app.caloriecounter.utils.EvenSpaceView;

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

    }

    public void requestDailyFoods(){

    }

    public void handleMessage(JSONObject msg){

    }



}
