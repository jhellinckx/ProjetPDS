package manager;

import java.util.List;

import org.json.simple.JSONObject;
import org.json.simple.JSONArray;
import nioserver.Message;
import nioserver.AbstractNIOServer;

import static org.calorycounter.shared.Constants.network.*;

import org.calorycounter.shared.models.User;
import org.calorycounter.shared.models.Food;

import dao.FoodDAO;
import dao.UserHistoryDAO;

public class HistoryRequestManager implements RequestManager{

	private User user;
	private AbstractNIOServer _server;
	private FoodDAO _foodDatabase;
	private UserHistoryDAO _userHistoryDatabase;

	public HistoryRequestManager(AbstractNIOServer srv, FoodDAO fdb, UserHistoryDAO uhdb){
		_server = srv;
		_foodDatabase = fdb;
		_userHistoryDatabase = uhdb;
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

	private void manageHistorySuccess(JSONObject data, List<Food> foods, List<String> dates){
		JSONArray foodsDatesRepr = new JSONArray();
		for(int i = 0 ; i< foods.size(); ++i){
			JSONObject foodDateRepr = new JSONObject();
			foodDateRepr.put(HISTORY_DATE, dates.get(i));
			foodDateRepr.put(HISTORY_NAME, foods.get(i).getProductName());
			foodDateRepr.put(FOOD_IMAGE_URL, foods.get(i).getImageUrl());
			foodsDatesRepr.add(foodDateRepr);
		}
		data.put(HISTORY_NAMES_DATES, foodsDatesRepr);
	}

	private JSONObject onHistoryRequest(Message msg){
		JSONObject data = new JSONObject();
		List<Food> foods = _userHistoryDatabase.getHistoryFoods(user);
		List<String> dates = _userHistoryDatabase.getHistoryDates(user);

		//Make JSON response
		if(foods.size() == dates.size()){
			manageHistorySuccess(data, foods, dates);
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
		List<String> foodNames = _userHistoryDatabase.getHistoryFoodNamesForDate(user, date);
		JSONArray foodNamesData = new JSONArray();
		for (int i = 0; i < foodNames.size(); i++){
			JSONObject foodName = new JSONObject();
			foodName.put(FOOD_NAME, foodNames.get(i));
		}
		data.put(FOOD_NAMES, foodNamesData);
		return data;
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
		return responseData;
	}
}