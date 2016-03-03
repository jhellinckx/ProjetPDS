package com.pds.app.caloriecounter;

import android.content.Context;
import android.graphics.Color;
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

    public IntakeProgress(Context context, int currentProgress, int max, String unit){
        super(context);
        this.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));

        this.setStartingDegree(270);

        this.setUnfinishedStrokeWidth(defaultUnfinishedWidth);
        this.setFinishedStrokeWidth(defaultFinishedWidth);

        this.setUnfinishedStrokeColor(defaultUnfinishedColor);
        this.setFinishedStrokeColor(defaultFinishedColor);

        this.setProgress(currentProgress);
        this.setMax(max);

        this.setTextSize(defaultCenterTextSize);
        this.setTextColor(defaultCenterTextColor);
        this.setSuffixText(" " + unit);
        this.setInnerBottomText("/"+Integer.toString(getMax()));
        this.setInnerBottomTextColor(defaultBottomTextColor);
        this.setInnerBottomTextSize(defaultBottomTextSize);

        this.setText("lol");
    }


}
