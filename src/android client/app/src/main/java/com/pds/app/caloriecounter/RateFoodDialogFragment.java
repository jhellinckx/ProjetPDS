package com.pds.app.caloriecounter;


import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

public class RateFoodDialogFragment extends DialogFragment {

    private long _id;

    // This interface allows event callbacks to RatingActivity.

    public interface RateFoodDialogListener{
        public void onDialogPositiveClick(DialogFragment dialog, long id, float rating);
        public void onDialogNegativeClick(DialogFragment dialog);
    }

    RateFoodDialogListener _listener;

    @Override
    public void onAttach(Activity activity){
        super.onAttach(activity);
        try{
            _listener = (RateFoodDialogListener) activity;
        } catch (ClassCastException e){
            throw new ClassCastException(activity.toString() +
                    " must implement RateFoodDialogListener");
        }
    }


    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState){
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        final LayoutInflater layoutInflater = (LayoutInflater) getActivity()
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        Bundle b = getArguments();
        String name = b.getString("name");

        View view = layoutInflater.inflate(R.layout.rating_dialog,null);
        builder.setView(view);
        ImageView imageView = (ImageView) view.findViewById(R.id.imageView);
        TextView text = ((TextView) view.findViewById(R.id.rateTitle));
        text.setText((String) text.getText() + ": " + name);

        builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                RatingBar ratingBar = (RatingBar) ((AlertDialog) dialog).findViewById(R.id.ratingBar);
                _listener.onDialogPositiveClick(RateFoodDialogFragment.this, _id, ratingBar.getRating());
            }
        });

        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener(){
            @Override
            public void onClick(DialogInterface dialog, int which){
                _listener.onDialogNegativeClick(RateFoodDialogFragment.this);
            }

        });

        _id = b.getLong("id");

        Dialog dialog = builder.create();
        return dialog;

    }

}
