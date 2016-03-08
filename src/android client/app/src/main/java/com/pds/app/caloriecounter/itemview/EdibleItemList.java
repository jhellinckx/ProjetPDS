package com.pds.app.caloriecounter.itemview;

import android.content.Context;
import android.view.View;
import android.widget.LinearLayout;

import org.calorycounter.shared.models.EdibleItem;
import org.calorycounter.shared.models.Food;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static com.pds.app.caloriecounter.GraphicsConstants.ItemList.*;
import static com.pds.app.caloriecounter.GraphicsConstants.Global.*;

/**
 * Created by jhellinckx on 05/03/16.
 */
public class EdibleItemList extends LinearLayout {
    private Map<EdibleItem, View> itemViewMap;
    private EdibleItemActionCallback actionCallback;
    boolean removable = false; boolean addable = false;
    boolean ratable = false; boolean expandable = false;

    public EdibleItemList(Context context, List<EdibleItem> items, EdibleItemActionCallback actionCallback, int... flags){
        super(context);
        this.actionCallback = actionCallback;
        initLayout();
        initItems(items, flags);
    }

    private void initLayout(){
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        this.setLayoutParams(params);
        this.setOrientation(LinearLayout.VERTICAL);
    }

    private void initItems(List<EdibleItem> items, int... flags){

        for(int flag : flags){
            if(flag == FLAG_REMOVABLE) removable = true;
            else if(flag == FLAG_ADDABLE) addable = true;
            else if(flag == FLAG_RATABLE) ratable = true;
            else if(flag == FLAG_EXPANDABLE) expandable = true;
        }
        this.itemViewMap = new LinkedHashMap<>(items.size(), MAP_LOAD_FACTOR);

        for(EdibleItem item : items){
            View sticker = new EdibleItemSticker(getContext(), item, this, removable, addable, ratable, expandable);
            this.itemViewMap.put(item, sticker);
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

    public void makeAddTest(){
        EdibleItem item = new Food();
        item.setImageUrl("https://colruyt.collectandgo.be/cogo/step/JPG/JPG/500x500/std.lang.all/41/55/asset-834155.jpg");
        item.setProductName("Test add");
        item.setTotalCarbohydrates(119.4f);
        item.setTotalEnergy(1000f);
        item.setTotalProteins(31f);
        item.setId(1000L);

        View sticker = new EdibleItemSticker(getContext(), item, this, removable, addable, ratable, expandable);
        this.itemViewMap.put(item, sticker);
        this.addView(sticker);
    }

    public void onRateItem(EdibleItem item){
        actionCallback.onRateEdibleItem(item);
    }

    public void onExpandItem(EdibleItem item){
        actionCallback.onExpandEdibleItem(item);
    }

}
