package items;

import java.util.List;

public class User {

    private Long      id;
    private String    username;
    private String    gender;
    private List<Food> appreciatedFood;
    private List<Food> depreciatedFood;
    
    public User() {
    	this.id = null;
    	this.username = null;
    	this.gender = null;
    	this.appreciatedFood = null;
    	this.depreciatedFood = null;
    }
    
    public User( String username, String gender ) {
    	this.username = username;
    	this.gender = gender;
    }
    
    public Long getId() {
        return id;
    }
    public void setId( Long id ) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }
     public void setUsername( String username ) {
        this.username = username;
    }
   

    public String getGender() {
    	return gender;
    }
    public void setGender( String gender ) {
        this.gender = gender;
    }
    
    public List<Food> getAppreciatedFood() {
    	return appreciatedFood;
    }
    public void setApprecitedFood( List<Food> appreciatedFood ) {
        this.appreciatedFood = appreciatedFood;
    }
    
    public List<Food> getDepreciatedFood() {
    	return depreciatedFood;
    }
    public void setDepreciatedFood( List<Food> depreciatedFood ) {
        this.depreciatedFood = depreciatedFood;
    }
}
