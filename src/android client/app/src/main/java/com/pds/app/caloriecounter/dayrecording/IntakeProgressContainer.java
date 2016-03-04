package com.pds.app.caloriecounter.dayrecording;

import android.content.Context;
import android.view.Gravity;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.pds.app.caloriecounter.R;
import static com.pds.app.caloriecounter.dayrecording.GraphicsConstants.ProgressContainer.*;
public class IntakeProgressContainer extends LinearLayout {
    private Context context;
    private TextView intakeType;
    private IntakeProgress intakeProgress;

    public IntakeProgressContainer(Context context, String intakeType, float progress, float max, String unit) {
        super(context);
        this.context = context;
        this.setOrientation(LinearLayout.VERTICAL);
        this.setBackgroundColor(BACKGROUND_COLOR);
        this.setPadding(0, 0, 0, 0);
        this.setLayoutParams(new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
        initIntakeText(intakeType);
        initIntakeProgress(progress, max, unit);
    }

    private void initIntakeText(String intakeType) {
        this.intakeType = new TextView(context);
        this.intakeType.setText(intakeType);
        this.intakeType.setTextAppearance(context, android.R.style.TextAppearance_DeviceDefault_Medium);
        this.intakeType.setTextColor(TEXT_COLOR);
        this.intakeType.setGravity(Gravity.CENTER_HORIZONTAL);
        this.intakeType.setTextSize(TEXT_SIZE);
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
        layoutParams.setMargins(MARGIN_LEFT, MARGIN_TOP, MARGIN_RIGHT, MARGIN_BOTTOM);
        this.intakeType.setBackgroundColor(getResources().getColor(R.color.primary_dark));
        this.intakeType.setLayoutParams(layoutParams);
        this.addView(this.intakeType);
    }

    private void initIntakeProgress(float barProgress, float barMax, String unit) {
        this.intakeProgress = new IntakeProgress(context, barProgress, barMax, unit);
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
