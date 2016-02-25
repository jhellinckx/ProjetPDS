package items;
import org.json.simple.JSONObject;
import org.calorycounter.shared.Constants;

import static org.calorycounter.shared.Constants.network.*;
import java.io.UnsupportedEncodingException;
public class Food {
	
	private Long      id;
    private String    url;
    private String    code;
    private String    productName;
    private String    image_url;
    private Float    total_energy;
    private Float    total_fat;
    private Float    total_proteins;
    private Float    total_saturated_fat;
    private Float    total_carbohydrates;
    private Float    total_sugars;
    private Float    total_sodium;
    
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
    
    public String getCode() {
        return code;
    }
    public void setCode( String code ) {
        this.code = code;
    }
    
    public String getProductName() {
        String repr = productName;
        try{
            byte[] b_l1 = repr.getBytes("ISO-8859-1");
            String repr_utf = new String(b_l1, "UTF-8");
            repr = repr_utf;
        }catch(UnsupportedEncodingException e){
            System.out.println(Constants.errorMessage(e.getMessage(), this));
        }
        return repr;
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

    public String toString() {
        String m = "";
        m+= productName;
        m+= " - energy: ";
        m+= Float.toString(total_energy);
        return m;
    }

    public JSONObject toJSON() {
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
        return repr;
    }

}
