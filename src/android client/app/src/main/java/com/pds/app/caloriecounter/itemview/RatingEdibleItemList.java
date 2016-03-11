package com.pds.app.caloriecounter.itemview;

import android.content.Context;
import android.view.View;
import android.widget.LinearLayout;

import org.calorycounter.shared.models.EdibleItem;
import org.calorycounter.shared.models.Food;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static com.pds.app.caloriecounter.GraphicsConstants.Global.MAP_LOAD_FACTOR;
import static com.pds.app.caloriecounter.GraphicsConstants.ItemList.FLAG_ADDABLE;
import static com.pds.app.caloriecounter.GraphicsConstants.ItemList.FLAG_CHECKABLE;
import static com.pds.app.caloriecounter.GraphicsConstants.ItemList.FLAG_EXPANDABLE;
import static com.pds.app.caloriecounter.GraphicsConstants.ItemList.FLAG_RATABLE;
import static com.pds.app.caloriecounter.GraphicsConstants.ItemList.FLAG_REMOVABLE;

/**
 * Created by mrmmtb on 10.03.16.
 */
public class RatingEdibleItemList extends LinearLayout {
    private Map<EdibleItem, View> itemViewMap;
    private EdibleItemActionCallback actionCallback;
    private List<EdibleItem> items;
    boolean removable = false; boolean addable = false;
    boolean ratable = false; boolean expandable = false;
    boolean checkable = false;

    public RatingEdibleItemList(Context context, List<EdibleItem> givenItems, EdibleItemActionCallback actionCallback, int... flags){
        super(context);
        this.actionCallback = actionCallback;
        this.items = givenItems;
        initLayout();
        initItems(flags);
    }

    private void initLayout(){
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        this.setLayoutParams(params);
        this.setOrientation(LinearLayout.VERTICAL);
    }

    private void initItems( int... flags){

        for(int flag : flags){
            if(flag == FLAG_REMOVABLE) removable = true;
            else if(flag == FLAG_ADDABLE) addable = true;
            else if(flag == FLAG_RATABLE) ratable = true;
            else if(flag == FLAG_EXPANDABLE) expandable = true;
            else if(flag == FLAG_CHECKABLE) checkable = true;
        }
        this.itemViewMap = new LinkedHashMap<>(items.size(), MAP_LOAD_FACTOR);
        List<EdibleItem> checkedItems = new ArrayList<EdibleItem>();
        for(EdibleItem item : items){
            if(item.isEaten()){
                checkedItems.add(item);
            }else {
                View sticker = new RatingEdibleItemSticker(getContext(), item, this, removable, addable, ratable, expandable, checkable);
                this.itemViewMap.put(item, sticker);
                this.addView(sticker);
            }
        }
        for(EdibleItem checkedItem: checkedItems){
            View sticker = new RatingEdibleItemSticker(getContext(), checkedItem, this, removable, addable, ratable, expandable, checkable);
            this.itemViewMap.put(checkedItem, sticker);
            this.addView(sticker);
        }
    }

    public void onRemoveItem(EdibleItem item){
        this.removeView(itemViewMap.get(item));
        itemViewMap.remove(item);
        actionCallback.onRemoveEdibleItem(item);
    }

    public void onAddItem(EdibleItem item){
        //makeAddTest();
        actionCallback.onAddEdibleItem(item);

    }

    public void onExpandItem(EdibleItem item){
        actionCallback.onExpandEdibleItem(item);
    }

    public void makeAddTest(){
        EdibleItem item = new Food();
        item.setImageUrl("https://colruyt.collectandgo.be/cogo/step/JPG/JPG/500x500/std.lang.all/41/55/asset-834155.jpg");
        item.setProductName("Test add");
        item.setTotalCarbohydrates(119.4f);
        item.setTotalEnergy(1000f);
        item.setTotalProteins(31f);
        item.setId(1000L);

        View sticker = new RatingEdibleItemSticker(getContext(), item, this, removable, addable, ratable, expandable, checkable);
        this.itemViewMap.put(item, sticker);
        this.addView(sticker);
    }

    public void onRateItem(EdibleItem item){
        actionCallback.onRateEdibleItem(item);
    }

    public void onCheckItem(EdibleItem item){
        this.removeAllViews();
        initItems();
        actionCallback.onCheckEdibleItem(item);
    }

}

