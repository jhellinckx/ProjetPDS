package items;

public class Food {
	
	private Long      id;
    private String    url;
    private String    code;
    private String    productName;
    private String    image_url;
    private String    energy_100g;
    
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

    public String getEnergy100g() {
        return energy_100g;
    }
    public void setEnergy100g( String energy_100g ) {
        this.energy_100g = energy_100g;
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

}
