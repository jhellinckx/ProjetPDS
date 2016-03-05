package com.pds.app.caloriecounter.dayrecording;

import android.content.Context;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import static com.pds.app.caloriecounter.dayrecording.GraphicsConstants.Sticker.*;

class RecordingContainer extends LinearLayout {
    private TextView title;
    private View content;

    public RecordingContainer(Context context, String title, View content){
        super(context);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
        params.setMargins(MARGIN_LEFT, MARGIN_TOP, MARGIN_RIGHT, MARGIN_BOTTOM);
        this.setLayoutParams(params);
        this.setOrientation(LinearLayout.VERTICAL);
        this.setBackgroundColor(BACKGROUND_COLOR);
        initTitle(title);
        initContent(content);
    }

    private void initTitle(String title){
        this.title = new TextView(getContext());
        this.title.setText(title);
        this.title.setTextAppearance(getContext(), android.R.style.TextAppearance_DeviceDefault_Small);
        this.title.setTextColor(TEXT_COLOR);
        this.title.setGravity(TEXT_GRAVITY);
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
        layoutParams.setMargins(TEXT_LEFT_MARGIN, TEXT_TOP_MARGIN, TEXT_RIGHT_MARGIN, TEXT_BOTTOM_MARGIN);
        this.title.setLayoutParams(layoutParams);
        this.addView(this.title);
    }

    private void initContent(View content){
        this.content = content;
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        params.setMargins(CONTENT_LEFT_MARGIN, CONTENT_TOP_MARGIN, CONTENT_RIGHT_MARGIN, CONTENT_BOTTOM_MARGIN);
        content.setLayoutParams(params);
        this.addView(this.content);
    }

    public void setTitle(String title){
        this.title.setText(title);
    }

    public void setContent(View content){
        this.removeView(this.content);
        this.initContent(content);
    }
}
