package org.calorycounter.shared.models;

import org.json.simple.JSONObject;
import static org.calorycounter.shared.Constants.network.*;

public class Food extends EdibleItem{

    @Override
    public boolean equals(Object other){
        if(this == other) return true;
        if(!(other instanceof Food)) return false;

        final Food otherFood = (Food) other;
        if(this.getId().equals(otherFood.getId())) return true;
        else return false;
    }

    @Override
    public int hashCode(){
        return this.getId().hashCode();
    }

    @Override
    public JSONObject toJSON(){
        JSONObject repr = new JSONObject();
        repr.put(FOOD_ID, id);
        repr.put(FOOD_URL, url);
        repr.put(FOOD_CODE, code);
        
        repr.put(FOOD_NAME, getProductName());
        repr.put(FOOD_IMAGE_URL, image_url);
        repr.put(FOOD_TOTAL_ENERGY, total_energy);
        repr.put(FOOD_TOTAL_FAT, total_fat);
        repr.put(FOOD_TOTAL_PROTEINS,total_proteins);
        repr.put(FOOD_TOTAL_SATURATED_FAT, total_saturated_fat);
        repr.put(FOOD_TOTAL_CARBOHYDRATES, total_carbohydrates);
        repr.put(FOOD_TOTAL_SUGARS, total_sugars);
        repr.put(FOOD_TOTAL_SODIUM, total_sodium);
        repr.put(FOOD_QUANTITY, quantity);
        return repr;
    }

    @Override
    public void initFromJSON(JSONObject obj){
        this.id = (Long) obj.get(FOOD_ID);
        this.url = (String) obj.get(FOOD_URL);
        this.code = (String) obj.get(FOOD_CODE);
        this.productName = (String) obj.get(FOOD_NAME);
        this.image_url = (String) obj.get(FOOD_IMAGE_URL);
        this.total_energy = (Float) obj.get(FOOD_TOTAL_ENERGY);
        this.total_fat = (Float) obj.get(FOOD_TOTAL_FAT);
        this.total_proteins = (Float) obj.get(FOOD_TOTAL_PROTEINS);
        this.total_saturated_fat = (Float) obj.get(FOOD_TOTAL_SATURATED_FAT);
        this.total_carbohydrates = (Float) obj.get(FOOD_TOTAL_CARBOHYDRATES);
        this.total_sugars = (Float) obj.get(FOOD_TOTAL_SUGARS);
        this.total_sodium = (Float) obj.get(FOOD_TOTAL_SODIUM);
        this.quantity = (String) obj.get(FOOD_QUANTITY);
    }
}