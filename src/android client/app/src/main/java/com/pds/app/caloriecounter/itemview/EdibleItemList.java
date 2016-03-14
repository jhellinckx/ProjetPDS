package com.pds.app.caloriecounter.itemview;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;

import com.pds.app.caloriecounter.RecommendationActivity;
import com.pds.app.caloriecounter.dayrecording.DayRecordingActivity;

import org.calorycounter.shared.models.EdibleItem;
import org.calorycounter.shared.models.Food;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static com.pds.app.caloriecounter.GraphicsConstants.ItemList.*;
import static com.pds.app.caloriecounter.GraphicsConstants.Global.*;

/**
 * Created by jhellinckx on 05/03/16.
 */
public class EdibleItemList extends LinearLayout {
    private Map<EdibleItem, List<View>> itemViewMap;
    private EdibleItemActionCallback actionCallback;
    private List<EdibleItem> items;
    boolean removable = false; boolean addable = false;
    boolean ratable = false; boolean expandable = false;
    boolean checkable = false;

    public EdibleItemList(Context context, List<EdibleItem> givenItems, EdibleItemActionCallback actionCallback, int... flags){
        super(context);
        this.actionCallback = actionCallback;
        this.items = givenItems;
        initLayout();
        initItems(flags);
    }

    private void initLayout(){
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        this.setLayoutParams(params);
        this.setOrientation(LinearLayout.VERTICAL);
    }

    private void chainingPut(EdibleItem item, View v){
        List<View> views;
        if (itemViewMap.get(item) == null){
            views = new ArrayList<>();
        }else{
            views = itemViewMap.get(item);
        }
        views.add(v);
        itemViewMap.put(item, views);
    }

    private void chainingRemove(EdibleItem item){
        if (itemViewMap.containsKey(item)){
            List<View> views = itemViewMap.get(item);
            int size = views.size();
            views.remove(size-1);
            if (size == 1){
                itemViewMap.remove(item);
            }
        }
    }

    private View chainingGet(EdibleItem item){
        if (itemViewMap.containsKey(item)){
            List<View> views = itemViewMap.get(item);
            int size = views.size();
            return views.get(size-1);
        }
        return null;
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
            if(item instanceof Food ){
                ratable=false;
            }else{
                ratable=true;
            }
            if(item.isEaten()){
                checkedItems.add(item);
            }else {

                View sticker = new EdibleItemSticker(getContext(), item, this, removable, addable, ratable, expandable, checkable);
                chainingPut(item, sticker);
                this.addView(sticker);
            }
        }
        for(EdibleItem checkedItem: checkedItems){
            View sticker = new EdibleItemSticker(getContext(), checkedItem, this, removable, addable, ratable, expandable, checkable);
            chainingPut(checkedItem, sticker);
            this.addView(sticker);
        }
    }

    public void onRemoveItem(EdibleItem item){
        this.removeView(chainingGet(item));
        chainingRemove(item);
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
        item.notEaten();

        View sticker = new EdibleItemSticker(getContext(), item, this, removable, addable, ratable, expandable, checkable);
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
