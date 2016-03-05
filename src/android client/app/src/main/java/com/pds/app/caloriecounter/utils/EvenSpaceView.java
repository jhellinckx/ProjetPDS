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
        this.setLayoutParams(new LinearLayout.LayoutParams(Converter.dp(context, 0), Converter.dp(context, 1), 1));
    }
}