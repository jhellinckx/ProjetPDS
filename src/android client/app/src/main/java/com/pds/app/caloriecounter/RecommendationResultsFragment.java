package com.pds.app.caloriecounter;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.view.ViewGroup.LayoutParams;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import org.json.simple.JSONObject;

import java.util.ArrayList;

import static org.calorycounter.shared.Constants.network.*;
/**
 * Created by aurelien on 15/12/15.
 */
public class RecommendationResultsFragment extends Fragment {

    private OnItemClickListener listener;

    private ArrayList<JSONObject> recommendations;
    private TableLayout recomTable;

    private void initRecommendationList(View v, Activity a){
        recommendations =((RecommendationActivity) getActivity()).recommendationsResults();
        recomTable = (TableLayout) v.findViewById(R.id.resultsview);
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
                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT, 1.0f);
                TableRow recomRow = new TableRow((getActivity()));
                ImageView imageView = new ImageView(getActivity());
                Picasso.with(getActivity())
                        .load(url)
                        .resize(230, 230)
                        .transform(new RoundedTransformation(100, 0))
                        .into(imageView);
                TextView name = new TextView(getActivity());
                name.setText(productName);

                /* Add views to the row then add row to table */
                recomRow.addView(imageView);
                recomRow.addView(name);
                recomTable.addView(recomRow);
            }
        }

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
