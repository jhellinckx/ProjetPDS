package com.pds.app.caloriecounter.itemview;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutCompat;
import android.text.InputType;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.pds.app.caloriecounter.utils.EvenSpaceView;

import java.util.ArrayList;
import java.util.List;

import static com.pds.app.caloriecounter.GraphicsConstants.ItemSticker.ADD_ICON;
import static com.pds.app.caloriecounter.GraphicsConstants.ItemSticker.CARD_PADDING;
import static com.pds.app.caloriecounter.GraphicsConstants.ItemSticker.DELETE_ICON;
import static com.pds.app.caloriecounter.GraphicsConstants.ItemSticker.ICON_SIZE;
import static com.pds.app.caloriecounter.GraphicsConstants.ItemSticker.MAIN_TEXT_COLOR;
import static com.pds.app.caloriecounter.GraphicsConstants.ItemSticker.MAIN_TEXT_SIZE;
import static com.pds.app.caloriecounter.GraphicsConstants.ItemSticker.NOT_ICON_MARGIN;
import static com.pds.app.caloriecounter.GraphicsConstants.ItemSticker.SECONDARY_TEXT_COLOR;
import static com.pds.app.caloriecounter.GraphicsConstants.ItemSticker.SECONDARY_TEXT_MAX_LINES;
import static com.pds.app.caloriecounter.GraphicsConstants.ItemSticker.SECONDARY_TEXT_SIZE;

/**
 * Created by mrmmtb on 06.03.16.
 */
public class AddSportSticker extends CardView {

    private class addSportInfosLayout extends LinearLayout {
        AddSportSticker card;
        addSportInfosLayout(Context context, AddSportSticker card){
            super(context);
            this.card = card;
        }

        @Override
        protected void onSizeChanged(int w, int h, int oldw, int oldh){
            if(oldw == 0) {
                Log.d("CALLING INIT ACTIONS :", Integer.toString(oldw) + " -> " + Integer.toString(w));
                card.initActions(w, h);
                post(new Runnable() {
                    public void run() {
                        requestLayout();
                    }
                });
            }
        }
    }
    private LinearLayout cardLayout;
    private LinearLayout addSportInfosLayout;
    private LinearLayout textLayout;
    private LinearLayout iconsLayout;
    private SportList container;
    private AutoCompleteTextView _autoComplete;
    private ArrayAdapter<String> _adapter;
    private EditText secondaryEditText;
    private static List<String> _sportNames;
    boolean removable; boolean addable;

    public AddSportSticker(Context context, SportList container, List<String> sportNames){
        this(context, container, sportNames, false, false);
    }

    public AddSportSticker(Context context, SportList container, List<String> sportNames,
                        boolean removable, boolean addable){
        super(context);
        this.container = container;
        this._sportNames = sportNames;
        setSport(removable, addable);
    }

    public void setSport(boolean removable,
                         boolean addable){
        this.removable = removable; this.addable = addable;
        initCard();
        initTextsSelections();
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

        addSportInfosLayout = new addSportInfosLayout(getContext(), this);
        addSportInfosLayout.setOrientation(LinearLayout.HORIZONTAL);
        LinearLayout.LayoutParams infosLayoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        infosLayoutParams.setMargins(NOT_ICON_MARGIN, NOT_ICON_MARGIN, NOT_ICON_MARGIN, NOT_ICON_MARGIN);
        addSportInfosLayout.setLayoutParams(infosLayoutParams);
        cardLayout.addView(addSportInfosLayout);

        iconsLayout = null; // Will be initialized in initActions()
    }

    private void initTextsSelections() {
        textLayout = new LinearLayout(getContext());
        LinearLayout.LayoutParams textContParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
        textLayout.setLayoutParams(textContParams);
        textLayout.setOrientation(LinearLayout.VERTICAL);
        textLayout.setGravity(Gravity.CENTER_VERTICAL);

        _autoComplete = new AutoCompleteTextView(getContext());
        LinearLayout.LayoutParams mainTextParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        _autoComplete.setLayoutParams(mainTextParams);
        //String[] foo = new String[] { "Tennis" };
        _adapter = new ArrayAdapter<String>(getContext(),android.R.layout.select_dialog_item,_sportNames);
        _autoComplete.setAdapter(_adapter);
        _autoComplete.setTextSize(MAIN_TEXT_SIZE);
        _autoComplete.setTextColor(MAIN_TEXT_COLOR);
        _autoComplete.setHint("Entrez un sport");
        _autoComplete.setThreshold(0);
        _autoComplete.setVisibility(View.VISIBLE);
        _autoComplete.setEllipsize(TextUtils.TruncateAt.END);
        textLayout.addView(_autoComplete);

        LinearLayout secondaryTextLayout = new LinearLayout(getContext());
        LinearLayout.LayoutParams secondaryTextContParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
        secondaryTextLayout.setLayoutParams(secondaryTextContParams);
        secondaryTextLayout.setOrientation(LinearLayout.HORIZONTAL);
        secondaryTextLayout.setGravity(Gravity.CENTER_VERTICAL);

        TextView secondaryText = new TextView(getContext());
        LinearLayout.LayoutParams secondaryTextParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        secondaryText.setLayoutParams(secondaryTextParams);
        secondaryText.setTextSize(SECONDARY_TEXT_SIZE);
        secondaryText.setTextColor(SECONDARY_TEXT_COLOR);
        secondaryText.setText("Durée: ");
        secondaryText.setMaxLines(SECONDARY_TEXT_MAX_LINES);
        secondaryText.canScrollHorizontally(LinearLayout.HORIZONTAL);
        secondaryText.setEllipsize(TextUtils.TruncateAt.END);
        secondaryTextLayout.addView(secondaryText);

        secondaryEditText = new EditText(getContext());
        LinearLayout.LayoutParams secondaryEditTextParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        secondaryEditText.setLayoutParams(secondaryEditTextParams);
        secondaryEditText.setTextSize(SECONDARY_TEXT_SIZE);
        secondaryEditText.setTextColor(SECONDARY_TEXT_COLOR);
        secondaryEditText.setHintTextColor(SECONDARY_TEXT_COLOR);
        secondaryEditText.setHint("En minutes");
        secondaryEditText.setInputType(InputType.TYPE_CLASS_NUMBER);
        secondaryEditText.setMaxLines(SECONDARY_TEXT_MAX_LINES);
        secondaryEditText.canScrollHorizontally(LinearLayout.HORIZONTAL);
        secondaryEditText.setEllipsize(TextUtils.TruncateAt.END);
        secondaryTextLayout.addView(secondaryEditText);

        textLayout.addView(secondaryTextLayout);

    addSportInfosLayout.addView(textLayout);
    }

    private void initIconsLayout(int w){
        addSportInfosLayout.getLayoutParams().width = w - ICON_SIZE; // Crucial - makes space on the card for the icon layout
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
            icons.add(initEmptyAction());
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
        return clearIcon;
    }

    private boolean checkErrors() {
        Boolean res = false;
        if (_autoComplete.getText().toString().isEmpty()) {
            _autoComplete.setError("Aucun sport entré");
            res = true;
        } else if (_adapter.getCount() == 0 || _adapter.getPosition(_autoComplete.getText().toString()) == -1) {
            _autoComplete.setError("Entrez un sport valide");
            res = true;
        }
        if (secondaryEditText.getText().toString().isEmpty()) {
            secondaryEditText.setError("Aucune durée entrée");
            res = true;
        }
        return res;
    }

    private View initAddAction(){
        ImageView addIcon = new ImageView(getContext());
        addIcon.setLayoutParams(new LinearLayoutCompat.LayoutParams(ICON_SIZE, ICON_SIZE));
        addIcon.setImageResource(ADD_ICON);
        addIcon.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!checkErrors()) {
                    container.onAddSport(_autoComplete.getText().toString(), Integer.parseInt(secondaryEditText.getText().toString()));
                }
            }
        });
        return addIcon;
    }
}
