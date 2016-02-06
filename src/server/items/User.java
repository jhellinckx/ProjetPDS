package items;

import java.util.ArrayList;
import java.util.List;
import java.util.HashMap;

public class User {

    private Long      id;
    private String    username;
    private String    gender;
    private HashMap rankedFoods;

    public User() {
    	this.id = null;
    	this.username = null;
    	this.gender = null;
        this.rankedFoods = new HashMap(); 
    }
    
    public User( String username, String gender ) {
    	this.username = username;
    	this.gender = gender;
        this.rankedFoods = new HashMap();
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
    

    public HashMap getRankedFoods(){
        return rankedFoods;
    }

    public void setRankedFoods(HashMap foodList){
        this.rankedFoods = foodList;
    }

    public void addRankedfood(Food food, float rank){
        rankedFoods.put(food,rank);
    }
}
