package com.pds.app.caloriecounter.dayrecording;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.CardView;
import android.view.Gravity;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.pds.app.caloriecounter.R;
import static com.pds.app.caloriecounter.GraphicsConstants.ProgressSticker.*;

public class IntakeProgressSticker extends CardView {
    private TextView intakeType;
    private IntakeProgress intakeProgress;
    private LinearLayout cardLayout;

    public IntakeProgressSticker(Context context, String intakeType, IntakeProgress progress) {
        super(context);
        this.intakeProgress = progress;
        initCard();
        initIntakeText(intakeType);
        initIntakeProgress();
    }

    private void initCard(){
        LinearLayout.LayoutParams cardParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        this.setLayoutParams(cardParams);
        this.setPreventCornerOverlap(false);

        cardLayout = new LinearLayout(getContext());
        cardLayout.setOrientation(LinearLayout.VERTICAL);
        cardLayout.setBackgroundColor(BACKGROUND_COLOR);
        cardLayout.setPadding(0, 0, 0, 0);
        cardLayout.setLayoutParams(new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
        this.addView(cardLayout);
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
        cardLayout.addView(this.intakeType);
    }

    private void initIntakeProgress() {
        cardLayout.addView(this.intakeProgress);
    }

    public void setIntakeProgress(IntakeProgress progress){
        cardLayout.removeView(this.intakeProgress);
        this.intakeProgress = progress;
        cardLayout.addView(progress);
    }
}
