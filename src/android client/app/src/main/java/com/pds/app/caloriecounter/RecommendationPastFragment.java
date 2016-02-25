package com.pds.app.caloriecounter;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;

import android.widget.ListView;
import android.widget.Toast;

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import java.util.ArrayList;
import java.util.List;


/*
    This fragment handles the previous meals of the users.
*/

public class RecommendationPastFragment extends Fragment {

    private OnItemClickListener listener;
    private ArrayList<String> _foodNames;
    private ArrayAdapter<String> _adapter;
    private View _view;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Bundle bundle = this.getArguments();

        _view = inflater.inflate(R.layout.fragment_past_step, container, false);

        Button next = (Button) _view.findViewById(R.id.past_next);
        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                nextStep();
            }
        });

        _foodNames = new ArrayList<String>();
        _foodNames = bundle.getStringArrayList("productNames");

        ListView lv = (ListView) _view.findViewById(R.id.listView) ;
        View footerView = inflater.inflate(R.layout.fragment_past_footer, lv, false);
        lv.addFooterView(footerView);
        Button btn = (Button) footerView.findViewById(R.id.button);
        _adapter = new ArrayAdapter<String>(getActivity(), R.layout.fragment_past_element_list, R.id.textView_test, _foodNames);
        addListenerFooterListView(footerView);
        addListenerFooterListView(btn);
        lv.setAdapter(_adapter);
        return _view;
    }

    public void addListenerFooterListView(View v){
        v.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startScan();
            }
        });
    }

    public void startScan(){
        String scanContent = "3038350013002";
        ((RecommendationActivity)getActivity()).sendCode(scanContent);
        //IntentIntegrator scanIntegrator = new IntentIntegrator(getActivity());
        //scanIntegrator.initiateScan();
    }

    public interface OnItemClickListener {
        public void onNextPastClick(List<String> pastFoods);
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

        listener.onNextPastClick(_foodNames);
    }
}