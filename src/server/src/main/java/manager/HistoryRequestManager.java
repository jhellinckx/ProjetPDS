package manager;

import java.util.List;
import java.util.ArrayList;
import java.lang.Math;

import org.json.simple.JSONObject;
import org.json.simple.JSONArray;
import nioserver.Message;
import nioserver.AbstractNIOServer;
import util.ImageLoader;

import static org.calorycounter.shared.Constants.network.*;

import org.calorycounter.shared.models.User;
import org.calorycounter.shared.models.EdibleItem;
import org.calorycounter.shared.models.Food;
import org.calorycounter.shared.models.Recipe;
import org.calorycounter.shared.models.Sport;

import dao.FoodDAO;
import dao.UserHistoryDAO;
import dao.RecipeDAO;

public class HistoryRequestManager implements RequestManager{

	private User user;
	private AbstractNIOServer _server;
	private FoodDAO _foodDatabase;
	private RecipeDAO _recipeDatabase;
	private UserHistoryDAO _userHistoryDatabase;

	public HistoryRequestManager(AbstractNIOServer srv, FoodDAO fdb, UserHistoryDAO uhdb, RecipeDAO rdb){
		_server = srv;
		_foodDatabase = fdb;
		_userHistoryDatabase = uhdb;
		_recipeDatabase = rdb;
	}

	private void manageCodeFailure(JSONObject responseData){
		responseData.put(FOOD_CODE_RESPONSE, FOOD_CODE_FAILURE);
		responseData.put(REASON, FOOD_CODE_NOT_FOUND);
	}

	private void manageCodeSuccess(JSONObject responseData, Food food, String date){
		responseData.put(FOOD_CODE_RESPONSE, FOOD_CODE_SUCCESS);
		responseData.put(FOOD_NAME,food.getProductName());
		responseData.put(HISTORY_DATE, date);
	}

	private JSONObject onCodeHistoryRequest(Message msg){
		JSONObject data = (JSONObject) msg.toJSON().get(DATA);
		String code = (String) data.get(FOOD_CODE);
		String date = (String) data.get(HISTORY_DATE);
		JSONObject responseData = new JSONObject();
		Food food = _foodDatabase.findByCode(code);
		if(food == null){
			manageCodeFailure(responseData);
		}
		else{
			_userHistoryDatabase.addToHistory(user, food, date);
			manageCodeSuccess(responseData, food, date);
		}

		return responseData;
	}

	private List<Food> getFoodsFromDatabase(String date){
		List<Food> foods;
		if (date == null){
			foods = _userHistoryDatabase.getHistoryFoods(user);
		}
		else{
			foods = _userHistoryDatabase.getHistoryFoodForDate(user, date);
		}

		return foods;
	}

	private List<Recipe> getRecipesFromDatabase(String date){		// Change this when Users_history adapted to recipes.
		List<Recipe> recipes;
		if (date == null){
			recipes = _userHistoryDatabase.getHistoryRecipes(user);
		}
		else{
			recipes = _userHistoryDatabase.getHistoryRecipeForDate(user, date);
		}
		return recipes;
	}

	private void manageHistorySuccess(JSONObject data, List<? extends EdibleItem> items, List<String> dates, String constante, String type){
		loadImages(items);
		JSONArray itemsDatesRepr = new JSONArray();
		for(int i = 0 ; i< items.size(); ++i){
			JSONObject itemDateRepr = new JSONObject();
			itemDateRepr.put(constante, items.get(i).toJSON(false));
			itemDateRepr.put(HISTORY_DATE, dates.get(i));
			itemsDatesRepr.add(itemDateRepr);
		}
		data.put(type, itemsDatesRepr);
	}

	private void loadImages(List<? extends EdibleItem> items){
		ImageLoader.loadImages(items);
	}

	private JSONObject onHistoryRequest(Message msg){
		JSONObject data = new JSONObject();
		List<Food> foods = getFoodsFromDatabase(null);
		List<Recipe> recipes = getRecipesFromDatabase(null);

		List<String> foodates = _userHistoryDatabase.getHistoryDates(user);
		List<String> recipedates = _userHistoryDatabase.getHistoryRecipeDates(user);

		//Make JSON response
		if(foods.size() == foodates.size() && recipes.size() == recipedates.size()){
			manageHistorySuccess(data, foods, foodates, HISTORY_FOOD, HISTORY_FOODS_DATES);
			manageHistorySuccess(data, recipes, recipedates, HISTORY_RECIPE, HISTORY_RECIPES_DATES);
			return data;
		}
		else{
			System.out.println("Foods list and dates list in history don't match in size");
			return null;
		}
	}

	private JSONObject onHistoryForDateRequest(Message msg){
		JSONObject data = (JSONObject) msg.toJSON().get(DATA);
		String date = (String) data.get(HISTORY_DATE);
		List<Food> foods = getFoodsFromDatabase(date);
		List<Recipe> recipes = getRecipesFromDatabase(date);
		loadImages(foods);
		loadImages(recipes);
		JSONArray foodData = new JSONArray();
		for (int i = 0; i < foods.size(); i++){

			foodData.add(foods.get(i).toJSON());
		}
		JSONArray recipeData = new JSONArray();
		for (int k = 0; k < recipes.size(); k++){

			recipeData.add(recipes.get(k).toJSON());
		}
		List<Sport> sports = _userHistoryDatabase.getHistorySportForDate(user, date);
		JSONArray sportData = new JSONArray();
		for (int j = 0; j < sports.size(); j++){
			sportData.add(sports.get(j).toJSON());
		}
		data.put(FOOD_LIST, foodData);
		data.put(SPORT_LIST, sportData);
		data.put(RECIPE_LIST, recipeData);
		return data;
	}

	private JSONObject onChangeEatenStatus(Message msg){
		User user = _server.getUser(msg);
		JSONObject data = (JSONObject) msg.toJSON().get(DATA);
		int eatenStatus =(int) ((long) data.get(FOOD_IS_EATEN));
		String date = (String) data.get(HISTORY_DATE);
		String recipeOrFood = (String) data.get(RECIPE_OR_FOOD);
		if(recipeOrFood.equals("food")) {
			Food food = new Food();
			food.initFromJSON((JSONObject) data.get(FOOD_NAME));
			if((int) ((long) data.get(FOOD_IS_NEW)) == 1){
				_userHistoryDatabase.addToHistory(user.getId(), food.getId(), date, eatenStatus);
			}
			_userHistoryDatabase.changeEatenStatus(user, food, date, eatenStatus);
		}else{
			Recipe recipe = new Recipe();
			recipe.initFromJSON((JSONObject) data.get(FOOD_NAME));
			if((int) ((long) data.get(FOOD_IS_NEW)) == 1){
				_userHistoryDatabase.addRecipeToHistory(user, recipe, date, eatenStatus);
			}
			_userHistoryDatabase.changeRecipeEatenStatus(user, recipe, date, eatenStatus);
		}
		return new JSONObject();
	}


	private JSONObject onDeleteFoodHistoryRequest(Message msg){
		User user = _server.getUser(msg);
		JSONObject data = (JSONObject) msg.toJSON().get(DATA);
		String date = (String) data.get(HISTORY_DATE);
		String recipeOrFood = (String) data.get(RECIPE_OR_FOOD);
		if(recipeOrFood.equals("food")){
			Food food = new Food();
			food.initFromJSON((JSONObject) data.get(FOOD_NAME));
			_userHistoryDatabase.deleteFoodFromHistory(user, food, date);
		}else{
			Recipe recipe = new Recipe();
			recipe.initFromJSON((JSONObject) data.get(FOOD_NAME));
			_userHistoryDatabase.deleteRecipeFromHistory(user, recipe, date);
		}
		return new JSONObject();
	}

	private JSONObject onDeleteSportHistoryRequest(Message msg){
		User user = _server.getUser(msg);
		JSONObject data = (JSONObject) msg.toJSON().get(DATA);
		Sport sport = new Sport();
		sport.initFromJSON((JSONObject) data.get(SPORT_NAME));
		String date = (String) data.get(HISTORY_DATE);
		_userHistoryDatabase.deleteSportFromHistory(user, sport, date);
		return new JSONObject();
	}

	@Override
	public JSONObject manageRequest(Message msg){
		JSONObject received = msg.toJSON();
		String request = (String) received.get(REQUEST_TYPE);
		JSONObject responseData = null;
		user = _server.getUser(msg);
		if (request.equals(HISTORY_REQUEST)){
			responseData = onHistoryRequest(msg);
		}
		else if (request.equals(FOOD_CODE_REQUEST_HISTORY)){
			responseData = onCodeHistoryRequest(msg);
		}
		else if (request.equals(HISTORY_FOR_DATE_REQUEST)){
			responseData = onHistoryForDateRequest(msg);
		}
		else if (request.equals(CHANGE_EATEN_STATUS_REQUEST)){
			responseData = onChangeEatenStatus(msg);
		}
		else if (request.equals(DELETE_FOOD_HISTORY_REQUEST)){
			responseData = onDeleteFoodHistoryRequest(msg);
		}
		else if (request.equals(DELETE_SPORT_HISTORY_REQUEST)){
			responseData = onDeleteSportHistoryRequest(msg);
		}
		return responseData;
	}
}