package com.pds.app.caloriecounter;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

/**
 * Created by aurelien on 15/12/15.
 */
public class RecommendationResultsFragment extends Fragment {

    private OnItemClickListener listener;

    private String[] recommendations;
    private ListView recomList;

    private void initRecommendationList(View v, Activity a){

        recommendations = getResources().getStringArray(R.array.resultsplaceholder);
        recomList = (ListView) v.findViewById(R.id.resultsview);

        recomList.setAdapter(new ArrayAdapter<String>(a, android.R.layout.simple_list_item_1, recommendations));

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

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
