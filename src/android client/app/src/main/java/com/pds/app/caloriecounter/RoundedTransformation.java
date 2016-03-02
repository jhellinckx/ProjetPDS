package com.pds.app.caloriecounter;

import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.Shader;

import com.squareup.picasso.Transformation;

public class RoundedTransformation implements Transformation{

    private static int mBorder;
    private static final float RADIUS_FACTOR = 1.80F;

    private final int radius;
    private final int margin;

    public RoundedTransformation(final int rad, final int marg, final int borderWidth){
        this.radius = rad;
        this.margin = marg;
        this.mBorder = borderWidth;
    }

    @Override
    public Bitmap transform(final Bitmap source){
        final Paint paint = new Paint();
        final Paint border = new Paint();
        border.setColor(Color.parseColor("#303030"));
        border.setStyle(Paint.Style.STROKE);
        border.setAntiAlias(true);
        border.setStrokeWidth(mBorder);
        paint.setAntiAlias(true);
        paint.setShader(new BitmapShader(source, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP));

        Bitmap output = Bitmap.createBitmap(source.getWidth(), source.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(output);
        //canvas.drawCircle((source.getWidth()-margin)/2, (source.getHeight()-margin)/2, (radius-BORDER_WIDTH)*RADIUS_FACTOR, paint);
        //canvas.drawCircle((source.getWidth()-margin)/2, (source.getHeight()-margin)/2, (radius-BORDER_WIDTH)*RADIUS_FACTOR, border);
        canvas.drawRoundRect(new RectF(margin, margin, source.getWidth()-margin,
                source.getHeight()-margin), radius-mBorder, radius-mBorder, paint);
        canvas.drawRoundRect(new RectF(margin, margin, source.getWidth()-margin,
                source.getHeight()-margin), radius, radius, border);

        if (source != output){
            source.recycle();
        }

        return output;
    }

    @Override
    public String key(){
        return "rounded";
    }
}
