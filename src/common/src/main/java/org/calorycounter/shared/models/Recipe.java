package org.calorycounter.shared.models;

import static org.calorycounter.shared.Constants.network.*;
import org.json.simple.JSONObject;

public class Recipe extends EdibleItem {
	private String ingredients;


	public String getIngredients(){
		return ingredients;
	}

	public void setIngredients(String ingren){
		this.ingredients = ingren;
	}

	@Override
    public JSONObject toJSON(){
        JSONObject repr = new JSONObject();
        repr = super.toJSON();
        repr.put(INGREDIENTS_LIST, ingredients);
        return repr;
    }

    @Override
    public JSONObject toJSON(boolean noImage){
        JSONObject repr = new JSONObject();
        repr = super.toJSON(noImage);
        repr.put(INGREDIENTS_LIST, ingredients);
        return repr;
    }

    @Override
    public void initFromJSON(JSONObject obj){
        super.initFromJSON(obj);
        this.ingredients = (String) obj.get(INGREDIENTS_LIST);
    }
	
}