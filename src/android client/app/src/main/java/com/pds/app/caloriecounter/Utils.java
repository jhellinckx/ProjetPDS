package com.pds.app.caloriecounter;

import android.content.Context;
import android.util.TypedValue;

/**
 * Created by jhellinckx on 04/03/16.
 */
public class Utils {
    public static int dp(Context context, int dp){
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, context.getResources().getDisplayMetrics());
    }

    public static int sp(Context context, int sp){
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, sp, context.getResources().getDisplayMetrics());
    }
}
