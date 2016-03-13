package org.calorycounter.shared.models;

import org.json.simple.JSONObject;
import static org.calorycounter.shared.Constants.network.*;

public class Food extends EdibleItem{

    private String    code;

    public String getCode() {
        return code;
    }
    public void setCode( String code ) {
        this.code = code;
    }

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
        repr = super.toJSON();
        repr.put(FOOD_CODE, code);
        repr.put(FOOD_TOTAL_SODIUM, total_salt);
        repr.put(FOOD_TOTAL_SUGARS, total_sugars);
        repr.put(FOOD_TOTAL_SATURATED_FAT, total_saturated_fat);
        return repr;
    }

    @Override
    public JSONObject toJSON(boolean noImage){
        JSONObject repr = new JSONObject();
        repr = super.toJSON(noImage);
        repr.put(FOOD_CODE, code);
        repr.put(FOOD_TOTAL_SODIUM, total_salt);
        repr.put(FOOD_TOTAL_SUGARS, total_sugars);
        repr.put(FOOD_TOTAL_SATURATED_FAT, total_saturated_fat);
        return repr;
    }

    @Override
    public void initFromJSON(JSONObject obj){
        super.initFromJSON(obj);
        this.code = (String) obj.get(FOOD_CODE);
    }

    public void initFloatValuesFromFloat(JSONObject obj) throws ClassCastException{        // From Android, the values send are Double, this method handles this case.
        super.initFloatValuesFromFloat(obj);
        this.total_saturated_fat = (Float) obj.get(FOOD_TOTAL_SATURATED_FAT);
        this.total_sugars = (Float) obj.get(FOOD_TOTAL_SUGARS);
        this.total_salt = (Float) obj.get(FOOD_TOTAL_SODIUM);
    }

    public void initFloatValuesFromDouble(JSONObject obj){
        super.initFloatValuesFromDouble(obj);
        this.total_saturated_fat = ((Double) obj.get(FOOD_TOTAL_SATURATED_FAT)).floatValue();
        this.total_sugars = ((Double) obj.get(FOOD_TOTAL_SUGARS)).floatValue();
        this.total_salt = ((Double) obj.get(FOOD_TOTAL_SODIUM)).floatValue();

    }
}