package com.pds.app.caloriecounter;

import android.content.Context;
import android.os.Handler;
import android.view.Gravity;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

public class DailyIntakeLayout extends LinearLayout {
    private Context context;
    private TextView intakeType;
    private IntakeProgress intakeProgress;
    private int intakeStatus;


    public DailyIntakeLayout(Context context, String intakeType, int progress, int max, String unit) {
        super(context);
        this.context = context;
        this.setOrientation(LinearLayout.VERTICAL);
        this.setBackgroundColor(getResources().getColor(R.color.primary));
        initIntakeText(intakeType);
        initIntakeProgress(progress, max, unit);
    }

    private void initIntakeText(String intakeType) {
        this.intakeType = new TextView(context);
        this.intakeType.setText(intakeType);
        this.intakeType.setTextAppearance(context, android.R.style.TextAppearance_DeviceDefault_Large);
        this.intakeType.setTextColor(context.getResources().getColor(R.color.black));
        this.intakeType.setGravity(Gravity.CENTER_HORIZONTAL);
        this.intakeType.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        this.addView(this.intakeType);
    }

    private void initIntakeProgress(int progress, int max, String unit) {
        this.intakeProgress = new IntakeProgress(context, progress, max, unit);
        this.intakeProgress.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        this.addView(this.intakeProgress);
    }

    public void setMax(int max){
        this.intakeProgress.setMax(max);
    }

    public int getMax(){
        return intakeProgress.getMax();
    }

    public int getProgress(){
        return intakeProgress.getProgress();
    }

    public void setProgress(int progress){
        intakeProgress.setProgress(progress);
    }
}
