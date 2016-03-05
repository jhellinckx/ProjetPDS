package com.pds.app.caloriecounter.dayrecording;

import android.content.Context;
import android.view.ViewGroup;
import com.pds.app.caloriecounter.progresslib.DonutProgress;
import static com.pds.app.caloriecounter.dayrecording.GraphicsConstants.Progress.*;
/**
 * Created by jhellinckx on 03/03/16.
 */
public class IntakeProgress extends DonutProgress {
    private float intakeProgress;
    private float intakeMax;
    private String unit;

    public IntakeProgress(Context context, float intakeProgress, float intakeMax, String unit){
        super(context);
        this.intakeProgress = intakeProgress;
        this.intakeMax = intakeMax;
        this.unit = unit;
        initDonutProgressValues();
        initDonutStroke();
        initDonutText();
    }

    private void initDonutProgressValues(){
        this.setIntakeProgress(intakeProgress);
        this.setIntakeMax(intakeMax);
        this.setMax(BAR_PRECISION);
        this.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        //this.setLayoutParams(new ViewGroup.LayoutParams(DONUT_WIDTH, DONUT_HEIGHT));

        this.setStartingDegree(STARTING_DEGREE);
    }

    private void initDonutStroke(){
        this.setUnfinishedStrokeWidth(STROKE_UNFINISHED_WIDTH);
        this.setFinishedStrokeWidth(SRTOKE_FINISHED_WIDTH);
        this.setUnfinishedStrokeColor(STROKE_UNFINISHED_COLOR);
        this.setFinishedStrokeColor(STROKE_FINISHED_COLOR);
    }

    private void initDonutText(){
        this.setTextSize(TEXT_CENTER_SIZE);
        this.setTextColor(TEXT_CENTER_COLOR);
        this.setText(intakeString(intakeProgress) + " " + unit);
        this.setInnerBottomText("/" + intakeString(intakeMax));
        this.setInnerBottomTextColor(TEXT_BOTTOM_COLOR);
        this.setInnerBottomTextSize(TEXT_BOTTOM_SIZE);
    }

    private int floatProgressToDonutScale(){
        return (int)Math.ceil((intakeProgress / intakeMax)*BAR_PRECISION);

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
            setProgress(floatProgressToDonutScale());
        }
    }

    public float getIntakeMax(){
        return intakeMax;
    }

    public void setIntakeProgress(float progress){
        if(progress > 0) {
            intakeProgress = progress;
            setProgress(floatProgressToDonutScale());
        }
    }

    public float getIntakeProgress(){
        return intakeProgress;
    }

}
