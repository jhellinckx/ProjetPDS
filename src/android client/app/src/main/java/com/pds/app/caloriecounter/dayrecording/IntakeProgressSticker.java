package com.pds.app.caloriecounter.dayrecording;

import android.content.Context;
import android.view.Gravity;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.pds.app.caloriecounter.R;
import static com.pds.app.caloriecounter.dayrecording.GraphicsConstants.ProgressSticker.*;

public class IntakeProgressSticker extends LinearLayout {
    private TextView intakeType;
    private IntakeProgress intakeProgress;

    public IntakeProgressSticker(Context context, String intakeType, IntakeProgress progress) {
        super(context);
        this.intakeProgress = progress;
        initContainerLayout();
        initIntakeText(intakeType);
        initIntakeProgress();
    }

    private void initContainerLayout(){
        this.setOrientation(LinearLayout.VERTICAL);
        this.setBackgroundColor(BACKGROUND_COLOR);
        this.setPadding(0, 0, 0, 0);
        this.setLayoutParams(new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
    }

    private void initIntakeText(String intakeType) {
        this.intakeType = new TextView(getContext());
        this.intakeType.setText(intakeType);
        this.intakeType.setTextAppearance(getContext(), android.R.style.TextAppearance_DeviceDefault_Medium);
        this.intakeType.setTextColor(TEXT_COLOR);
        this.intakeType.setGravity(Gravity.CENTER_HORIZONTAL);
        this.intakeType.setTextSize(TEXT_SIZE);
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
        layoutParams.setMargins(MARGIN_LEFT, MARGIN_TOP, MARGIN_RIGHT, MARGIN_BOTTOM);
        this.intakeType.setBackgroundColor(getResources().getColor(R.color.primary_dark));
        this.intakeType.setLayoutParams(layoutParams);
        this.addView(this.intakeType);
    }

    private void initIntakeProgress() {
        this.addView(this.intakeProgress);
    }

    public void setIntakeProgress(IntakeProgress progress){
        this.removeView(this.intakeProgress);
        this.intakeProgress = progress;
        this.addView(progress);
    }
}
