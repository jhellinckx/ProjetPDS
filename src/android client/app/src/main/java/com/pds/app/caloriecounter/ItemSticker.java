package com.pds.app.caloriecounter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.Shader;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.squareup.picasso.Transformation;

public class ItemSticker extends LinearLayout {

    private Context context;
    private ImageView thumbnail;
    private TextView describing_text;

    public ItemSticker(Context cont, String url, String txt){
        super(cont);
        context = cont;
        this.setOrientation(LinearLayout.HORIZONTAL);
        this.setBackground(context.getResources().getDrawable(R.drawable.background_sticker));
        initThumbnail(url);
        addDescribingText(txt);
    }

    private void initThumbnail(String url){
        thumbnail = new ImageView(context);
        Picasso.with(context)
                .load(url)
                .resize(200,200)
                .transform(new RoundedTransformation(5,0))
                .into(thumbnail);
        thumbnail.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT));
        this.addView(thumbnail, 0);
    }

    private void addDescribingText(String txt) {
        describing_text = new TextView(context);
        describing_text.setText(txt);
        this.addView(describing_text, 1);
    }
}
