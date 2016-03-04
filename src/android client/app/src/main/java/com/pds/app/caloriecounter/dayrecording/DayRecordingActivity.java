package com.pds.app.caloriecounter.dayrecording;

import android.content.Context;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.View;
import android.widget.LinearLayout;

import com.pds.app.caloriecounter.MenuNavigableActivity;
import com.pds.app.caloriecounter.R;

import org.calorycounter.shared.models.Food;
import org.json.simple.JSONObject;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class DayRecordingActivity extends MenuNavigableActivity {
    private class SpaceView extends View{ // Needed to horizontally fill gaps between other views
        public SpaceView(Context context){
            super(context);
            int width = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 0, getResources().getDisplayMetrics());
            int height = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 1, getResources().getDisplayMetrics());
            this.setLayoutParams(new LinearLayout.LayoutParams(width, height, 1));
        }
    }

    private static String TITLE_CALORIES = "Calories";
    private static String TITLE_PROTEINS = "Protéines";
    private static String TITLE_CARBO = "Glucides";
    private static String TITLE_SALT = "Sels";
    private static String TITLE_FAT = "Lipides";

    private static String CALORIES_UNIT = "kcal";
    private static String DEFAULT_UNIT = "g";

    private LinearLayout topLayout;
    private Map<String, IntakeProgressContainer> dailyIntakes;
    private List<Food> dailyFoods;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        v = getLayoutInflater().inflate(R.layout.activity_day_recording,frameLayout);



        topLayout = (LinearLayout) v.findViewById(R.id.day_recording_layout);

        setIntakesContainer();
        setFoodsContainer();

    }

    public void setIntakesContainer(){
        // Add horizontal layout to draw the daily intakes
        LinearLayout intakesLayoutWrapper = new LinearLayout(this);

        intakesLayoutWrapper.setOrientation(LinearLayout.HORIZONTAL);

        dailyIntakes = new LinkedHashMap<>();

        IntakeProgressContainer calorieIntake = new IntakeProgressContainer(this, TITLE_CALORIES, 250, 2000, CALORIES_UNIT);
        dailyIntakes.put(TITLE_CALORIES, calorieIntake);

        IntakeProgressContainer proteinIntake = new IntakeProgressContainer(this, TITLE_PROTEINS, 3.5f, 40, DEFAULT_UNIT);
        dailyIntakes.put(TITLE_PROTEINS, proteinIntake);

        IntakeProgressContainer carboIntake = new IntakeProgressContainer(this, TITLE_CARBO, 150, 300, DEFAULT_UNIT);
        dailyIntakes.put(TITLE_CARBO, carboIntake);


        for(String key : dailyIntakes.keySet()){
            intakesLayoutWrapper.addView(new SpaceView(this));
            intakesLayoutWrapper.addView(dailyIntakes.get(key));
        }
        intakesLayoutWrapper.addView(new SpaceView(this));

        DayRecordingContainer intakesContainer = new DayRecordingContainer(this, "APPORTS JOURNALIERS", intakesLayoutWrapper);
        topLayout.addView(intakesContainer);
    }

    public void setFoodsContainer(){
        dailyFoods = new ArrayList<>();

    }

    public void requestDailyFoods(){

    }

    public void handleMessage(JSONObject msg){

    }



}
