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
        public static final String TITLE_RDI = "CONSOMMÉ AUJOURD'HUI";
        public static final String TITLE_FOODS = "MENU";
        public static final String TITLE_SPORTS = "ACTIVITES SPORTIVES";
        public static final String TITLE_CALORIES = "Calories";
        public static final String TITLE_PROTEINS = "Protéines";
        public static final String TITLE_CARBO = "Glucides";
        public static final String TITLE_INFOS = "INFORMATIONS PERSONNELLES";
        public static final String TITLE_RECOM_CONSTR = "CONTRAINTES PROCHAINE RECOMMANDATION";
        public static final String TITLE_SALT = "Sels";
        public static final String TITLE_FAT = "Lipides";
        public static final String CALORIES_UNIT = "kcal";
        public static final String DEFAULT_UNIT = "g";

        public static final float MAP_LOAD_FACTOR = 0.75f;
    }

    public static final class Progress {
        public static final int STROKE_FINISHED_COLOR = context.getResources().getColor(R.color.snowy_mint);
        public static final int STROKE_UNFINISHED_COLOR = Color.WHITE;
        public static final float SRTOKE_FINISHED_WIDTH = Converter.dp2px(8);
        public static final float STROKE_UNFINISHED_WIDTH = Converter.dp2px(1);
        public static final float TEXT_CENTER_SIZE = Converter.sp2px(15);
        public static final int TEXT_CENTER_COLOR = Color.WHITE;
        public static final float TEXT_BOTTOM_SIZE = Converter.sp2px(10);
        public static final int TEXT_BOTTOM_COLOR = Color.LTGRAY;
        public static final int STARTING_DEGREE = 270;
        public static final int BAR_PRECISION = 100;
        public static final int DONUT_WIDTH = Converter.dp2px(100);
        public static final int DONUT_HEIGHT = Converter.dp2px(100);

        public static final int FLOAT_PRECISION = 2;
    }

    public static final class ProgressSticker {
        public static final int BACKGROUND_COLOR = context.getResources().getColor(R.color.primary);
        public static final int TEXT_COLOR = context.getResources().getColor(R.color.white);
        public static final int TEXT_SIZE = Converter.sp2px(8);
        public static final int MARGIN_LEFT = Converter.dp2px(0);
        public static final int MARGIN_TOP = Converter.dp2px(0);
        public static final int MARGIN_RIGHT = Converter.dp2px(0);
        public static final int MARGIN_BOTTOM = Converter.dp2px(20);
    }

    public static final class Recording{
        public static final int BACKGROUND_COLOR = context.getResources().getColor(R.color.white);
        public static final int MARGIN_LEFT = Converter.dp2px(5);
        public static final int MARGIN_TOP = Converter.dp2px(5);
        public static final int MARGIN_RIGHT = Converter.dp2px(5);
        public static final int MARGIN_BOTTOM = Converter.dp2px(0);

        public static final int TITLE_COLOR = Color.GRAY;
        public static final int TITLE_GRAVITY = Gravity.LEFT;
        public static final int TITLE_LEFT_MARGIN = Converter.dp2px(5);
        public static final int TITLE_TOP_MARGIN = Converter.dp2px(5);
        public static final int TITLE_RIGHT_MARGIN = Converter.dp2px(5);
        public static final int TITLE_BOTTOM_MARGIN = Converter.dp2px(5);

        public static final int CONTENT_LEFT_MARGIN = Converter.dp2px(10);
        public static final int CONTENT_TOP_MARGIN = Converter.dp2px(10);
        public static final int CONTENT_RIGHT_MARGIN = Converter.dp2px(10);
        public static final int CONTENT_BOTTOM_MARGIN = Converter.dp2px(10);

        public static final int FLAG_CALORIES = 1;
        public static final int FLAG_PROTEINS = 2;
        public static final int FLAG_CARBO = 3;
        public static final int FLAG_SALT = 4;
        public static final int FLAG_FAT = 5;


    }

    public static final class ItemSticker{
        public static final int CARD_PADDING = Converter.dp2px(4);
        public static final int BOTTOM_MARGIN = Converter.dp2px(8);
        public static final int IMAGE_HEIGHT = Converter.dp2px(60); // == card height
        public static final int IMAGE_WIDTH = Converter.dp2px(60);
        public static final int IMAGE_BORDER_COLOR = context.getResources().getColor(R.color.primary);
        public static final int IMAGE_BORDER_WIDTH = Converter.dp2px(2);
        public static final int SPACE_BETWEEN_IMAGE_AND_TEXT = Converter.dp2px(16);
        public static final int NOT_ICON_MARGIN = Converter.dp2px(10);
        public static final int IMAGE_MARGIN_LEFT = NOT_ICON_MARGIN;
        public static final int IMAGE_MARGIN_TOP = NOT_ICON_MARGIN;
        public static final int IMAGE_MARGIN_RIGHT = SPACE_BETWEEN_IMAGE_AND_TEXT;
        public static final int IMAGE_MARGIN_BOTTOM = NOT_ICON_MARGIN;
        public static final int TEXT_MARGIN_LEFT = Converter.dp2px(0);
        public static final int TEXT_MARGIN_TOP = NOT_ICON_MARGIN;
        public static final int TEXT_MARGIN_RIGHT = NOT_ICON_MARGIN;
        public static final int TEXT_MARGIN_BOTTOM = NOT_ICON_MARGIN;
        public static final int MAIN_TEXT_SIZE = Converter.dp2px(8);
        public static final int MAIN_TEXT_COLOR = Color.GRAY;
        public static final int MAIN_TEXT_MAX_LINES = 1;
        public static final int SECONDARY_TEXT_SIZE = Converter.dp2px(6);
        public static final int SECONDARY_TEXT_COLOR = Color.LTGRAY;
        public static final int SECONDARY_TEXT_MAX_LINES = 1;
        public static final int ICON_SIZE = Converter.dp2px(20);
        public static final int DELETE_ICON = R.drawable.ic_clear_grey_600_18dp;
        public static final int ADD_ICON = R.drawable.ic_add_green_700_18dp;
        public static final int RATE_ICON = R.drawable.ic_star_rate_amber_a200_18dp;
    }

    public static final class ItemList{
        public static final int FLAG_REMOVABLE = 1;
        public static final int FLAG_ADDABLE = 2;
        public static final int FLAG_RATABLE = 3;
        public static final int FLAG_EXPANDABLE = 4;
    }




    /* CONTAINER */

}
