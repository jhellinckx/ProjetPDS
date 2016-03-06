package com.pds.app.caloriecounter.itemview;

import android.content.Context;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutCompat;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.pds.app.caloriecounter.utils.Converter;
import com.pds.app.caloriecounter.utils.EvenSpaceView;

import org.calorycounter.shared.models.Sport;

import java.util.ArrayList;
import java.util.List;

import static org.calorycounter.shared.Constants.network.*;

import static com.pds.app.caloriecounter.GraphicsConstants.Global.CALORIES_UNIT;
import static com.pds.app.caloriecounter.GraphicsConstants.ItemSticker.ADD_ICON;
import static com.pds.app.caloriecounter.GraphicsConstants.ItemSticker.DELETE_ICON;
import static com.pds.app.caloriecounter.GraphicsConstants.ItemSticker.ICON_SIZE;
import static com.pds.app.caloriecounter.GraphicsConstants.ItemSticker.MAIN_TEXT_COLOR;
import static com.pds.app.caloriecounter.GraphicsConstants.ItemSticker.MAIN_TEXT_MAX_LINES;
import static com.pds.app.caloriecounter.GraphicsConstants.ItemSticker.MAIN_TEXT_SIZE;
import static com.pds.app.caloriecounter.GraphicsConstants.ItemSticker.NOT_ICON_MARGIN;
import static com.pds.app.caloriecounter.GraphicsConstants.ItemSticker.SECONDARY_TEXT_COLOR;
import static com.pds.app.caloriecounter.GraphicsConstants.ItemSticker.SECONDARY_TEXT_MAX_LINES;
import static com.pds.app.caloriecounter.GraphicsConstants.ItemSticker.SECONDARY_TEXT_SIZE;
import static com.pds.app.caloriecounter.GraphicsConstants.ItemSticker.CARD_PADDING;

/**
 * Created by mrmmtb on 06.03.16.
 */
public class SportSticker extends CardView {

    private class SportInfosLayout extends LinearLayout {
        SportSticker card;
        SportInfosLayout(Context context, SportSticker card){
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
    private Sport sport;
    private LinearLayout cardLayout;
    private LinearLayout sportInfosLayout;
    private LinearLayout textLayout;
    private LinearLayout iconsLayout;
    private SportList container;
    boolean removable; boolean addable;

    public SportSticker(Context context, Sport sport, SportList container){
        this(context, sport, container, false, false);
    }

    public SportSticker(Context context, Sport sport, SportList container,
                             boolean removable, boolean addable){
        super(context);
        this.container = container;
        setSport(sport, removable, addable);
    }

    public void setSport(Sport sport, boolean removable,
                              boolean addable){
        this.sport = sport;
        this.removable = removable; this.addable = addable;
        initCard();
        initTexts();
    }

    private void initCard(){
        LinearLayout.LayoutParams cardParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        this.setLayoutParams(cardParams);

        cardLayout = new LinearLayout(getContext());
        cardLayout.setOrientation(LinearLayout.HORIZONTAL);
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        cardLayout.setLayoutParams(layoutParams);
        cardLayout.setPadding(CARD_PADDING, CARD_PADDING, CARD_PADDING, CARD_PADDING);
        this.addView(cardLayout);

        sportInfosLayout = new SportInfosLayout(getContext(), this);
        sportInfosLayout.setOrientation(LinearLayout.HORIZONTAL);
        LinearLayout.LayoutParams infosLayoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        infosLayoutParams.setMargins(NOT_ICON_MARGIN, NOT_ICON_MARGIN, NOT_ICON_MARGIN, NOT_ICON_MARGIN);
        sportInfosLayout.setLayoutParams(infosLayoutParams);
        cardLayout.addView(sportInfosLayout);

        iconsLayout = null; // Will be initialized in initActions()
    }

    private void initTexts(){
        textLayout = new LinearLayout(getContext());
        LinearLayout.LayoutParams textContParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
        textLayout.setLayoutParams(textContParams);
        textLayout.setOrientation(LinearLayout.VERTICAL);
        textLayout.setGravity(Gravity.CENTER_VERTICAL);

        TextView mainText = new TextView(getContext());
        LinearLayout.LayoutParams mainTextParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        mainText.setLayoutParams(mainTextParams);
        mainText.setTextSize(MAIN_TEXT_SIZE);
        mainText.setTextColor(MAIN_TEXT_COLOR);
        mainText.setText(sport.getName());
        mainText.setMaxLines(MAIN_TEXT_MAX_LINES);
        mainText.canScrollHorizontally(LinearLayout.HORIZONTAL);
        mainText.setEllipsize(TextUtils.TruncateAt.END);
        textLayout.addView(mainText);


        String energyInfos = getEnergyInfos();
        if(! energyInfos.isEmpty()) {
            TextView secondaryText = new TextView(getContext());
            LinearLayout.LayoutParams secondaryTextParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            secondaryText.setLayoutParams(secondaryTextParams);
            secondaryText.setTextSize(SECONDARY_TEXT_SIZE);
            secondaryText.setTextColor(SECONDARY_TEXT_COLOR);
            secondaryText.setText(energyInfos);
            secondaryText.setMaxLines(SECONDARY_TEXT_MAX_LINES);
            secondaryText.canScrollHorizontally(LinearLayout.HORIZONTAL);
            secondaryText.setEllipsize(TextUtils.TruncateAt.END);
            textLayout.addView(secondaryText);
        }
        sportInfosLayout.addView(textLayout);
    }

    private void initIconsLayout(int w){
        sportInfosLayout.getLayoutParams().width = w - ICON_SIZE; // Crucial - makes space on the card for the icon layout
        iconsLayout = new LinearLayout(getContext());
        iconsLayout.setOrientation(LinearLayout.VERTICAL);
        LinearLayout.LayoutParams iconsLayoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.MATCH_PARENT);
        iconsLayout.setLayoutParams(iconsLayoutParams);
        cardLayout.addView(iconsLayout);

    }

    private void initActions(int w, int h){
        if(iconsLayout != null) return;
        /* This method will only be called once we know exactly the width of the itemInfosLayout.
         * As soon as we know it, we can resize it in order to fit the iconLayout onto the card. */
        if(removable || addable) {
            initIconsLayout(w);
            List<View> icons = new ArrayList<>();
            icons.add((removable) ? initClearAction() : initEmptyAction());
            icons.add((addable) ? initAddAction() : initEmptyAction());
            distributeSpace(icons, iconsLayout);
        }
    }

    private View initEmptyAction(){
        View emptyView = new View(getContext());
        emptyView.setLayoutParams(new LinearLayoutCompat.LayoutParams(ICON_SIZE, ICON_SIZE));
        return emptyView;
    }

    private void distributeSpace(List<View> views, LinearLayout parent){
        if(!views.isEmpty()) {
            for(int i = 0; i < views.size(); ++i) {
                parent.addView(views.get(i));
                if(i != views.size() - 1)
                    parent.addView(new EvenSpaceView(getContext()));
            }
        }
    }

    private View initClearAction(){
        ImageView clearIcon = new ImageView(getContext());
        clearIcon.setLayoutParams(new LinearLayoutCompat.LayoutParams(ICON_SIZE, ICON_SIZE));
        clearIcon.setImageResource(DELETE_ICON);
        clearIcon.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                container.onRemoveSport(sport);
            }
        });
        return clearIcon;
    }

    private View initAddAction(){
        ImageView addIcon = new ImageView(getContext());
        addIcon.setLayoutParams(new LinearLayoutCompat.LayoutParams(ICON_SIZE, ICON_SIZE));
        addIcon.setImageResource(ADD_ICON);
        return addIcon;
    }

    private String getEnergyInfos(){
        String infos = "Durée: ";
        infos += Integer.toString(sport.getDuration());
        infos += " mins - ";
        infos += Integer.toString(Math.round(sport.getEnergyConsumed()/CAL_TO_JOULE_FACTOR)) + " " + CALORIES_UNIT;
        return infos;
    }
}
