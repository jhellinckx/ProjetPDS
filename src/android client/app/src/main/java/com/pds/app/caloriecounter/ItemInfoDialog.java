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

import com.squareup.picasso.Picasso;

/**
 * Created by aurelien on 25/02/16.
 */
public class ItemInfoDialog extends DialogFragment {

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState){
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        final LayoutInflater layoutInflater = (LayoutInflater) getActivity()
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        Bundle b = getArguments();
        String url = b.getString("url");
        String name = b.getString("name");

        View view = layoutInflater.inflate(R.layout.rating_dialog,null);
        builder.setView(view);
        TextView text = ((TextView) view.findViewById(R.id.rateTitle));
        text.setText((String) text.getText() + ": " + name);

        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener(){
            @Override
            public void onClick(DialogInterface dialog, int which){
            }

        });

        Dialog dialog = builder.create();
        return dialog;

    }
}
