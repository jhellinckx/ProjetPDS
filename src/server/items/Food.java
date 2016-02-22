package items;

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

}
