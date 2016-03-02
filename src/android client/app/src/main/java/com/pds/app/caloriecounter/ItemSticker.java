package com.pds.app.caloriecounter;

import android.content.Context;
import android.view.Gravity;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

public class ItemSticker extends LinearLayout {

    private Context context;
    private ImageView thumbnail;
    private TextView describing_text;

    public ItemSticker(Context cont, String url, String txt){
        super(cont);
        context = cont;
        this.setOrientation(LinearLayout.HORIZONTAL);
        initThumbnail(url);
        addDescribingText(txt);
    }

    private void initThumbnail(String url){
        thumbnail = new ImageView(context);
        Picasso.with(context)
                .load(url)
                .resize(200,200)
                .into(thumbnail);
        this.addView(thumbnail, 0);
    }

    private void addDescribingText(String txt){
        describing_text = new TextView(context);
        describing_text.setText(txt);
        this.addView(describing_text,1);
    }
}
