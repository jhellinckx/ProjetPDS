import java.io.IOException;

import org.json.simple.JSONObject;
import org.json.simple.parser.ParseException;
import org.json.simple.JSONArray;

import static org.calorycounter.shared.Constants.network.*;
import org.calorycounter.shared.Constants;

import nioserver.AbstractNIOServer;
import nioserver.Message;

import items.User;
import items.Food;
import dao.DAOFactory;
import dao.UserDAO;
import dao.FoodDAO;
import dao.UserPrefDAO;
import dao.SportsDAO;
import dao.CategoryRatingDAO;
import dao.DAOException;

import recommender.HybridationStrategy;
import recommender.CascadeStrategy;

import java.util.Random;
import java.util.ArrayList;
import java.util.List;
import java.util.Arrays;

public class AppliServer extends AbstractNIOServer{

	/* Database fields */
	private DAOFactory _daoFactory;
	private UserDAO _userDatabase;
	private FoodDAO _foodDatabase;
	private UserPrefDAO _userprefDatabase;
	private SportsDAO _sportsDatabase;
	private CategoryRatingDAO _categoryRatingDatabase;

	/* Recommendations fields */
	private HybridationStrategy _hybridStrategy;

	public AppliServer(){
		super();
		_daoFactory = DAOFactory.getInstance();
		_userDatabase = _daoFactory.getUserDAO();
		_foodDatabase = _daoFactory.getFoodDAO();
		_userprefDatabase = _daoFactory.getUserPrefDAO();
		_sportsDatabase = _daoFactory.getSportsDAO();
		_categoryRatingDatabase = _daoFactory.getCategoryRatingDAO();

		_hybridStrategy = new CascadeStrategy();
	}

	public User getUser(Message msg){
		return this._userDatabase.findByUsername(getUsername(msg));
	}

	public void handleMessage(Message msg) throws IOException{
		JSONObject received = msg.toJSON();
		if(!received.containsKey(REQUEST_TYPE) || !received.containsKey(DATA))
			throw new IOException("Network message has to contain a " +
				REQUEST_TYPE +" key and a " + DATA + " key.");
		String request = (String) received.get(REQUEST_TYPE);
		if(request.equals(LOG_IN_REQUEST)){
			onLoginRequest(msg);
		}
		else if(request.equals(LOG_OUT_REQUEST)){
			onLogoutRequest(msg);
		}
		else if(request.equals(SIGN_UP_REQUEST)){
			onSignupRequest(msg);
		}
		else if(request.equals(FOOD_CODE_REQUEST)){
			onFoodcodeRequest(msg);
		}
		else if(request.equals(RANDOM_UNRANKED_FOODS_REQUEST)){
			onRandomUnrankedFoodsRequest(msg);
		}
		else if(request.equals(SEND_RATINGS_REQUEST)){
			onSendRatingRequest(msg);
		}
		else if(request.equals(SPORTS_LIST_REQUEST)){
			onSportsListRequest(msg);
		}
		else if(request.equals(UPDATE_DATA_REQUEST)){
			onUpdateDataRequest(msg);
		}
		else if(request.equals(RECOMMEND_REQUEST)){
			onShotgunRecommendRequest(msg);
		}
	}

	 public ArrayList<Food> randomRecommend(FoodDAO dbFood){
        ArrayList<Food> randomFoods = new ArrayList<>();
        int min = 1; int max = 10000;
        for(int i = 0; i < RECOMMENDATIONS_REQUIRED; ++i){
            int randomNum = min + (int)(Math.random() * ((max - min) + 1));
            Food food = new Food();
            food.setId((new Integer(randomNum)).longValue());
            try{
                food = dbFood.findById((new Integer(randomNum)).longValue());
            } catch(DAOException e){
                System.out.println(e.getMessage());
            }
            if(food != null) randomFoods.add(food);
        }
        return randomFoods;
    }

	public void onShotgunRecommendRequest(Message msg){
		ArrayList<Food> randomRecommend = randomRecommend(_foodDatabase);
		JSONArray jsonFoods = new JSONArray();
		for(Food randomFood : randomRecommend){
			jsonFoods.add(randomFood.toJSON());
		}
		JSONObject data = new JSONObject();
		data.put(RECOMMENDED_FOOD_LIST, jsonFoods);
		msg.setJSON(networkJSON(RECOMMEND_REQUEST, data));
		send(msg);
	}

	public void onLoginRequest(Message msg){
		JSONObject data = (JSONObject) msg.toJSON().get(DATA);
		String username = (String) data.get(USERNAME);
		JSONObject responseData = new JSONObject();
		User usr = this._userDatabase.findByUsername(username);
		if(usr == null){
			responseData.put(LOG_IN_RESPONSE, LOG_IN_FAILURE);
			responseData.put(REASON, LOG_IN_USERNAME_NOT_FOUND);
			responseData.put(USERNAME, username);
		}
		else{
			if(userConnected(usr.getUsername())){ // Check if username is already connected
				responseData.put(LOG_IN_RESPONSE, LOG_IN_FAILURE);
				responseData.put(REASON, LOG_IN_ALREADY_CONNECTED);
				responseData.put(USERNAME, username);
			}
			else{
				addClient(username, msg);
				responseData.put(LOG_IN_RESPONSE, LOG_IN_SUCCESS);
				responseData.put(USERNAME, username);
			}
		}
		/* Adjust message with json response. networkJSON is defined in Constants.network */
		msg.setJSON(networkJSON(LOG_IN_REQUEST, responseData));
		send(msg);
	}

	/* No response is sent on logout request */
	public void onLogoutRequest(Message msg){
		removeClient(msg);
	}

	public void onSignupRequest(Message msg){
		JSONObject data = (JSONObject) msg.toJSON().get(DATA);
		String username = (String) data.get(USERNAME);
		JSONObject responseData = new JSONObject();
		User usr = new User(username,"M"); //TODO : demander le genre
		if(!(this._userDatabase.create(usr))){
			if(userConnected(usr.getUsername())){
				responseData.put(SIGN_UP_RESPONSE, SIGN_UP_FAILURE);
				responseData.put(REASON, SIGN_UP_ALREADY_CONNECTED);
				responseData.put(USERNAME, username);
			}
			else{
				addClient(username, msg);
				responseData.put(SIGN_UP_RESPONSE, SIGN_UP_SUCCESS);
				responseData.put(USERNAME, username);
			}
		}
		else{
			responseData.put(SIGN_UP_RESPONSE, SIGN_UP_FAILURE);
			responseData.put(REASON, SIGN_UP_USERNAME_EXISTS);
			responseData.put(USERNAME, username);
		}
		msg.setJSON(networkJSON(SIGN_UP_REQUEST, responseData));
		send(msg);

	}

	public void onFoodcodeRequest(Message msg){
		JSONObject data = (JSONObject) msg.toJSON().get(DATA);
		String code = (String) data.get(FOOD_CODE);
		JSONObject responseData = new JSONObject();
		Food food = _foodDatabase.findByCode(code);
		if(food == null){
			responseData.put(FOOD_CODE_RESPONSE, FOOD_CODE_FAILURE);
			responseData.put(REASON, FOOD_CODE_NOT_FOUND);
		}
		else{
			responseData.put(FOOD_CODE_RESPONSE, FOOD_CODE_SUCCESS);
			responseData.put(FOOD_NAME,food.getProductName());
			responseData.put(FOOD_IMAGE_URL,food.getImageUrl());
		}
		msg.setJSON(networkJSON(FOOD_CODE_REQUEST, responseData));
		send(msg);
	}

	public void onRandomUnrankedFoodsRequest(Message msg){
		JSONObject responseData = new JSONObject();
		ArrayList<Long> foodIds = new ArrayList<Long>();
		generateRandomFoodIds(NUMBER_RANDOM_FOODS, msg, foodIds);//populate array foodIds
		List<Food> foods = _foodDatabase.findByIds(foodIds);
		makeJSON_onRandomUnrankedFoodRequest(foods, responseData);
		msg.setJSON(networkJSON(RANDOM_UNRANKED_FOODS_REQUEST, responseData));
		send(msg);
	}

	private void generateRandomFoodIds(int nb, Message msg,  ArrayList<Long> foodIds){
		Random r = new Random();
		int min = 1, max = 63016;
		ArrayList<Food> foods = new ArrayList(_userprefDatabase.findFoodsForUser(getUser(msg)));
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

	public void onSendRatingRequest(Message msg){
		JSONObject data = (JSONObject) msg.toJSON().get(DATA);
		System.out.println(data.size());
		String currUrl;
		double currRank; 
		User currUser = getUser(msg);
		for(int i = 0 ; i < data.size()/2 ; ++i){
			currUrl = (String) data.get(FOOD_IMAGE_URL+String.valueOf(i));
			currRank = (double) data.get(FOOD_RATING+String.valueOf(i));
			float rank = (float) currRank;
			System.out.println("currUrl: "+currUrl+"\ncurrRank :"+String.valueOf(currRank));
			Food currFood = _foodDatabase.findByUrl(currUrl);
			_userprefDatabase.create(currUser.getId(),currFood.getId(), rank);
		}
		//String response = (String) data.get(FOOD_CODE);
		//JSONObject responseData = new JSONObject();
		//Food food = _foodDatabase.findByCode(code);
		
	}

	public void onSportsListRequest(Message msg){
		int threshold = JSON_THRESHOLD;
		double size = (double) SPORTS_LIST_SIZE;
		int nbPackets = (int) Math.ceil(size/JSON_THRESHOLD);
		List<String> db_names = _sportsDatabase.findSportsNames();

		if(db_names.size() == 0){
			JSONObject response = new JSONObject();
			response.put(SPORTS_LIST_RESPONSE, SPORTS_LIST_FAILURE);
			response.put(REASON, SPORTS_LIST_EMPTY);
			msg.setJSON(networkJSON(SPORTS_LIST_REQUEST, response));
			send(msg);
		}
		else{
			for (int j = 0; j < nbPackets; j++){
				JSONObject response = new JSONObject();
				response.put(SPORTS_LIST_RESPONSE, SPORTS_LIST_SUCCESS);
				for (int i = 0; i < Math.min(JSON_THRESHOLD,db_names.size()); i++){
					response.put(SPORT_NAME+Integer.toString(i), db_names.get(i+Math.min(JSON_THRESHOLD,db_names.size())*j));
				}
				msg.setJSON(networkJSON(SPORTS_LIST_REQUEST, response));
				send(msg);		
			}
		}
	}

	public void onUpdateDataRequest(Message msg){
		User user = getUser(msg);
		JSONObject data = (JSONObject) msg.toJSON().get(DATA);
		String gender = (String) data.get(UPDATE_DATA_GENDER);
		gender = getGenderDbId(gender);
		Float weight = Float.parseFloat( (String) data.get(UPDATE_DATA_WEIGHT));
		_userDatabase.updateUserWeight(user, weight);
		_userDatabase.updateUserGender(user, gender);

	}

	private String getGenderDbId(String gender){

		if(gender.equals("Woman")) {return "F";}
		else if (gender.equals("Man")) {return "M";}
		else if (gender.equals("Teen")) {return "T";}
		else if (gender.equals("Child")) {return "K";} //kid
		else{return "B";} //aurélien remove baby des age bracket please
	}

	public static void main(String[] args){
		try{
			AppliServer appserver = new AppliServer();
			appserver.run();
		} 
		catch(Exception e){
			System.out.println(Constants.errorMessage("Uncaught exception : "
				+e.getMessage(), new String("root")));
			e.printStackTrace();
		}
	}

}