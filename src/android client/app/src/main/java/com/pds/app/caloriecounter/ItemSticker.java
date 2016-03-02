package com.pds.app.caloriecounter;

import android.content.Context;
import android.view.Gravity;
import android.view.ViewGroup;
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
        this.setBackground(context.getResources().getDrawable(R.drawable.background_sticker));
        initThumbnail(url);
        addDescribingText(txt);
    }

    private void initThumbnail(String url){
        thumbnail = new ImageView(context);
        Picasso.with(context)
                .load(url)
                .resize(200,200)
                .transform(new RoundedTransformation(5,0,4))
                .into(thumbnail);
        thumbnail.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT));
        this.addView(thumbnail, 0);
    }

    private void addDescribingText(String txt) {
        describing_text = new TextView(context);
        describing_text.setText(txt);
        describing_text.setTextAppearance(context, android.R.style.TextAppearance_DeviceDefault_Large);
        describing_text.setTextColor(context.getResources().getColor(R.color.white));
        describing_text.setGravity(Gravity.CENTER_VERTICAL);
        describing_text.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT));
        this.addView(describing_text, 1);
    }
}
