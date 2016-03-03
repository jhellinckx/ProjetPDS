package manager;


import dao.FoodDAO;
import dao.UserPrefDAO;
import nioserver.Message;

import org.json.simple.JSONObject;
import org.calorycounter.shared.models.Food;
import org.calorycounter.shared.models.User;

import java.util.List;
import java.util.ArrayList;
import java.util.Random;
import java.util.Arrays;

import static org.calorycounter.shared.Constants.network.*;


public class FoodRequestManager implements RequestManager{

	private FoodDAO _foodDatabase;
	private UserPrefDAO _userprefDatabase;
	private User usr;

	public FoodRequestManager(FoodDAO fdb, User u, UserPrefDAO uprfdao ){
		_foodDatabase = fdb;
		_userprefDatabase = uprfdao;
		usr = u;
	}

	private void manageFailure(JSONObject responseData){
		responseData.put(FOOD_CODE_RESPONSE, FOOD_CODE_FAILURE);
		responseData.put(REASON, FOOD_CODE_NOT_FOUND);
	}

	private void manageSuccess(JSONObject responseData, Food food){
		responseData.put(FOOD_CODE_RESPONSE, FOOD_CODE_SUCCESS);
		responseData.put(FOOD_NAME,food.getProductName());
		responseData.put(FOOD_IMAGE_URL,food.getImageUrl());
	}

	private JSONObject onFoodcodeRequest(Message msg){
		JSONObject data = (JSONObject) msg.toJSON().get(DATA);
		String code = (String) data.get(FOOD_CODE);
		JSONObject responseData = new JSONObject();
		Food food = _foodDatabase.findByCode(code);
		if(food == null){
			manageFailure(responseData);
		}
		else{
			manageSuccess(responseData, food);
		}

		return responseData;
	}

	private JSONObject onRandomUnrankedFoodsRequest(Message msg){
		JSONObject responseData = new JSONObject();
		ArrayList<Long> foodIds = new ArrayList<Long>();
		generateRandomFoodIds(NUMBER_RANDOM_FOODS, msg, foodIds);//populate array foodIds
		List<Food> foods = _foodDatabase.findByIds(foodIds);
		makeJSON_onRandomUnrankedFoodRequest(foods, responseData);
		return responseData;
	}

	private void generateRandomFoodIds(int nb, Message msg,  ArrayList<Long> foodIds){
		Random r = new Random();
		int min = 1, max = 63016;
		ArrayList<Food> foods = new ArrayList(_userprefDatabase.findFoodsForUser(usr));
		int[] alreadyRankedIds = new int[foods.size()];

		for(int j=0 ; j<foods.size(); ++j){
			alreadyRankedIds[j] = foods.get(j).getId().intValue();
		}
		Arrays.sort(alreadyRankedIds);//Sort needed for random with excluded values
		for(int i=0 ; i<nb ; ++i){
			Long id = new Long(getRandomWithExclusion(r,min,max, alreadyRankedIds));
			if(!foodIds.contains(id) && !_foodDatabase.findById(id).getImageUrl().isEmpty()){
				foodIds.add(id);
			}else{i-=1;}
		}
	}  

	private int getRandomWithExclusion(Random rnd, int start, int end, int... exclude) {
	    int random = start + rnd.nextInt(end - start + 1 - exclude.length);
	    for (int ex : exclude) {
	        if (random < ex) {
	            break;
	        }
	        random++;
	    }
	    return random;
	}

	private void makeJSON_onRandomUnrankedFoodRequest(List<Food> foods, JSONObject responseData){
		if(foods.size() == 0){
			responseData.put(RANDOM_UNRANKED_FOODS_RESPONSE, RANDOM_UNRANKED_FOODS_FAILURE);
			responseData.put(REASON, RANDOM_UNRANKED_FOODS_NOT_FOUND);
		}
		else{
			responseData.put(RANDOM_UNRANKED_FOODS_RESPONSE, RANDOM_UNRANKED_FOODS_SUCCESS);
			for(int i=0 ; i<foods.size() ; ++i){ //ajout de chaque url au JSON
				responseData.put(FOOD_IMAGE_URL+String.valueOf(i), foods.get(i).getImageUrl());
				responseData.put(FOOD_NAME+String.valueOf(i), foods.get(i).getProductName());
			}
		}
	}

	@Override
	public JSONObject manageRequest(Message msg){
		JSONObject response = msg.toJSON();
		String request = (String) response.get(REQUEST_TYPE);
		JSONObject responseData = null;
		if (request.equals(FOOD_CODE_REQUEST)){
			responseData = onFoodcodeRequest(msg);
		}
		else if (request.equals(RANDOM_UNRANKED_FOODS_REQUEST)){
			responseData = onRandomUnrankedFoodsRequest(msg);
		}
		return responseData;
	}
}