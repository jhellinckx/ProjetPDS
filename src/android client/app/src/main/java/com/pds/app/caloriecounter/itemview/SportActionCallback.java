package com.pds.app.caloriecounter.itemview;

import org.calorycounter.shared.models.Sport;

/**
 * Created by mrmmtb on 06.03.16.
 */
public interface SportActionCallback {
    public void onRemoveSport(Sport sport);
    public void onAddSport(String sportName, int duration);
}
