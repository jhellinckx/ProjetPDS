package com.pds.app.caloriecounter.itemview;

import android.content.Context;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutCompat;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.pds.app.caloriecounter.R;
import com.pds.app.caloriecounter.utils.Converter;
import com.squareup.picasso.Picasso;

import org.calorycounter.shared.models.EdibleItem;

import de.hdodenhof.circleimageview.CircleImageView;
import static com.pds.app.caloriecounter.GraphicsConstants.ItemSticker.*;
import static com.pds.app.caloriecounter.GraphicsConstants.Global.*;

/**
 * Created by jhellinckx on 05/03/16.
 */
public class EdibleItemSticker extends CardView {
    private class EdibleItemStickerTextLayout extends LinearLayout{
        EdibleItemSticker card;
        EdibleItemStickerTextLayout(Context context, EdibleItemSticker card){
            super(context);
            this.card = card;
        }

        @Override
        protected void onSizeChanged(int w, int h, int oldw, int oldh){
            if(oldw == 0) {
                Log.d("CALLING INIT ACTIONS :", Integer.toString(oldw) + " -> " + Integer.toString(w));
                card.initActions(w, h);
            }
        }
    }
    private EdibleItem item;
    private LinearLayout cardLayout;
    private CircleImageView cardImage;
    private LinearLayout textCont;
    boolean removable; boolean addable;
    boolean ratable; boolean expandable;


    public EdibleItemSticker(Context context, EdibleItem item){
        this(context, item, false, false, false, false);
    }

    public EdibleItemSticker(Context context, EdibleItem item, boolean removable,
                             boolean addable, boolean ratable, boolean expandable){
        super(context);
        setEdibleItem(item, removable, addable, ratable, expandable);
    }

    public void setEdibleItem(EdibleItem item, boolean removable,
                              boolean addable, boolean ratable, boolean expandable){
        this.item = item;
        this.removable = removable; this.addable = addable;
        this.ratable = ratable; this.expandable = expandable;
        initCard();
        initImage();
        initTexts();
    }

    private void initCard(){
        LinearLayout.LayoutParams cardParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        this.setLayoutParams(cardParams);

        cardLayout = new LinearLayout(getContext());
        cardLayout.setOrientation(LinearLayout.HORIZONTAL);
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        layoutParams.setMargins(0, 0, 0, BOTTOM_MARGIN);
        cardLayout.setLayoutParams(layoutParams);
        cardLayout.setPadding(CARD_PADDING, CARD_PADDING, CARD_PADDING, CARD_PADDING);
        this.addView(cardLayout);
    }

    private void initImage(){
        cardImage = new CircleImageView(getContext());
        Picasso.with(getContext())
                .load(item.getImageUrl())
                .into(cardImage);
        LinearLayout.LayoutParams imageParams = new LinearLayout.LayoutParams(IMAGE_WIDTH, IMAGE_HEIGHT);
        imageParams.setMargins(0, 0, SPACE_BETWEEN_IMAGE_AND_TEXT, 0);
        cardImage.setLayoutParams(imageParams);
        cardImage.setBorderColor(IMAGE_BORDER_COLOR);
        cardImage.setBorderWidth(IMAGE_BORDER_WIDTH);
        cardLayout.addView(cardImage);
    }

    private void initTexts(){
        textCont = new EdibleItemStickerTextLayout(getContext(), this);
        LinearLayout.LayoutParams textContParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
        textCont.setLayoutParams(textContParams);
        textCont.setOrientation(LinearLayout.VERTICAL);
        textCont.setGravity(Gravity.CENTER_VERTICAL);

        TextView mainText = new TextView(getContext());
        LinearLayout.LayoutParams mainTextParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        mainText.setLayoutParams(mainTextParams);
        mainText.setTextSize(MAIN_TEXT_SIZE);
        mainText.setTextColor(MAIN_TEXT_COLOR);
        mainText.setText(item.getProductName());
        mainText.setMaxLines(MAIN_TEXT_MAX_LINES);
        mainText.canScrollHorizontally(LinearLayout.HORIZONTAL);
        mainText.setEllipsize(TextUtils.TruncateAt.END);
        textCont.addView(mainText);

        String nutrInfos = getNutrInfos();
        if(! nutrInfos.isEmpty()) {
            TextView secondaryText = new TextView(getContext());
            LinearLayout.LayoutParams secondaryTextParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            secondaryText.setLayoutParams(secondaryTextParams);
            secondaryText.setTextSize(SECONDARY_TEXT_SIZE);
            secondaryText.setTextColor(SECONDARY_TEXT_COLOR);
            secondaryText.setText(nutrInfos);
            secondaryText.setMaxLines(SECONDARY_TEXT_MAX_LINES);
            secondaryText.canScrollHorizontally(LinearLayout.HORIZONTAL);
            secondaryText.setEllipsize(TextUtils.TruncateAt.END);
            textCont.addView(secondaryText);
        }
        cardLayout.addView(textCont);
    }

    private void initActions(int w, int h){
        if(removable){
            textCont.setLayoutParams(new LinearLayout.LayoutParams(w - ICON_DELETE_SIZE, LinearLayout.LayoutParams.MATCH_PARENT));
            ImageView clearIcon = new ImageView(getContext());
            clearIcon.setLayoutParams(new LinearLayoutCompat.LayoutParams(ICON_DELETE_SIZE, ICON_DELETE_SIZE));
            clearIcon.setImageResource(R.drawable.ic_clear_grey_600_18dp);
            clearIcon.setScaleType(ImageView.ScaleType.FIT_END);
            cardLayout.addView(clearIcon);
        }
        if(addable){

        }
        if(expandable){

        }
        if(ratable){

        }
    }

    private String getNutrInfos(){
        String infos = "";
        if(item.getTotalEnergy() != null)
            infos += Converter.floatToString(item.getTotalEnergy()) + " " + CALORIES_UNIT + ", ";
        if(item.getTotalProteins() != null)
            infos += Converter.floatToString(item.getTotalProteins()) + " " + DEFAULT_UNIT + " " + TITLE_PROTEINS.toLowerCase() + ", ";
        if(item.getTotalCarbohydrates() != null)
            infos += Converter.floatToString(item.getTotalCarbohydrates()) + " " + DEFAULT_UNIT + " " + TITLE_CARBO.toLowerCase() + ", ";
        if(! infos.isEmpty())
            infos = new String(infos.substring(0, infos.length() - 2));
        return infos;
    }
}