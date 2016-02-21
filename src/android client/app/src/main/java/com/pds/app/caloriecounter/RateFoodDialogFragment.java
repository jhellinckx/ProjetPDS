package com.pds.app.caloriecounter;


import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.widget.RatingBar;

public class RateFoodDialogFragment extends DialogFragment {

    private int _position;

    // This interface allows event callbacks to RatingActivity.

    public interface RateFoodDialogListener{
        public void onDialogPositiveClick(DialogFragment dialog, int pos, float rating);
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
        final LayoutInflater inflater = getActivity().getLayoutInflater();
        builder.setView(inflater.inflate(R.layout.rating_dialog, null));

        builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                RatingBar ratingBar = (RatingBar) ((AlertDialog) dialog).findViewById(R.id.ratingBar);
                _listener.onDialogPositiveClick(RateFoodDialogFragment.this, _position, ratingBar.getRating());
            }
        });

        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener(){
            @Override
            public void onClick(DialogInterface dialog, int which){
                _listener.onDialogNegativeClick(RateFoodDialogFragment.this);
            }

        });
        Bundle b = getArguments();
        _position = b.getInt("position");

        Dialog dialog = builder.create();
        return dialog;

    }

}
