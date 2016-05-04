package org.calorycounter.shared.models;

import static org.calorycounter.shared.Constants.network.*;
import org.json.simple.JSONObject;
import java.util.List;

public class Recipe extends EdibleItem {
	private String ingredients;
    private int subCategoryId;
    private List<Integer> ingredientsIds;
    private List<Integer> tagIds;
    private int originId;



	public String getIngredients(){
		return ingredients;
	}

	public void setIngredients(String ingren){
		this.ingredients = ingren;
	}

    public void setSubCategoryId(int subCatId){
        this.subCategoryId = subCatId;
    }

    public int getSubCategoryId(){
        return subCategoryId;
    }

    public List<Integer> getIngredientsIds() {
        return ingredientsIds;
    }

    public void setIngredientsIds(List<Integer> ingIds){
        this.ingredientsIds = ingIds;
    }

    public List<Integer> getTagIds() {
        return tagIds;
    }

    public void setTagIds(List<Integer> tagIds){
        this.tagIds = tagIds;
    }

    public int getOriginId(){ //If origin_id = 0 , means NO ORIGIN
        return originId;
    }

    public void setOriginId(int orgId){
        this.originId = orgId;
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

    @Override
    public boolean equals(Object other){
        if(this == other) return true;
        if(!(other instanceof Recipe)) return false;

        final Recipe otherRecipe = (Recipe) other;
        if(this.getId().equals(otherRecipe.getId())) return true;
        else return false;
    }

    @Override
    public int hashCode(){
        return this.getId().hashCode();
    }
	
}