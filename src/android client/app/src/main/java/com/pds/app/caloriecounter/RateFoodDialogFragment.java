package com.pds.app.caloriecounter;


import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;

public class RateFoodDialogFragment extends DialogFragment {

    // This interface allows event callbacks to RatingAcitivy.

    public interface RateFoodDialogListener{
        public void onDialogPositiveClick(DialogFragment dialog);
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
        LayoutInflater inflater = getActivity().getLayoutInflater();
        builder.setView(inflater.inflate(R.layout.rating_dialog,null));

        builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                _listener.onDialogPositiveClick(RateFoodDialogFragment.this);
            }
        });

        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener(){
            @Override
            public void onClick(DialogInterface dialog, int which){
                _listener.onDialogNegativeClick(RateFoodDialogFragment.this);
            }

        });

        Dialog dialog = builder.create();
        return dialog;

    }

}
