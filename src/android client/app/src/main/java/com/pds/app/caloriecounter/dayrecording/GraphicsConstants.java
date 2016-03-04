package com.pds.app.caloriecounter.dayrecording;

import android.content.Context;
import android.graphics.Color;

import com.pds.app.caloriecounter.R;
import com.pds.app.caloriecounter.Utils;

/**
 * Created by jhellinckx on 04/03/16.
 */
public final class GraphicsConstants {
    private static Context context = null;

    public static void setContext(Context context){
        GraphicsConstants.context = context;
    }

    public static final class Progress {
        public static final int STROKE_FINISHED_COLOR = context.getResources().getColor(R.color.snowy_mint);
        public static final int STROKE_UNFINISHED_COLOR = Color.WHITE;
        public static final float SRTOKE_FINISHED_WIDTH = Utils.dp(context, 8);
        public static final float STROKE_UNFINISHED_WIDTH = Utils.dp(context, 1);
        public static final float TEXT_CENTER_SIZE = Utils.sp(context, 15);
        public static final int TEXT_CENTER_COLOR = Color.WHITE;
        public static final float TEXT_BOTTOM_SIZE = Utils.sp(context, 10);
        public static final int TEXT_BOTTOM_COLOR = Color.LTGRAY;
        public static final int STARTING_DEGREE = 270;
        public static final int BAR_PRECISION = 100;
        public static final int DONUT_WIDTH = Utils.dp(context, 100);
        public static final int DONUT_HEIGHT = Utils.dp(context, 100);
    }

    public static final class ProgressContainer {
        public static final int BACKGROUND_COLOR = context.getResources().getColor(R.color.primary);
        public static final int TEXT_COLOR = context.getResources().getColor(R.color.white);
        public static final int TEXT_SIZE = Utils.sp(context, 8);
        public static final int MARGIN_LEFT = Utils.dp(context, 0);
        public static final int MARGIN_TOP = Utils.dp(context, 0);
        public static final int MARGIN_RIGHT = Utils.dp(context, 0);
        public static final int MARGIN_BOTTOM = Utils.dp(context, 20);
    }



    /* CONTAINER */

}
