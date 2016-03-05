package org.calorycounter.shared.models;

import static org.calorycounter.shared.Constants.network.*;
import org.calorycounter.shared.Constants;
import org.json.simple.JSONObject;
import java.io.UnsupportedEncodingException;


public abstract class EdibleItem implements JSONSerializable{
	protected Long      id;
    protected String    url;
    protected String    productName;
    protected String    image_url;
    protected Float    total_energy;
    protected Float    total_fat;
    protected Float    total_proteins;
    protected Float    total_saturated_fat;
    protected Float    total_carbohydrates;
    protected Float    total_sugars;
    protected Float    total_sodium;
    protected String   quantity;
    
    public Long getId() {
        return id;
    }
    public void setId( Long id ) {
        this.id = id;
    }
    
    public String getUrl() {
        return url;
    }
    public void setUrl( String url ) {
        this.url = url;
    }
    
    public String getProductName() {
        return productName;
    }
    public void setProductName( String productName ) {
        this.productName = productName;
    }

    public String getImageUrl() {
        return image_url;
    }
    public void setImageUrl( String image_url ) {
        this.image_url = image_url;
    }

    public Float getTotalEnergy() {
        return total_energy;
    }
    public void setTotalEnergy( Float total_en ) {
        this.total_energy = total_en;
    }

    public Float getTotalFat() {
        return total_fat;
    }
    public void setTotalFat( Float total_fa ) {
        this.total_fat = total_fa;
    }

    public Float getTotalProteins() {
        return total_proteins;
    }
    public void setTotalProteins( Float total_pro ) {
        this.total_proteins = total_pro;
    }

    public Float getTotalSaturatedFat() {
        return total_saturated_fat;
    }
    public void setTotalSaturatedFat( Float total_sat ) {
        this.total_saturated_fat= total_sat;
    }

    public Float getTotalCarbohydrates() {
        return total_carbohydrates;
    }
    public void setTotalCarbohydrates( Float total_car ) {
        this.total_carbohydrates = total_car;
    }

    public Float getTotalSugars() {
        return total_sugars;
    }
    public void setTotalSugars( Float total_su ) {
        this.total_sugars = total_su;
    }

    public Float getTotalSodium() {
        return total_sodium;
    }
    public void setTotalSodium( Float total_so ) {
        this.total_sodium = total_so;
    }

    public String getQuantity(){
        return quantity;
    }

    public void setQuantity(String quant){
        this.quantity = quant;
    }

    public String toString() {
        return getProductName();
    }

    @Override
    public JSONObject toJSON(){
        JSONObject repr = new JSONObject();
        repr.put(FOOD_ID, id);
        repr.put(FOOD_URL, url);
        
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