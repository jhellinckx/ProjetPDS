package com.pds.app.caloriecounter;

import android.content.Context;
import android.os.Handler;
import android.view.Gravity;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

public class DailyIntakeProgress extends LinearLayout {
    private Context context;
    private TextView intakeType;
    private ProgressBar intakeProgress;
    private int intakeStatus;


    public DailyIntakeProgress(Context context, String intakeType, int maxValue) {
        super(context);
        this.context = context;
        this.setOrientation(LinearLayout.HORIZONTAL);
        initIntakeText(intakeType);
        initIntakeProgress(maxValue);
    }

    private void initIntakeText(String intakeType){
        this.intakeType = new TextView(context);
        this.intakeType.setText(intakeType);
        this.intakeType.setTextAppearance(context, android.R.style.TextAppearance_DeviceDefault_Large);
        this.intakeType.setTextColor(context.getResources().getColor(R.color.white));
        this.intakeType.setGravity(Gravity.CENTER_VERTICAL);
        this.intakeType.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        this.addView(this.intakeType);
    }

    private void initIntakeProgress(int maxValue){
        this.intakeProgress = new ProgressBar(context);
    }

    public void setTypeOfIntake(String intakeType){

    }
}
