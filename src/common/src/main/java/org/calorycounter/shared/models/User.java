package org.calorycounter.shared.models;

import java.lang.Math;

import java.util.ArrayList;
import java.util.List;
import java.util.HashMap;
import java.util.Collection;
import java.util.Iterator;

import org.calorycounter.shared.models.EdibleItem;
import org.calorycounter.shared.models.JSONSerializable;
import org.json.simple.JSONObject;
import static org.calorycounter.shared.Constants.network.*;

public class User implements JSONSerializable {

    private Long      id;
    private String    username;
    private String    password;
    private String    gender;
    private HashMap<EdibleItem, Float> rankedEdibleItems;
    private Float weight;
    private Float height;

    public User() {
        this.id = null;
        this.username = null;
        this.gender = null;
        this.weight = null;
        this.height = null;
        this.rankedEdibleItems = new HashMap<EdibleItem, Float>();
    }

    public User( String username, String pw, String gender ) {
        this.username = username;
        this.password = pw;
        this.gender = gender;
        this.weight = -1f;
        this.height = -1f;
        this.rankedEdibleItems = new HashMap<EdibleItem, Float>();
    }

    public User( String username,String pw, String gender, Float weight, Float height ) {
        this.username = username;
        this.password = pw;
        this.gender = gender;
        this.weight = weight;
        this.height = height;
        this.rankedEdibleItems = new HashMap<EdibleItem, Float>();
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

    public String getPassword(){
        return password;
    }

    public void setPassword( String pw ){
        this.password = pw;
    }


    public String getGender() {
        return gender;
    }
    public void setGender( String gender ) {
        this.gender = gender;
    }


    public HashMap getRankedEdibleItems(){
        return rankedEdibleItems;
    }

    public void setRankedEdibleItems(HashMap<EdibleItem, Float> foodList){
        this.rankedEdibleItems = foodList;
    }

    public void addRankedEdibleItem(EdibleItem food, float rank){
        rankedEdibleItems.put(food,rank);
    }

    public float getRankForEdibleItem(EdibleItem food){
        return rankedEdibleItems.get(food);
    }

    public boolean hasNotedEdibleItem(EdibleItem food){
        return rankedEdibleItems.containsKey(food);
    }

    public void setWeight(float weight) {
        this.weight = weight;
    }

    public float getWeight() {
        return weight;
    }

    public void setHeight(float height) {
        this.height = height;
    }

    public float getHeight() {
        return height;
    }

    public float getMeanRank(){
        float sumRank = 0;
        int count = 0;
        Collection ranks = rankedEdibleItems.values();
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
        Collection ranks = rankedEdibleItems.values();
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

    @Override
    public JSONObject toJSON(){
        JSONObject repr = new JSONObject();
        repr.put(USERNAME, this.username);
        repr.put(PASSWORD, this.password);
        repr.put(GENDER, this.gender);
        repr.put(WEIGHT, this.weight);
        return repr;
    }

    @Override
    public JSONObject toJSON(boolean noImage){
        return toJSON();
    }

    @Override
    public void initFromJSON(JSONObject obj){
        this.setUsername((String) obj.get(USERNAME));
        this.setPassword((String) obj.get(PASSWORD));
        this.setGender((String) obj.get(GENDER));
        this.setWeight((float) obj.get(WEIGHT));
    }
}