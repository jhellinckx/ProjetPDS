package com.pds.app.caloriecounter.utils;

import android.content.Context;
import android.view.View;
import android.widget.LinearLayout;

/**
 * Created by jhellinckx on 05/03/16.
 */
public class EvenSpaceView extends View { // Needed to horizontally fill gaps between other views
    public EvenSpaceView(Context context){
        super(context);
        this.setLayoutParams(new LinearLayout.LayoutParams(Converter.dp2px(0), Converter.dp2px(1), 1));
    }
}