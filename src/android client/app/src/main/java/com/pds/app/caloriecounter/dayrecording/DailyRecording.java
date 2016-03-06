package com.pds.app.caloriecounter.dayrecording;

import android.content.Context;
import android.support.v7.widget.CardView;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import static com.pds.app.caloriecounter.GraphicsConstants.Recording.*;

class DailyRecording extends CardView {
    private TextView title;
    private View content;
    private View footer;
    private LinearLayout layout;

    public DailyRecording(Context context, String title){
        this(context, title, new View(context));
    }

    public DailyRecording(Context context, String title, View content){
        super(context);
        LinearLayout.LayoutParams cardParams = new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
        cardParams.setMargins(MARGIN_LEFT, MARGIN_TOP, MARGIN_RIGHT, MARGIN_BOTTOM);
        this.setLayoutParams(cardParams);

        layout = new LinearLayout(context);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
        layout.setLayoutParams(params);
        layout.setOrientation(LinearLayout.VERTICAL);
        //layout.setBackgroundColor(BACKGROUND_COLOR);
        initTitle(title);
        initContent(content);
        this.addView(layout);
    }

    private void initTitle(String title){
        this.title = new TextView(getContext());
        this.title.setText(title);
        this.title.setTextAppearance(getContext(), android.R.style.TextAppearance_DeviceDefault_Small);
        this.title.setTextColor(TITLE_COLOR);
        this.title.setGravity(TITLE_GRAVITY);
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
        layoutParams.setMargins(TITLE_LEFT_MARGIN, TITLE_TOP_MARGIN, TITLE_RIGHT_MARGIN, TITLE_BOTTOM_MARGIN);
        this.title.setLayoutParams(layoutParams);
        layout.addView(this.title);
    }

    private void initContent(View content){
        this.content = content;
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        params.setMargins(CONTENT_LEFT_MARGIN, CONTENT_TOP_MARGIN, CONTENT_RIGHT_MARGIN, CONTENT_BOTTOM_MARGIN);
        this.content.setLayoutParams(params);
        layout.addView(this.content);
    }

    private void initFooter(View footer){
        this.footer = footer;
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        params.setMargins(CONTENT_LEFT_MARGIN, 0, CONTENT_RIGHT_MARGIN, CONTENT_BOTTOM_MARGIN);
        this.footer.setLayoutParams(params);
        layout.addView(footer);
    }

    public void setTitle(String title){
        this.title.setText(title);
    }

    public void setContent(View content){
        layout.removeView(this.content);
        this.initContent(content);
    }

    public void setFooter(View footer){
        layout.removeView(this.footer);
        this.initFooter(footer);
    }
}
