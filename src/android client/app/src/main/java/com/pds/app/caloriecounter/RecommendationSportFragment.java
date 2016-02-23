package com.pds.app.caloriecounter;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;

/**
 * Created by aurelien on 15/12/15.
 */
public class RecommendationSportFragment extends Fragment {

    private OnItemClickListener listener;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        Bundle bundle = this.getArguments();
        System.out.println(bundle.getStringArrayList("names"));

        View view = inflater.inflate(R.layout.fragment_sport_step, container, false);

        Button next = (Button) view.findViewById(R.id.sport_next);
        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v){
                nextStep();
            }
        });
        Spinner sports = (Spinner) view.findViewById(R.id.recom_sport);

        ArrayAdapter<String> ageAdapter = new ArrayAdapter<String>(getActivity() ,android.R.layout.simple_spinner_item, bundle.getStringArrayList("names"));
        ageAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        sports.setAdapter(ageAdapter);

        return view;
    }

    public interface OnItemClickListener {
        public void onNextSportClick();
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

    public void nextStep(){

        listener.onNextSportClick();
    }

}