package com.pds.app.caloriecounter;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.List;

public class DayFragment extends Fragment {

    private BackListener listener;
    private LinearLayout food_layout;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_day, container, false);
        Bundle b = getArguments();
        EditText date = (EditText) view.findViewById(R.id.date_field);
        food_layout = (LinearLayout) view.findViewById(R.id.past_meals);
        date.setText(b.getString("day"));
        addFoodsToLayout(b.getStringArrayList("names"));
        return view;
    }

    private void addFoodsToLayout(List<String> foodnames){
        for (String foodname : foodnames){
            food_layout.addView(new ItemSticker(getContext(), new String(), foodname));
        }

    }

    public interface BackListener{
        public void switchCalendarAndFrameVisibility();

    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof BackListener) {
            listener = (BackListener) context;
        } else {
            throw new ClassCastException(context.toString() + "not instance of this.OnItemClickListener");
        }
    }

    @Override
    public void onDestroy(){
        listener.switchCalendarAndFrameVisibility();
        super.onDestroy();
    }
}
