package com.pds.app.caloriecounter.itemview;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapShader;
import android.graphics.Shader;
import android.graphics.drawable.ClipDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.RoundRectShape;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutCompat;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;

import com.pds.app.caloriecounter.R;
import com.pds.app.caloriecounter.utils.EvenSpaceView;

import org.calorycounter.shared.models.EdibleItem;
import org.calorycounter.shared.models.EdibleItemImage;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

import static com.pds.app.caloriecounter.GraphicsConstants.ItemSticker.ADD_ICON;
import static com.pds.app.caloriecounter.GraphicsConstants.ItemSticker.CARD_PADDING;
import static com.pds.app.caloriecounter.GraphicsConstants.ItemSticker.DELETE_ICON;
import static com.pds.app.caloriecounter.GraphicsConstants.ItemSticker.ICON_SIZE;
import static com.pds.app.caloriecounter.GraphicsConstants.ItemSticker.IMAGE_BORDER_COLOR;
import static com.pds.app.caloriecounter.GraphicsConstants.ItemSticker.IMAGE_BORDER_WIDTH;
import static com.pds.app.caloriecounter.GraphicsConstants.ItemSticker.IMAGE_HEIGHT;
import static com.pds.app.caloriecounter.GraphicsConstants.ItemSticker.IMAGE_WIDTH;
import static com.pds.app.caloriecounter.GraphicsConstants.ItemSticker.MAIN_TEXT_COLOR;
import static com.pds.app.caloriecounter.GraphicsConstants.ItemSticker.MAIN_TEXT_MAX_LINES;
import static com.pds.app.caloriecounter.GraphicsConstants.ItemSticker.MAIN_TEXT_SIZE;
import static com.pds.app.caloriecounter.GraphicsConstants.ItemSticker.NOT_ICON_MARGIN;
import static com.pds.app.caloriecounter.GraphicsConstants.ItemSticker.RATE_ICON;
import static com.pds.app.caloriecounter.GraphicsConstants.ItemSticker.SECONDARY_TEXT_COLOR;
import static com.pds.app.caloriecounter.GraphicsConstants.ItemSticker.SECONDARY_TEXT_MAX_LINES;
import static com.pds.app.caloriecounter.GraphicsConstants.ItemSticker.SECONDARY_TEXT_SIZE;
import static com.pds.app.caloriecounter.GraphicsConstants.ItemSticker.SPACE_BETWEEN_IMAGE_AND_TEXT;
import static com.pds.app.caloriecounter.GraphicsConstants.ItemSticker.ZOOM_ICON;

/**
 * Created by mrmmtb on 10.03.16.
 */
public class RatingEdibleItemSticker extends CardView {
    private class EdibleItemInfosLayout extends LinearLayout {
        RatingEdibleItemSticker card;
        EdibleItemInfosLayout(Context context, RatingEdibleItemSticker card){
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
    private LinearLayout iconsLayout;
    private RatingEdibleItemList container;
    private RatingBar secondaryRatingBar;
    private View ratingBarView;
    private boolean ratingBarAlreadyRated = false;
    boolean removable; boolean addable;
    boolean ratable; boolean checkable;
    boolean expandable;


    public RatingEdibleItemSticker(Context context, EdibleItem item, RatingEdibleItemList container){
        this(context, item, container, false, false, false, false, false);
    }

    public RatingEdibleItemSticker(Context context, EdibleItem item, RatingEdibleItemList container,
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

        iconsLayout = null; // Will be initialized in initActions()
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

        final LayoutInflater layoutInflater = (LayoutInflater) getContext()
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        ratingBarView = layoutInflater.inflate(R.layout.tiny_rating_bar, null);
        secondaryRatingBar = (RatingBar) ratingBarView.findViewById(R.id.ratingBar);

        itemInfosLayout.addView(textLayout);
    }

    private void initIconsLayout(int w){
        itemInfosLayout.getLayoutParams().width = w - ICON_SIZE; // Crucial - makes space on the card for the icon layout
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
        if(removable || addable || checkable || ratable || expandable) {
            initIconsLayout(w);
            List<View> icons = new ArrayList<>();
            icons.add((removable) ? initClearAction() : initEmptyAction());
            icons.add((expandable) ? initZoomAction() : initEmptyAction());
            icons.add((addable) ? initAddAction() : initEmptyAction());
            icons.add((ratable) ? initRateAction() : initEmptyAction());
            if (checkable) {
                initCheckAction();
            }
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
        clearIcon.setOnClickListener(new View.OnClickListener() {
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
        addIcon.setOnClickListener(new View.OnClickListener() {
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
        zoomIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                container.onExpandItem(item);
            }
        });
        return zoomIcon;
    }

    private void initCheckAction(){
        cardLayout.setClickable(true);
        cardLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (item.isEaten()) {
                    item.notEaten();
                    cardLayout.setBackgroundColor(getResources().getColor(R.color.cardview_light_background));
                }else {
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
        rateIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                container.onRateItem(item);
            }
        });
        return rateIcon;
    }

    public void setRatingBar(float rating){
        if(!ratingBarAlreadyRated){
            textLayout.addView(ratingBarView);
            ratingBarAlreadyRated = true;
        }
        secondaryRatingBar.setRating(rating);

    }
}
