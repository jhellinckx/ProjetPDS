package com.pds.app.caloriecounter;

import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.view.ViewGroup;


import com.pds.app.caloriecounter.progresslib.DonutProgress;
import com.pds.app.caloriecounter.progresslib.Utils;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * Created by jhellinckx on 03/03/16.
 */
public class IntakeProgress extends DonutProgress {
    /* Strokes parameters */
    private int defaultFinishedColor = getResources().getColor(R.color.snowy_mint);
    private int defaultUnfinishedColor = Color.WHITE;
    private float defaultUnfinishedWidth = Utils.dp2px(getResources(), 1);
    private float defaultFinishedWidth = Utils.dp2px(getResources(), 8);

    /* Center text parameters */
    private float defaultCenterTextSize = Utils.sp2px(getResources(), 15);
    private int defaultCenterTextColor = Color.WHITE;

    /* Bottom text parameters */
    private float defaultBottomTextSize = Utils.dp2px(getResources(), 10);
    private int defaultBottomTextColor = Color.LTGRAY;

    private int startingDegree = 270;

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
        this.setStartingDegree(startingDegree);
        this.setUnfinishedStrokeWidth(defaultUnfinishedWidth);
        this.setFinishedStrokeWidth(defaultFinishedWidth);
        this.setUnfinishedStrokeColor(defaultUnfinishedColor);
        this.setFinishedStrokeColor(defaultFinishedColor);
        this.setTextSize(defaultCenterTextSize);
        this.setTextColor(defaultCenterTextColor);
        this.setText(intakeString(intakeProgress) + " " + unit);
        this.setInnerBottomText("/" + intakeString(intakeMax));
        this.setInnerBottomTextColor(defaultBottomTextColor);
        this.setInnerBottomTextSize(defaultBottomTextSize);

    }

    private String intakeString(Float intake){
        String intakeString = Float.toString(intake);
        String[] dotSplitted = intakeString.split("\\.");
        if (dotSplitted.length > 1 && Integer.valueOf(dotSplitted[1]).equals(0)) {
            intakeString = dotSplitted[0];
        }
        return intakeString;
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
