package com.pds.app.caloriecounter;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import org.json.simple.JSONObject;

import java.util.ArrayList;

import static org.calorycounter.shared.Constants.network.*;

public class RecommendationResultsFragment extends Fragment {

    private OnItemClickListener listener;
    private int id = 0;

    private ArrayList<JSONObject> recommendations;
    private LinearLayout recomList;

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
        View view = inflater.inflate(R.layout.fragment_recom_results, container, false);
        Activity activity = getActivity();
        initRecommendationList(view, activity);

        return view;
    }

    public interface OnItemClickListener {
        public void restart();
    }

    @Override
    public void onAttach(Context context) {
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

}
