package com.pds.app.caloriecounter.dayrecording;

import android.content.Context;
import android.util.Log;
import android.view.ViewGroup;

import com.pds.app.caloriecounter.rawlibs.DonutProgress;
import com.pds.app.caloriecounter.utils.Converter;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.ParseException;

import static com.pds.app.caloriecounter.GraphicsConstants.Progress.*;
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
        this.setText(Converter.floatToString(intakeProgress) + " " + unit);
        this.setInnerBottomText("/" + Converter.floatToString(intakeMax));
        this.setInnerBottomTextColor(TEXT_BOTTOM_COLOR);
        this.setInnerBottomTextSize(TEXT_BOTTOM_SIZE);
    }

    private int floatProgressToDonutScale(){
        return (intakeProgress>intakeMax) ? BAR_PRECISION : (int)Math.ceil((intakeProgress / intakeMax)*BAR_PRECISION);

    }

    public void setIntakeMax(float max){
        if(max > 0) {
            intakeMax = changeFloatPrecision(max, 0);
            setIntakeProgress(this.intakeProgress);
            this.setInnerBottomText("/" + Converter.floatToString(intakeMax));
        }
    }

    public float getIntakeMax(){
        return intakeMax;
    }

    public void setIntakeProgress(float progress){
        if(progress >= 0) {
            Log.d("BEFORE: ", Float.toString(progress));
            //intakeProgress = progress;
            intakeProgress =  changeFloatPrecision(progress, FLOAT_PRECISION);
            Log.d("AFTER: ", Float.toString(intakeProgress));
            setProgress(floatProgressToDonutScale());
            this.setText(Converter.floatToString(intakeProgress) + " " + unit);
        }
    }

    public float changeFloatPrecision(float f, int i){
        String floatString = Float.toString(f);
        String charSeparator = (floatString.contains(".")) ? "." : ",";
        System.out.println("CHAR SEPARATOR: "+charSeparator);
        System.out.println(String.format("%" + "." + Integer.toString(i) + "f", f));

        DecimalFormatSymbols symbols = new DecimalFormatSymbols();
        symbols.setDecimalSeparator('.');
        DecimalFormat format = new DecimalFormat("0.#");
        format.setDecimalFormatSymbols(symbols);
        float fl;
        try {
            fl = format.parse(floatString).floatValue();
        }catch( ParseException e){
            fl = 0.0f;
        }

        //return Float.parseFloat(String.format("%" + "." + Integer.toString(i) + "f", f));
        return fl;
    }

    public void addIntakeProgress(float progress){
        if(progress > 0) setIntakeProgress(intakeProgress + progress);
    }

    public void substractIntakeProgress(float regress){
        if(regress > 0) {
            float sub = intakeProgress - regress;
            setIntakeProgress((sub > 0) ? sub : 0);
        }
    }

    public float getIntakeProgress(){
        return intakeProgress;
    }
}
