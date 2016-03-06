package com.pds.app.caloriecounter.itemview;

import android.content.Context;
import android.util.Log;
import android.view.View;

import org.calorycounter.shared.models.Sport;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static com.pds.app.caloriecounter.GraphicsConstants.ItemList.*;
import static com.pds.app.caloriecounter.GraphicsConstants.Global.*;

import android.widget.LinearLayout;

/**
 * Created by mrmmtb on 06.03.16.
 */
public class SportList extends LinearLayout {
    private Map<Sport, View> sportViewMap;
    private SportActionCallback actionCallback;

    public SportList(Context context, List<Sport> sports, SportActionCallback actionCallback, List<String> sportNames, int... flags){
        super(context);
        this.actionCallback = actionCallback;
        initLayout();
        initItems(sports, sportNames, flags);
    }

    private void initLayout(){
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        this.setLayoutParams(params);
        this.setOrientation(LinearLayout.VERTICAL);
    }
    private void initItems(List<Sport> sports, List<String> sportNames, int... flags){
        boolean removable = false; boolean addable = false;
        for(int flag : flags){
            if(flag == FLAG_REMOVABLE) removable = true;
            else if(flag == FLAG_ADDABLE) {
                addable = true;
            }

        }
        this.sportViewMap = new LinkedHashMap<>(sports.size(), MAP_LOAD_FACTOR);
        for(Sport sport : sports){
            View sticker = new SportSticker(getContext(), sport, this, removable, addable);
            this.sportViewMap.put(sport, sticker);
            this.addView(sticker);
        }
        AddSportSticker addSportSticker = new AddSportSticker(getContext(),this,sportNames,false,true);
        this.sportViewMap.put(new Sport(), addSportSticker);
        this.addView(addSportSticker);
    }

    public void onRemoveSport(Sport sport){
        this.removeView(sportViewMap.get(sport));
        sportViewMap.remove(sport);
        actionCallback.onRemoveSport(sport);
    }

    public void onAddSport(String sportName, int duration){
        actionCallback.onAddSport(sportName,duration);
    }
}


