package com.pds.app.caloriecounter;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;

import com.squareup.picasso.Picasso;

import java.io.File;
import java.util.ArrayList;

/**
 * Created by aurelien on 15/12/15.
 */


/*
    This fragment handles the previous meals of the users.
*/

public class RecommendationPastFragment extends Fragment {

    private OnItemClickListener listener;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_past_step2, container, false);

        Button next = (Button) view.findViewById(R.id.past_next);
        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                nextStep();
            }
        });


        ArrayList<String> tests = new ArrayList<String>();
        tests.add("NAMEFOOD1");
        tests.add("NAMEFOOD2");
        tests.add("NAMEFOOD3");
        tests.add("NAMEFOOD4");

        ListView lv = (ListView) view.findViewById(R.id.listView) ;
        View footerView = inflater.inflate(R.layout.fragment_past_footer, lv, false);
        lv.addFooterView(footerView);
        ImageView imageView = (ImageView) footerView.findViewById(R.id.imageView);
        /*
        Picasso.with(view.getContext())
                //.load("File://///res/drawable/plus.jpg")
                .load("/home/end3rs/Musique/Bureau/ProjetPDS/src/android client/app/src/main/res/drawable")
                .resize(330,330)
                .transform(new RoundedTransformation(100, 0))
                .into(imageView);
                */
        lv.setAdapter(new RecommendationPastFragment_listAdapter(view.getContext(), tests));
        return view;
    }

    public interface OnItemClickListener {
        public void onNextPastClick();
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

        listener.onNextPastClick();
    }
}