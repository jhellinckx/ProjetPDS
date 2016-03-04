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



    public DailyIntakeLayout(Context context, String intakeType, float progress, float max, String unit) {
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

    private void initIntakeProgress(float barProgress, float barMax, String unit) {
        this.intakeProgress = new IntakeProgress(context, barProgress, barMax, unit);
        this.intakeProgress.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        this.addView(this.intakeProgress);
    }

    public void setMax(float max){
        this.intakeProgress.setIntakeMax(max);
    }

    public float getMax(){
        return intakeProgress.getIntakeMax();
    }

    public float getProgress(){
        return intakeProgress.getIntakeProgress();
    }

    public void setProgress(float progress){
        intakeProgress.setIntakeProgress(progress);
    }
}
