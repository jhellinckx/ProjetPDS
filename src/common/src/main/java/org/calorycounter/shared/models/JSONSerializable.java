package org.calorycounter.shared.models;

import org.json.simple.JSONObject;


public interface JSONSerializable {
	public JSONObject toJSON();
    public JSONObject toJSON(boolean noImage);
    public void initFromJSON(JSONObject obj);
}
