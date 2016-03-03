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
        return repr;
    }

    @Override
    public void initFromJSON(JSONObject obj){
        super.initFromJSON(obj);
        this.code = (String) obj.get(FOOD_CODE);
    }
}