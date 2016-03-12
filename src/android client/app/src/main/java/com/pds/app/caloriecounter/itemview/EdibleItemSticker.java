package com.pds.app.caloriecounter.itemview;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutCompat;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.pds.app.caloriecounter.R;
import com.pds.app.caloriecounter.utils.Converter;
import com.pds.app.caloriecounter.utils.EvenSpaceView;

import org.calorycounter.shared.models.EdibleItem;
import org.calorycounter.shared.models.EdibleItemImage;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import static com.pds.app.caloriecounter.GraphicsConstants.ItemSticker.*;
import static com.pds.app.caloriecounter.GraphicsConstants.Global.*;
import static org.calorycounter.shared.Constants.network.CAL_TO_JOULE_FACTOR;

/**
 * Created by jhellinckx on 05/03/16.
 */
public class EdibleItemSticker extends CardView {
    private class EdibleItemInfosLayout extends LinearLayout{
        EdibleItemSticker card;
        EdibleItemInfosLayout(Context context, EdibleItemSticker card){
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
    private EdibleItem item;
    private LinearLayout cardLayout;
    private CircleImageView cardImage;
    private LinearLayout textLayout;
    private LinearLayout itemInfosLayout;
    private LinearLayout iconsRightLayout;
    private LinearLayout iconsLeftLayout;
    private EdibleItemList container;
    boolean removable; boolean addable;
    boolean ratable; boolean checkable;
    boolean expandable;


    public EdibleItemSticker(Context context, EdibleItem item, EdibleItemList container){
        this(context, item, container, false, false, false, false, false);
    }

    public EdibleItemSticker(Context context, EdibleItem item, EdibleItemList container,
                             boolean removable, boolean addable,
                             boolean ratable, boolean expandable, boolean  checkable){
        super(context);
        this.container = container;
        setEdibleItem(item, removable, addable, ratable, expandable, checkable);
    }

    public void setEdibleItem(EdibleItem item, boolean removable,
                              boolean addable, boolean ratable, boolean expandable, boolean checkable){
        this.item = item;
        this.removable = removable; this.addable = addable;
        this.ratable = ratable; this.expandable = expandable;
        this.checkable = checkable;
        initCard();
        initImage();
        initTexts();
    }

    private void initCard(){
        this.setPreventCornerOverlap(false);
        LinearLayout.LayoutParams cardParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        this.setLayoutParams(cardParams);
        cardLayout = new LinearLayout(getContext());
        cardLayout.setOrientation(LinearLayout.HORIZONTAL);
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        cardLayout.setLayoutParams(layoutParams);
        cardLayout.setPadding(CARD_PADDING, CARD_PADDING, CARD_PADDING, CARD_PADDING);
        if(item.isEaten()){
            cardLayout.setBackgroundColor(getResources().getColor(R.color.primary));
        }
        this.addView(cardLayout);

        itemInfosLayout = new EdibleItemInfosLayout(getContext(), this);
        itemInfosLayout.setOrientation(LinearLayout.HORIZONTAL);
        LinearLayout.LayoutParams infosLayoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        infosLayoutParams.setMargins(NOT_ICON_MARGIN, NOT_ICON_MARGIN, NOT_ICON_MARGIN, NOT_ICON_MARGIN);
        itemInfosLayout.setLayoutParams(infosLayoutParams);
        cardLayout.addView(itemInfosLayout);

        iconsRightLayout = null; // Will be initialized in initActions()
        iconsLeftLayout = null;
    }

    private void initImage(){
        cardImage = new CircleImageView(getContext());
        EdibleItemImage pic = item.getImagePic();
        byte[] img_bytes = pic.getImageBytesArray();
        Bitmap bmp;
        bmp = BitmapFactory.decodeByteArray(img_bytes, 0, img_bytes.length);
        cardImage.setImageBitmap(bmp);
        LinearLayout.LayoutParams imageParams = new LinearLayout.LayoutParams(IMAGE_WIDTH, IMAGE_HEIGHT);
        imageParams.setMargins(0, 0, SPACE_BETWEEN_IMAGE_AND_TEXT, 0);
        cardImage.setLayoutParams(imageParams);
        cardImage.setBorderColor(IMAGE_BORDER_COLOR);
        cardImage.setBorderWidth(IMAGE_BORDER_WIDTH);
        itemInfosLayout.addView(cardImage);
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
        mainText.setText(item.getProductName());
        mainText.setMaxLines(MAIN_TEXT_MAX_LINES);
        mainText.canScrollHorizontally(LinearLayout.HORIZONTAL);
        mainText.setEllipsize(TextUtils.TruncateAt.END);
        textLayout.addView(mainText);

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
            textLayout.addView(secondaryText);
        }
        itemInfosLayout.addView(textLayout);
    }

    private void initIconsLayout(int w, boolean addLeft, boolean addRight){
        itemInfosLayout.getLayoutParams().width = w - (((addLeft?1:0)+(addRight?1:0))*ICON_SIZE); // Crucial - makes space on the card for the icon layout
        if(addLeft){
            iconsLeftLayout = new LinearLayout(getContext());
            iconsLeftLayout.setOrientation(LinearLayout.VERTICAL);
            LinearLayout.LayoutParams iconsLayoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.MATCH_PARENT);
            iconsLeftLayout.setLayoutParams(iconsLayoutParams);
            cardLayout.removeView(itemInfosLayout);
            cardLayout.addView(iconsLeftLayout);
            cardLayout.addView(itemInfosLayout);
        }
        if(addRight) {
            iconsRightLayout = new LinearLayout(getContext());
            iconsRightLayout.setOrientation(LinearLayout.VERTICAL);
            LinearLayout.LayoutParams iconsLayoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.MATCH_PARENT);
            iconsRightLayout.setLayoutParams(iconsLayoutParams);
            cardLayout.addView(iconsRightLayout);
        }

    }

    private void initActions(int w, int h){
        if(iconsRightLayout != null || iconsLeftLayout != null) return;
        /* This method will only be called once we know exactly the width of the itemInfosLayout.
         * As soon as we know it, we can resize it in order to fit the iconLayout onto the card. */
        if(removable || addable || checkable || ratable || expandable) {
            boolean addRightLayout = removable || addable || ratable;
            boolean addLeftLayout = expandable;
            initIconsLayout(w, addLeftLayout, addRightLayout);
            if(addRightLayout){
                List<View> rightIcons = new ArrayList<>();
                rightIcons.add((removable) ? initClearAction() : initEmptyAction());
                rightIcons.add((addable) ? initAddAction() : initEmptyAction());
                rightIcons.add((ratable) ? initRateAction() : initEmptyAction());
                distributeSpace(rightIcons, iconsRightLayout);
            }
            if(addLeftLayout){
                List<View> leftIcons = new ArrayList<>();
                leftIcons.add((expandable) ? initZoomAction() : initEmptyAction());
                distributeSpace(leftIcons, iconsLeftLayout);
            }
            if (checkable) {
                initCheckAction();
            }
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
                container.onRemoveItem(item);
            }
        });
        return clearIcon;
    }

    private View initAddAction(){
        ImageView addIcon = new ImageView(getContext());
        addIcon.setLayoutParams(new LinearLayoutCompat.LayoutParams(ICON_SIZE, ICON_SIZE));
        addIcon.setImageResource(ADD_ICON);
        addIcon.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                container.onAddItem(item);
            }
        });
        return addIcon;
    }

    private View initZoomAction(){
        ImageView zoomIcon = new ImageView(getContext());
        zoomIcon.setLayoutParams(new LinearLayoutCompat.LayoutParams(ICON_SIZE, ICON_SIZE));
        zoomIcon.setImageResource(ZOOM_ICON);
        zoomIcon.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                container.onExpandItem(item);
            }
        });
        return zoomIcon;
    }

    private void initCheckAction(){
        this.setClickable(true);
        this.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (item.isEaten()) {
                    item.notEaten();
                    cardLayout.setBackgroundColor(getResources().getColor(R.color.cardview_light_background));
                } else {
                    item.eaten();
                    cardLayout.setBackgroundColor(getResources().getColor(R.color.primary));
                }
                container.onCheckItem(item);
            }
        });
    }

    private View initRateAction(){
        ImageView rateIcon = new ImageView(getContext());
        rateIcon.setLayoutParams(new LinearLayoutCompat.LayoutParams(ICON_SIZE, ICON_SIZE));
        rateIcon.setImageResource(RATE_ICON);
        rateIcon.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                container.onRateItem(item);
            }
        });
        return rateIcon;
    }

    private String getNutrInfos(){
        String infos = "";
        if(item.getTotalEnergy() != null)
            infos += Integer.toString(Math.round(item.getTotalEnergy()/CAL_TO_JOULE_FACTOR)) + " " + CALORIES_UNIT + ", ";
        if(item.getTotalProteins() != null)
            infos += Converter.floatToString(item.getTotalProteins()) + " " + DEFAULT_UNIT + " " + TITLE_PROTEINS.toLowerCase() + ", ";
        if(item.getTotalCarbohydrates() != null)
            infos += Converter.floatToString(item.getTotalCarbohydrates()) + " " + DEFAULT_UNIT + " " + TITLE_CARBO.toLowerCase() + ", ";
        if(! infos.isEmpty())
            infos = new String(infos.substring(0, infos.length() - 2));
        return infos;
    }
}