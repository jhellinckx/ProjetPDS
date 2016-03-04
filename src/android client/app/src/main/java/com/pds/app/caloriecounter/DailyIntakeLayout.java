package com.pds.app.caloriecounter;

import android.content.Context;
import android.graphics.Typeface;
import android.os.Handler;
import android.util.TypedValue;
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
        this.setLayoutParams(new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
        initIntakeText(intakeType);
        initIntakeProgress(progress, max, unit);
    }

    private void initIntakeText(String intakeType) {
        this.intakeType = new TextView(context);
        this.intakeType.setText(intakeType);
        this.intakeType.setTextAppearance(context, android.R.style.TextAppearance_DeviceDefault_Large);
        this.intakeType.setTextColor(context.getResources().getColor(R.color.white));
        this.intakeType.setGravity(Gravity.CENTER_HORIZONTAL);
        this.intakeType.setTextSize(TypedValue.COMPLEX_UNIT_SP, 20);
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
        layoutParams.setMargins(2, 2, 2, 30);
        this.intakeType.setBackgroundColor(getResources().getColor(R.color.primary_dark));
        this.intakeType.setLayoutParams(layoutParams);
        this.addView(this.intakeType);
    }

    private void initIntakeProgress(float barProgress, float barMax, String unit) {
        this.intakeProgress = new IntakeProgress(context, barProgress, barMax, unit);
        int height = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 100, getResources().getDisplayMetrics());
        int width = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 100, getResources().getDisplayMetrics());
        this.intakeProgress.setLayoutParams(new ViewGroup.LayoutParams(width,height ));
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
