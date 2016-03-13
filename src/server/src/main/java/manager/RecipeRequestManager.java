package manager;


import dao.FoodDAO;
import dao.RecipeDAO;
import dao.UserPrefDAO;
import nioserver.Message;
import nioserver.AbstractNIOServer;

import org.json.simple.JSONObject;
import org.calorycounter.shared.models.Food;
import org.calorycounter.shared.models.User;
import org.calorycounter.shared.models.Recipe;

import java.util.List;
import java.util.ArrayList;
import java.util.Random;
import java.util.Arrays;

import static org.calorycounter.shared.Constants.network.*;


public class RecipeRequestManager implements RequestManager{

	private RecipeDAO _recipeDatabase;
	private UserPrefDAO _userprefDatabase;
	private AbstractNIOServer _server;
	private FoodDAO _foodDatabase;

	public RecipeRequestManager(RecipeDAO rdb, AbstractNIOServer srv, UserPrefDAO uprfdao ){
		_recipeDatabase = rdb;
		_userprefDatabase = uprfdao;
		_server = srv;
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



	private List<Long> generateRandomRecipeIds(int nb, Message msg,  List<Long> recipeIds){
		Random r = new Random();
		int min = 1, max = recipeIds.size();
		/*
		ArrayList<Food> foods = new ArrayList(_userprefDatabase.findFoodsForUser(_server.getUser(msg)));
		int[] alreadyRankedIds = new int[foods.size()];

		for(int j=0 ; j<foods.size(); ++j){
			alreadyRankedIds[j] = foods.get(j).getId().intValue();
		}

		Arrays.sort(alreadyRankedIds);//Sort needed for random with excluded values
		*/
		recipeIds = new ArrayList<Long>();
		Long id = 0l;
		for(int i=0 ; i<nb ; ++i){
			//Long id = new Long(getRandomWithExclusion(r,min,max, alreadyRankedIds));
			id+=1l;
			System.out.println("ATTEMPT TO ADD NEW RECIPE : id = "+String.valueOf(id));
			if(!recipeIds.contains(id) && !_recipeDatabase.findById((int)(long)id).getImageUrl().isEmpty()){
				System.out.println("NEW RECIPE ADDED");
				recipeIds.add(id);
			}
			//else{i-=1;}
		}
		return recipeIds;
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

	private void makeJSON_onRandomUnrankedRecipesForCategoryRequest(List<Recipe> recipes, JSONObject responseData){
		if(recipes.size() == 0){
			responseData.put(RANDOM_UNRANKED_FOODS_RESPONSE, RANDOM_UNRANKED_FOODS_FAILURE);
			responseData.put(REASON, RANDOM_UNRANKED_FOODS_NOT_FOUND);
		}
		else{
			responseData.put(RANDOM_UNRANKED_FOODS_RESPONSE, RANDOM_UNRANKED_FOODS_SUCCESS);
			for(int i=0 ; i<recipes.size() ; ++i){ //ajout de chaque url au JSON
				responseData.put(FOOD_NAME+String.valueOf(i), recipes.get(i).toJSON(false));
			}
		}
	}

	private JSONObject onRandomUnrankedRecipesForCategoryRequest(Message msg){
		JSONObject data = (JSONObject) msg.toJSON().get(DATA);
		String categoryName = (String) data.get(RECIPE_CATEGORY);
		System.out.println("-----------------------: " + categoryName);
		List<Long> recipeIds = _recipeDatabase.getRecipeIdsByCategory(categoryName);
		
		recipeIds = generateRandomRecipeIds(NUMBER_RANDOM_FOODS, msg, recipeIds);
		List<Recipe> recipes = _recipeDatabase.findByIds(recipeIds);
		System.out.println(recipes.size());
		
		JSONObject responseData = new JSONObject();
		makeJSON_onRandomUnrankedRecipesForCategoryRequest(recipes, responseData);
		
		return responseData;
	}

	@Override
	public JSONObject manageRequest(Message msg){
		JSONObject response = msg.toJSON();
		String request = (String) response.get(REQUEST_TYPE);
		JSONObject responseData = null;
		if (request.equals(FOOD_CODE_REQUEST)){
			responseData = onFoodcodeRequest(msg);
		}
		else if(request.equals(RANDOM_RECIPES_FOR_CATEGORY_REQUEST)){
			responseData = onRandomUnrankedRecipesForCategoryRequest(msg);
		}
		return responseData;
	}
}