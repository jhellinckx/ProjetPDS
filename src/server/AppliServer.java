import java.io.IOException;

import org.json.simple.JSONObject;
import org.json.simple.parser.ParseException;

import static org.calorycounter.shared.Constants.network.*;
import org.calorycounter.shared.Constants;

import nioserver.AbstractNIOServer;
import nioserver.Message;

import items.User;
import items.Food;

import java.util.Random;
import java.util.ArrayList;
import java.util.List;

public class AppliServer extends AbstractNIOServer{
	public AppliServer(){
		super();
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
			responseData.put(FOOD_ENERGY100G,food.getEnergy100g());
		}
		msg.setJSON(networkJSON(FOOD_CODE_REQUEST, responseData));
		send(msg);
	}

	public void onRandomUnrankedFoodsRequest(Message msg){
		int nbOfUnrankedFoodsReturned = 9;
		JSONObject responseData = new JSONObject();
		ArrayList<Long> foodIds = new ArrayList<Long>();
		generateRandomFoodIds(nbOfUnrankedFoodsReturned, foodIds);//populate array foodCodes
		List<Food> foods = _foodDatabase.findByIds(foodIds);
	}

	private void generateRandomFoodIds(int nb, ArrayList<Long> foodIds){
		Random r = new Random();
		int min = 1, max = 63016;
		if(min>=nb && nb>=max){
			throw new IllegalArgumentException("nb must be between min and max");
		}
		else{
			Long id = new Long(r.nextInt((max-min)+1)+min);
			foodIds.add(id);
		}
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