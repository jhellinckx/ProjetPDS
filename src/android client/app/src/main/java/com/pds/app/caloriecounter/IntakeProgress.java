package com.pds.app.caloriecounter;

import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.view.ViewGroup;


import com.pds.app.caloriecounter.progresslib.DonutProgress;
import com.pds.app.caloriecounter.progresslib.Utils;
/**
 * Created by jhellinckx on 03/03/16.
 */
public class IntakeProgress extends DonutProgress {
    private int defaultFinishedColor = getResources().getColor(R.color.smalt);
    private int defaultUnfinishedColor = Color.WHITE;
    private float defaultUnfinishedWidth = Utils.dp2px(getResources(), 3);
    private float defaultFinishedWidth = Utils.dp2px(getResources(), 8);

    private float defaultCenterTextSize = Utils.sp2px(getResources(), 15);
    private int defaultCenterTextColor = Color.BLACK;

    private float defaultBottomTextSize = Utils.dp2px(getResources(), 10);
    private int defaultBottomTextColor = Color.LTGRAY;

    private static int BAR_MAX = 100;

    private float intakeProgress;
    private float intakeMax;

    private int floatProgressToInt(){
        return (int)Math.ceil((intakeProgress / intakeMax)*BAR_MAX);

    }

    public IntakeProgress(Context context, float intakeProgress, float intakeMax, String unit){
        super(context);
        this.intakeProgress = intakeProgress;
        this.intakeMax = intakeMax;
        this.setIntakeProgress(intakeProgress);
        this.setIntakeMax(intakeMax);

        this.setMax(BAR_MAX);

        this.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));

        this.setStartingDegree(270);

        this.setUnfinishedStrokeWidth(defaultUnfinishedWidth);
        this.setFinishedStrokeWidth(defaultFinishedWidth);

        this.setUnfinishedStrokeColor(defaultUnfinishedColor);
        this.setFinishedStrokeColor(defaultFinishedColor);



        this.setTextSize(defaultCenterTextSize);
        this.setTextColor(defaultCenterTextColor);
        this.setSuffixText(" " + unit);
        this.setInnerBottomText("/" + Integer.toString(getMax()));
        this.setInnerBottomTextColor(defaultBottomTextColor);
        this.setInnerBottomTextSize(defaultBottomTextSize);

        this.setText("");
    }

    public void setIntakeMax(float max){
        if(max > 0) {
            intakeMax = max;
            setProgress(floatProgressToInt());
        }
    }

    public float getIntakeMax(){
        return intakeMax;
    }

    public void setIntakeProgress(float progress){
        if(progress > 0) {
            intakeProgress = progress;
            setProgress(floatProgressToInt());
        }
    }

    public float getIntakeProgress(){
        return intakeProgress;
    }

}
