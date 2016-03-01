package com.pds.app.caloriecounter;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

/**
 * Created by aurelien on 1/03/16.
 */
public class DayFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_day, container, false);
        Bundle b = getArguments();
        EditText date = (EditText) view.findViewById(R.id.date_field);
        date.setText(b.getString("day"));
        return view;
    }
}
