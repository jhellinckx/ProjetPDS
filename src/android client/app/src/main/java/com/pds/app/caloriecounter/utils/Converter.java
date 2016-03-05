package com.pds.app.caloriecounter.utils;

import android.content.Context;
import android.content.res.Resources;
import android.util.TypedValue;

/**
 * Created by jhellinckx on 05/03/16.
 */
public class Converter {
    public static int dp2px(int dp){
        return (int) (dp * Resources.getSystem().getDisplayMetrics().density);
    }

    public static int px2dp(int px){
        return (int) (px / Resources.getSystem().getDisplayMetrics().density);
    }

    public static int sp2px(int sp){
        return (int) (sp * Resources.getSystem().getDisplayMetrics().scaledDensity);
    }

    public static int px2sp(int px){
        return (int) (px / Resources.getSystem().getDisplayMetrics().scaledDensity);
    }

    public static String floatToString(Float f){
        String fString = Float.toString(f);
        String[] dotSplitted = fString.split("\\.");
        if (dotSplitted.length > 1 && Integer.valueOf(dotSplitted[1]).equals(0)) {
            fString = dotSplitted[0];
        }
        return fString;
    }
}
