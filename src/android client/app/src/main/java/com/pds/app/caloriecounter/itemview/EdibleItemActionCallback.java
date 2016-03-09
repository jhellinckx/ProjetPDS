package com.pds.app.caloriecounter.itemview;

import org.calorycounter.shared.models.EdibleItem;

/**
 * Created by jhellinckx on 06/03/16.
 */
public interface EdibleItemActionCallback {
    public void onRemoveEdibleItem(EdibleItem item);
    public void onAddEdibleItem(EdibleItem item);
    public void onRateEdibleItem(EdibleItem item);
    public void onCheckEdibleItem(EdibleItem item);
    public void onExpandEdibleItem(EdibleItem item);
}
