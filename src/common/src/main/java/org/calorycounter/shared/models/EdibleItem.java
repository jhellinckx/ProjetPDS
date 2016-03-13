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
    protected EdibleItemImage image_pic;
    protected String    image_path;
    protected Float    total_energy;
    protected Float    total_fat;
    protected Float    total_proteins;
    protected Float    total_saturated_fat;
    protected Float    total_carbohydrates;
    protected Float    total_sugars;
    protected Float    total_salt;
    protected String   quantity;
    protected Boolean  isEaten;
    
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

    public EdibleItemImage getImagePic() {
        return image_pic;
    }

    public void setImagePic( EdibleItemImage img ){
        this.image_pic = img;
    }

    public void setImagePath( String path ){
        this.image_path = path;
    }

    public String getImagePath(){
        return image_path;
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

    public Float getTotalSalt() {
        return total_salt;
    }
    public void setTotalSalt( Float total_so ) {
        this.total_salt = total_so;
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

    public void eaten() {
        this.isEaten = true;
    }

    public void notEaten() {
        this.isEaten = false;
    }

    public Boolean isEaten() {
        return isEaten;
    }

    private JSONObject constructJSON(){
        JSONObject repr = new JSONObject();
        repr.put(FOOD_ID, id);
        repr.put(FOOD_URL, url);
        
        repr.put(FOOD_NAME, getProductName());
        repr.put(FOOD_IMAGE_URL, image_url);
        repr.put(FOOD_TOTAL_ENERGY, total_energy);
        repr.put(FOOD_TOTAL_FAT, total_fat);
        repr.put(FOOD_TOTAL_PROTEINS,total_proteins);
        repr.put(FOOD_TOTAL_CARBOHYDRATES, total_carbohydrates);
        repr.put(FOOD_QUANTITY, quantity);
        repr.put(FOOD_IS_EATEN, isEaten);
        
        return repr;
    }

    @Override
    public JSONObject toJSON(){
        JSONObject repr = constructJSON();
        System.out.println("IMAGE PIC :"+image_pic);
        repr.put(FOOD_IMAGE, image_pic.toJSON());
        return repr;
    }

    @Override
    public JSONObject toJSON(boolean noImage){
        JSONObject repr = constructJSON();
        if (noImage){
            repr.put(FOOD_IMAGE, image_pic.toJSON());
        }
        return repr;
    }

    @Override
    public void initFromJSON(JSONObject obj){
        this.id = (Long) obj.get(FOOD_ID);
        this.url = (String) obj.get(FOOD_URL);
        this.productName = (String) obj.get(FOOD_NAME);
        this.image_url = (String) obj.get(FOOD_IMAGE_URL);
        try{
            initFloatValuesFromFloat(obj);
        } catch (ClassCastException e){
            initFloatValuesFromDouble(obj);
        }

        this.quantity = (String) obj.get(FOOD_QUANTITY);
        this.isEaten = (Boolean) obj.get(FOOD_IS_EATEN);
        JSONObject img = (JSONObject) obj.get(FOOD_IMAGE);
        if (img != null){
            EdibleItemImage pic = new EdibleItemImage();
            pic.initFromJSON(img);
            this.setImagePic(pic);
        }

    }

    public void initFloatValuesFromFloat(JSONObject obj) throws ClassCastException{        // From Android, the values send are Double, this method handles this case.
        this.total_energy = (Float) obj.get(FOOD_TOTAL_ENERGY);
        this.total_fat = (Float) obj.get(FOOD_TOTAL_FAT);
        this.total_proteins = (Float) obj.get(FOOD_TOTAL_PROTEINS);
        this.total_carbohydrates = (Float) obj.get(FOOD_TOTAL_CARBOHYDRATES);
    }

    public void initFloatValuesFromDouble(JSONObject obj){
        this.total_energy = ((Double) obj.get(FOOD_TOTAL_ENERGY)).floatValue();
        this.total_fat = ((Double) obj.get(FOOD_TOTAL_FAT)).floatValue();
        this.total_proteins = ((Double) obj.get(FOOD_TOTAL_PROTEINS)).floatValue();
        this.total_carbohydrates = ((Double) obj.get(FOOD_TOTAL_CARBOHYDRATES)).floatValue();
    }
}