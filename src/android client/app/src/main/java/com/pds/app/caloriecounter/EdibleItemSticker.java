package com.pds.app.caloriecounter;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.CardView;
import android.view.Gravity;
import android.widget.LinearLayout;
import android.widget.TextView;

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
    private EdibleItem item;
    private LinearLayout cardLayout;

    EdibleItemSticker(Context context, EdibleItem item){
        super(context);
        this.item = item;
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
        CircleImageView cardImage = new CircleImageView(getContext());
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
        LinearLayout textCont = new LinearLayout(getContext());
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
        textCont.addView(mainText);

        String nutrInfos = getNutrInfos();
        if(! nutrInfos.isEmpty()) {
            TextView secondaryText = new TextView(getContext());
            LinearLayout.LayoutParams secondaryTextParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            secondaryText.setLayoutParams(secondaryTextParams);
            secondaryText.setTextSize(SECONDARY_TEXT_SIZE);
            secondaryText.setTextColor(SECONDARY_TEXT_COLOR);
            secondaryText.setText(nutrInfos);
            textCont.addView(secondaryText);
        }
        cardLayout.addView(textCont);
    }

    private String getNutrInfos(){
        String infos = "";
        if(item.getTotalEnergy() != null)
            infos += Converter.floatToString(item.getTotalEnergy()) + " " + CALORIES_UNIT + " ";
        if(item.getTotalProteins() != null)
            infos += Converter.floatToString(item.getTotalProteins()) + " " + DEFAULT_UNIT + " " + TITLE_PROTEINS.toLowerCase() + " ";
        if(item.getTotalCarbohydrates() != null)
            infos += Converter.floatToString(item.getTotalCarbohydrates()) + " " + DEFAULT_UNIT + " " + TITLE_CARBO.toLowerCase();
        return infos;
    }
}