package com.pds.app.caloriecounter;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

/**
 * Created by aurelien on 15/12/15.
 */
public class RecommendationConstraintsFragment extends Fragment {
    private OnItemClickListener listener;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_constraints_step, container, false);

        Button next = (Button) view.findViewById(R.id.constraints_res);
        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v){
                getResults();
            }
        });

        return view;
    }

    public interface OnItemClickListener {
        public void onResultsClick();
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

    public void getResults(){

        listener.onResultsClick();
    }
}
