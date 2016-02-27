package items;

import java.lang.Math;

import java.util.ArrayList;
import java.util.List;
import java.util.HashMap;
import java.util.Collection;
import java.util.Iterator;

public class User {

    private Long      id;
    private String    username;
    private String    gender;
    private HashMap<Food, Float> rankedFoods;
    private Float weight;

    public User() {
    	this.id = null;
    	this.username = null;
    	this.gender = null;
        this.weight = null;
        this.rankedFoods = new HashMap<Food, Float>(); 
    }
    
    public User( String username, String gender ) {
    	this.username = username;
    	this.gender = gender;
        this.weight = -1f;
        this.rankedFoods = new HashMap<Food, Float>();
    }

    public User( String username, String gender, Float weight ) {
        this.username = username;
        this.gender = gender;
        this.weight = weight;
        this.rankedFoods = new HashMap<Food, Float>();
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

    public void setRankedFoods(HashMap<Food, Float> foodList){
        this.rankedFoods = foodList;
    }

    public void addRankedFood(Food food, float rank){
        rankedFoods.put(food,rank);
    }

    public float getRankForFood(Food food){
        return rankedFoods.get(food);
    }

    public boolean hasNotedFood(Food food){
        return rankedFoods.containsKey(food);
    }

    public void setWeight(float weight) {
        this.weight = weight;
    }

    public float getWeight() {
        return weight;
    }

    public float getMeanRank(){
        float sumRank = 0;
        int count = 0;
        Collection ranks = rankedFoods.values();
        Iterator it = ranks.iterator();
        if(it.hasNext()){
            while(it.hasNext()){
                count++;
                sumRank += (float)it.next();
            }
            return sumRank/count;
        }
        return sumRank;
    }

    private float getVarianceRank(){
        float mean = this.getMeanRank();
        float tmp = 0;
        int count = 0;
        Collection ranks = rankedFoods.values();
        Iterator it = ranks.iterator();
        if(it.hasNext()){
            while(it.hasNext()){
                count++;
                float rank = (float)it.next(); 
                tmp += (mean - rank)*(mean-rank);
            }
            return tmp/count;
        }
        return tmp;
    }

    public float getStdDeviation(){
        return (float)Math.sqrt(this.getVarianceRank());
    }
}
