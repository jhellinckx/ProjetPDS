package com.pds.app.caloriecounter.utils;

import android.content.Context;
import android.util.TypedValue;

/**
 * Created by jhellinckx on 05/03/16.
 */
public class Converter {
    public static int dp(Context context, int dp){
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, context.getResources().getDisplayMetrics());
    }

    public static int sp(Context context, int sp){
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, sp, context.getResources().getDisplayMetrics());
    }
}
