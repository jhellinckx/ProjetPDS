package com.pds.app.caloriecounter;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.view.ViewGroup.LayoutParams;

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

        JSONObject testObject = null;
        if(! recommendations.isEmpty()) {
            testObject = recommendations.get(0);
        }
        else{
            Log.d("ERROR", " EMPTY RECOMMENDATIONS !!!!");
        }
        if(testObject != null) {
            TableRow testRow = new TableRow(getActivity());
            LayoutParams params = new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT);
            testRow.setLayoutParams(params);
            ImageView imageView = new ImageView(getActivity());
            Picasso.with(getActivity())
                    .load((String)testObject.get(FOOD_IMAGE_URL))
                    .resize(330, 330)
                    .transform(new RoundedTransformation(100, 0))
                    .into(imageView);
            testRow.addView(imageView);
            recomTable.addView(testRow);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Log.d("ON CREATE VIEW", "CREATING RES FRAG");
        View view = inflater.inflate(R.layout.fragment_recom_results, container, false);
        Activity activity = getActivity();
        initRecommendationList(view, activity);
        Button next = (Button) view.findViewById(R.id.restart_process);
        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v){
                restartProcess();
            }
        });

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
