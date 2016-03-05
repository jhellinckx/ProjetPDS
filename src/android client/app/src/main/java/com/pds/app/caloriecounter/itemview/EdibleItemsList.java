package com.pds.app.caloriecounter.itemview;

import android.content.Context;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import org.calorycounter.shared.models.EdibleItem;

import java.util.ArrayList;
import java.util.List;
import static com.pds.app.caloriecounter.GraphicsConstants.ItemList.*;

/**
 * Created by jhellinckx on 05/03/16.
 */
public class EdibleItemsList extends LinearLayout {
    private List<EdibleItem> items;

    public EdibleItemsList(Context context, List<EdibleItem> items, int... flags){
        super(context);
        initLayout();
        initItems(items, flags);
    }

    private void initLayout(){
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        this.setLayoutParams(params);
        this.setOrientation(LinearLayout.VERTICAL);
    }

    private void initItems(List<EdibleItem> items, int... flags){
        boolean removable = false; boolean addable = false;
        boolean ratable = false; boolean expandable = false;
        for(int flag : flags){
            if(flag == FLAG_REMOVABLE) removable = true;
            else if(flag == FLAG_ADDABLE) addable = true;
            else if(flag == FLAG_RATABLE) ratable = true;
            else if(flag == FLAG_EXPANDABLE) expandable = true;
        }
        this.items = new ArrayList<>(items);
        for(EdibleItem item : items){
            this.addView(new EdibleItemSticker(getContext(), item, removable, addable, ratable, expandable));
        }


    }

}
