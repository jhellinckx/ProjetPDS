package com.pds.app.caloriecounter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.pds.app.caloriecounter.dayrecording.DailyRecording;
import com.pds.app.caloriecounter.dayrecording.DayRecordingActivity;
import com.pds.app.caloriecounter.itemview.EdibleItemActionCallback;
import com.pds.app.caloriecounter.itemview.EdibleItemList;

import org.calorycounter.shared.models.EdibleItem;
import org.calorycounter.shared.models.Food;
import org.calorycounter.shared.models.Recipe;
import org.json.simple.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import static com.pds.app.caloriecounter.GraphicsConstants.ItemList.FLAG_ADDABLE;
import static com.pds.app.caloriecounter.GraphicsConstants.ItemList.FLAG_EXPANDABLE;
import static com.pds.app.caloriecounter.GraphicsConstants.ItemList.FLAG_RATABLE;
import static org.calorycounter.shared.Constants.date.SDFORMAT;
import static org.calorycounter.shared.Constants.network.CHANGE_EATEN_STATUS_REQUEST;
import static org.calorycounter.shared.Constants.network.FOOD_IMAGE_URL;
import static org.calorycounter.shared.Constants.network.FOOD_IS_EATEN;
import static org.calorycounter.shared.Constants.network.FOOD_IS_NEW;
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
import static org.calorycounter.shared.Constants.network.RECIPE_OR_FOOD;
import static org.calorycounter.shared.Constants.network.networkJSON;

public class RecommendationResultsFragment extends Fragment implements  EdibleItemActionCallback {

    private OnItemClickListener listener;
    private int id = 0;

    private ArrayList<JSONObject> recommendations;
    private LinearLayout recomList;
    private LinearLayout stickersLayout;
    private DailyRecording foodsContainer;
    private String current_date;
    private Boolean isReceipt;
    private FragmentActivity myContext;

    private void initRecommendationList(View v, Activity a){
        recommendations =((RecommendationActivity) getActivity()).recommendationsResults();
        recomList = (LinearLayout) v.findViewById(R.id.resultsview);
        if(recommendations.isEmpty()){
            Log.d("Recommendations : ", "-----------------------------empty");
        }
        else{
            Log.d("Recommendations: ", recommendations.toString());
        }
        for(JSONObject recommendation : recommendations){
            String url = (String)recommendation.get(FOOD_IMAGE_URL);
            String productName = (String)recommendation.get(FOOD_NAME);
            if(!url.isEmpty() && !productName.isEmpty()) {
                ItemSticker sticker = new ItemSticker(getContext(), url, productName);
                sticker.setId(id);
                sticker.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        showInfoFragment(v.getId());
                    }
                });
                recomList.addView(sticker);
                id++;
            }
        }

    }
    private void showInfoFragment(int pos){
        JSONObject info = recommendations.get(pos);
        Bundle b = new Bundle();
        b.putString(FOOD_NAME, (String) info.get(FOOD_NAME));
        b.putString(FOOD_QUANTITY, (String) info.get(FOOD_QUANTITY));
        b.putDouble(FOOD_TOTAL_ENERGY, (double) info.get(FOOD_TOTAL_ENERGY));
        b.putDouble(FOOD_TOTAL_PROTEINS, (double) info.get(FOOD_TOTAL_PROTEINS));
        b.putDouble(FOOD_TOTAL_FAT, (double) info.get(FOOD_TOTAL_FAT));
        b.putDouble(FOOD_TOTAL_CARBOHYDRATES, (double) info.get(FOOD_TOTAL_CARBOHYDRATES));
        b.putDouble(FOOD_TOTAL_SATURATED_FAT, (double) info.get(FOOD_TOTAL_SATURATED_FAT));
        b.putDouble(FOOD_TOTAL_SODIUM, (double) info.get(FOOD_TOTAL_SODIUM));
        b.putDouble(FOOD_TOTAL_SUGARS, (double) info.get(FOOD_TOTAL_SUGARS));
        ItemInfoDialog frag = new ItemInfoDialog();
        frag.setArguments(b);
        frag.show(getActivity().getFragmentManager(), "item info");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Log.d("ON CREATE VIEW", "CREATING RES FRAG");
        View view = inflater.inflate(R.layout.activity_day_recording, container, false);
        stickersLayout = (LinearLayout) view.findViewById(R.id.day_recording_layout);
        stickersLayout.setOrientation(LinearLayout.VERTICAL);
        Bundle b = this.getArguments();
        current_date = b.getString("date");
        isReceipt = b.getBoolean("isReceipt");
        recommendations =((RecommendationActivity) getActivity()).recommendationsResults();
        foodsContainer = new DailyRecording(getContext(), "Résultats", new EdibleItemList(getContext(), changeJSONtoEdibleItems(recommendations), this, FLAG_RATABLE, FLAG_EXPANDABLE, FLAG_ADDABLE));
        stickersLayout.addView(foodsContainer);


        return view;
    }

    public interface OnItemClickListener {
        public void restart();
    }

    private List<EdibleItem> changeJSONtoEdibleItems(List<JSONObject> jsons){
        List<EdibleItem> foods = new ArrayList<>();
        if(isReceipt){
            for (JSONObject js : jsons) {
                Recipe r = new Recipe();
                r.initFromJSON(js);
                foods.add(r);
            }
        }else {
            for (JSONObject js : jsons) {
                Food f = new Food();
                f.initFromJSON(js);
                foods.add(f);
            }
        }
        return foods;
    }

    @Override
    public void onAttach(Context context) {
        myContext=(FragmentActivity) context;
        super.onAttach(context);
        if (context instanceof OnItemClickListener) {
            listener = (OnItemClickListener) context;
        } else {
            throw new ClassCastException(context.toString() + "not instance of this.OnItemClickListener");
        }
    }

    @Override
    public void onDetach(){
        super.onDetach();
        listener = null;
    }

    public void restartProcess(){

        listener.restart();
    }

    @Override
    public void onAddEdibleItem(EdibleItem item){
        Intent dayRecordingActivity = new Intent(getActivity(), DayRecordingActivity.class);

        //add la food a l'history
        String current_day = SDFORMAT.format(Calendar.getInstance().getTime());
        JSONObject data = new JSONObject();
        data.put(FOOD_NAME, item.toJSON(false));
        data.put(HISTORY_DATE, current_date);
        data.put(FOOD_IS_EATEN, 0);
        data.put(FOOD_IS_NEW,1);
        if(item instanceof Food) {
            data.put(RECIPE_OR_FOOD, "food");
        }else{
            data.put(RECIPE_OR_FOOD, "recipe");
        }
        RecommendationActivity ra = (RecommendationActivity) getActivity();
        ra.send(networkJSON(CHANGE_EATEN_STATUS_REQUEST, data));
        Bundle b = new Bundle();
        b.putString("day", current_date);
        dayRecordingActivity.putExtras(b);
        getActivity().startActivity(dayRecordingActivity);
    }

    @Override
    public void onRateEdibleItem(EdibleItem item){
        RateFoodDialogFragment frag = new RateFoodDialogFragment();
        Bundle bundle = new Bundle();
        bundle.putLong("id", item.getId());
        bundle.putString("name", item.getProductName());
        frag.setArguments(bundle);
        frag.show(myContext.getFragmentManager(), "titletest");
    }


    @Override
    public void onExpandEdibleItem(EdibleItem item){

        Bundle b = new Bundle();
        b.putString(FOOD_NAME, item.getProductName());

        b.putFloat(FOOD_TOTAL_ENERGY, item.getTotalEnergy());
        b.putFloat(FOOD_TOTAL_FAT, item.getTotalFat());
        b.putFloat(FOOD_TOTAL_PROTEINS, item.getTotalProteins());
        b.putFloat(FOOD_TOTAL_CARBOHYDRATES, item.getTotalCarbohydrates());
        if(item instanceof Food){
            b.putString(FOOD_QUANTITY, item.getQuantity());
            b.putFloat(FOOD_TOTAL_SUGARS, item.getTotalSugars());
            b.putFloat(FOOD_TOTAL_SODIUM, item.getTotalSalt());
            b.putFloat(FOOD_TOTAL_SATURATED_FAT, item.getTotalSaturatedFat());
            b.putString(RECIPE_OR_FOOD, "food");
        }else{
            b.putString(RECIPE_OR_FOOD, "recipe");
        }
        ItemInfoDialog dialog = new ItemInfoDialog();
        dialog.setArguments(b);
        dialog.show(myContext.getFragmentManager(), "infos");


    }

    @Override
    public void onCheckEdibleItem(EdibleItem item){

    }

    @Override
    public void onRemoveEdibleItem(EdibleItem item){

    }
}
