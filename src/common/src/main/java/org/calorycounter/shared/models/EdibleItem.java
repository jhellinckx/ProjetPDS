package org.calorycounter.shared.models;

import static org.calorycounter.shared.Constants.network.*;
import org.calorycounter.shared.Constants;
import java.io.UnsupportedEncodingException;


public abstract class EdibleItem implements JSONSerializable{
	protected Long      id;
    protected String    url;
    protected String    code;
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

    public String getQuantity(){
        return quantity;
    }

    public void setQuantity(String quant){
        this.quantity = quant;
    }

    public String toString() {
        return getProductName();
    }
}