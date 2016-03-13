package com.pds.app.caloriecounter;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.RatingBar;
import android.widget.TextView;

import static org.calorycounter.shared.Constants.network.*;

/**
 * Created by aurelien on 25/02/16.
 */
public class ItemInfoDialog extends DialogFragment {

    private static final String PORTION = " Personnes";

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState){
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        final LayoutInflater layoutInflater = (LayoutInflater) getActivity()
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        Bundle b = getArguments();

        View view = layoutInflater.inflate(R.layout.info_dialog, null);
        builder.setView(view);
        initTextViews(b, view);

        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener(){
            @Override
            public void onClick(DialogInterface dialog, int which){
            }

        });

        Dialog dialog = builder.create();
        return dialog;

    }

    private void initTextView(TextView tv, float num_value){
        tv.setText(Float.toString(num_value));
    }

    private void initMinorTextViewForRecipe(Bundle b, View v){
        ((TextView) v.findViewById(R.id.info_quantity)).setText(b.getString(FOOD_QUANTITY) + PORTION);
    }

    private void initMinorTextView(Bundle b, View v){
        ((TextView) v.findViewById(R.id.info_quantity)).setText(b.getString(FOOD_QUANTITY));
    }

    private void initMainTextView(Bundle b, View v){
        ((TextView) v.findViewById(R.id.info_name)).setText(b.getString(FOOD_NAME));
        int energy = (int) (float) ((b.getFloat(FOOD_TOTAL_ENERGY))/CAL_TO_JOULE_FACTOR);
        initTextView((TextView) v.findViewById(R.id.info_cal), energy);
        initTextView((TextView) v.findViewById(R.id.info_prot), b.getFloat(FOOD_TOTAL_PROTEINS));
        initTextView((TextView) v.findViewById(R.id.info_fat), b.getFloat(FOOD_TOTAL_FAT));
        initTextView((TextView) v.findViewById(R.id.info_carbo), b.getFloat(FOOD_TOTAL_CARBOHYDRATES));

    }

    private void initTextViews(Bundle b, View v){
        initMainTextView(b, v);
        if(b.getString(RECIPE_OR_FOOD).equals("food")) {
            initMinorTextView(b, v);
        }else{
            initMinorTextViewForRecipe(b, v);
        }
    }
}
