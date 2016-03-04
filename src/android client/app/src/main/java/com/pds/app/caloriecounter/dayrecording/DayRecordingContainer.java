package com.pds.app.caloriecounter.dayrecording;

import android.content.Context;
import android.graphics.Color;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.pds.app.caloriecounter.R;
import com.pds.app.caloriecounter.Utils;

class DayRecordingContainer extends LinearLayout {
    private TextView title = null;
    private View content = null;

    public DayRecordingContainer(Context context, String title, View content){
        super(context);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
        int marginLeft = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 5, getResources().getDisplayMetrics());
        int marginTop = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 5, getResources().getDisplayMetrics());
        int marginRight = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 5, getResources().getDisplayMetrics());
        int marginBot = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 5, getResources().getDisplayMetrics());
        params.setMargins(marginLeft, marginTop, marginRight, marginBot);
        this.setLayoutParams(params);
        this.setOrientation(LinearLayout.VERTICAL);
        this.setBackgroundColor(getResources().getColor(R.color.white));
        setTitle(title);
        setContent(content);
    }

    public void setTitle(String title){
        boolean added = false;
        if(this.title == null) {
            added = true;
            this.title = new TextView(getContext());
        }
        this.title.setText(title);
        this.title.setTextAppearance(getContext(), android.R.style.TextAppearance_DeviceDefault_Small);
        this.title.setTextColor(Color.LTGRAY);
        this.title.setGravity(Gravity.LEFT);
        int marginLeft = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 5, getResources().getDisplayMetrics());
        int marginTop = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 5, getResources().getDisplayMetrics());
        int marginRight = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 5, getResources().getDisplayMetrics());
        int marginBot = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 5, getResources().getDisplayMetrics());
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
        layoutParams.setMargins(marginLeft, marginTop, marginRight, marginBot);
        this.title.setLayoutParams(layoutParams);
        if(added) this.addView(this.title);
    }

    public void setContent(View content){
        this.content = content;
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        params.setMargins(Utils.dp(getContext(), 10), Utils.dp(getContext(), 10), Utils.dp(getContext(), 10), Utils.dp(getContext(), 10));
        content.setLayoutParams(params);
        this.addView(this.content);
    }
}
