package com.pds.app.caloriecounter;

import android.content.Context;
import android.graphics.Color;
import android.view.Gravity;

import com.pds.app.caloriecounter.utils.Converter;

/**
 * Created by jhellinckx on 04/03/16.
 */
public final class GraphicsConstants {
    private static Context context = null;

    public static void setContext(Context context){
        GraphicsConstants.context = context;
    }

    public static final class Global{
        public static final String TITLE_RDI = "APPORTS JOURNALIERS";
        public static final String TITLE_FOODS = "MENU";
        public static final String TITLE_SPORTS = "ACTIVITES SPORTIVES";
        public static final String TITLE_CALORIES = "Calories";
        public static final String TITLE_PROTEINS = "Protéines";
        public static final String TITLE_CARBO = "Glucides";
        public static final String TITLE_SALT = "Sels";
        public static final String TITLE_FAT = "Lipides";
        public static final String CALORIES_UNIT = "kcal";
        public static final String DEFAULT_UNIT = "g";
    }

    public static final class Progress {
        public static final int STROKE_FINISHED_COLOR = context.getResources().getColor(R.color.smalt);
        public static final int STROKE_UNFINISHED_COLOR = Color.WHITE;
        public static final float SRTOKE_FINISHED_WIDTH = Converter.dp(context, 8);
        public static final float STROKE_UNFINISHED_WIDTH = Converter.dp(context, 1);
        public static final float TEXT_CENTER_SIZE = Converter.sp(context, 15);
        public static final int TEXT_CENTER_COLOR = Color.WHITE;
        public static final float TEXT_BOTTOM_SIZE = Converter.sp(context, 10);
        public static final int TEXT_BOTTOM_COLOR = Color.LTGRAY;
        public static final int STARTING_DEGREE = 270;
        public static final int BAR_PRECISION = 100;
        public static final int DONUT_WIDTH = Converter.dp(context, 100);
        public static final int DONUT_HEIGHT = Converter.dp(context, 100);
    }

    public static final class ProgressSticker {
        public static final int BACKGROUND_COLOR = context.getResources().getColor(R.color.primary);
        public static final int TEXT_COLOR = context.getResources().getColor(R.color.white);
        public static final int TEXT_SIZE = Converter.sp(context, 8);
        public static final int MARGIN_LEFT = Converter.dp(context, 0);
        public static final int MARGIN_TOP = Converter.dp(context, 0);
        public static final int MARGIN_RIGHT = Converter.dp(context, 0);
        public static final int MARGIN_BOTTOM = Converter.dp(context, 20);
    }

    public static final class Recording{
        public static final int BACKGROUND_COLOR = context.getResources().getColor(R.color.white);
        public static final int MARGIN_LEFT = Converter.dp(context, 5);
        public static final int MARGIN_TOP = Converter.dp(context, 5);
        public static final int MARGIN_RIGHT = Converter.dp(context, 5);
        public static final int MARGIN_BOTTOM = Converter.dp(context, 0);

        public static final int TITLE_COLOR = Color.GRAY;
        public static final int TITLE_GRAVITY = Gravity.LEFT;
        public static final int TITLE_LEFT_MARGIN = Converter.dp(context, 5);
        public static final int TITLE_TOP_MARGIN = Converter.dp(context, 5);
        public static final int TITLE_RIGHT_MARGIN = Converter.dp(context, 5);
        public static final int TITLE_BOTTOM_MARGIN = Converter.dp(context, 5);

        public static final int CONTENT_LEFT_MARGIN = Converter.dp(context, 10);
        public static final int CONTENT_TOP_MARGIN = Converter.dp(context, 10);
        public static final int CONTENT_RIGHT_MARGIN = Converter.dp(context, 10);
        public static final int CONTENT_BOTTOM_MARGIN = Converter.dp(context, 10);
    }

    public static final class ItemSticker{
        public static final int CARD_PADDING = Converter.dp(context, 16);
        public static final int BOTTOM_MARGIN = Converter.dp(context, 8);
        public static final int IMAGE_HEIGHT = Converter.dp(context, 60); // == card height
        public static final int IMAGE_WIDTH = Converter.dp(context, 60);
        public static final int IMAGE_BORDER_COLOR = context.getResources().getColor(R.color.smalt);
        public static final int IMAGE_BORDER_WIDTH = Converter.dp(context, 1);
        public static final int SPACE_BETWEEN_IMAGE_AND_TEXT = Converter.dp(context, 16);
        public static final int MAIN_TEXT_SIZE = Converter.dp(context, 8);
        public static final int MAIN_TEXT_COLOR = Color.GRAY;
        public static final int SECONDARY_TEXT_SIZE = Converter.dp(context, 6);
        public static final int SECONDARY_TEXT_COLOR = Color.LTGRAY;
    }




    /* CONTAINER */

}
