package items;

public class Food {
	
	private Long      id;
    private String    url;
    private String    code;
    private String    productName;
    
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

}
