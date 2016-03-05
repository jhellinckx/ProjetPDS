package manager;

import static org.calorycounter.shared.Constants.network.*;

import java.util.List;
import java.util.ArrayList;
import nioserver.Message;
import nioserver.AbstractNIOServer;

import recommender.RecommenderSystem;
import recommender.NearestNeighborStrategy;
import recommender.KnowledgeBasedFilter;

import org.json.simple.JSONObject;
import org.json.simple.JSONArray;

import org.calorycounter.shared.models.User;
import org.calorycounter.shared.models.Food;

import dao.UserDAO;
import dao.FoodDAO;
import dao.SportsDAO;
import dao.UserHistoryDAO;

public class RecommendationRequestManager implements RequestManager{

	private User user;
	private AbstractNIOServer _server;
	private FoodDAO _foodDatabase;
	private SportsDAO _sportsDatabase;
	private RecommenderSystem _recommenderSystem;
	private KnowledgeBasedFilter _knowledgeBased;
	private UserDAO _userDatabase;
	private UserHistoryDAO _userHistoryDatabase;

	public RecommendationRequestManager(AbstractNIOServer srv, FoodDAO fdb, SportsDAO sdb, RecommenderSystem rs, KnowledgeBasedFilter kb, UserDAO udb, UserHistoryDAO uhdb){
		_server = srv;
		_foodDatabase = fdb;
		_sportsDatabase = sdb;
		_recommenderSystem = rs;
		_knowledgeBased = kb;
		_userDatabase = udb;
		_userHistoryDatabase = uhdb;
	}

	private List<Food> changeTypeOfListToFood(List<String> pastFoodsCodes){
		List<Food> pastFoods = new ArrayList<Food>();
		if(pastFoodsCodes != null){
			for(String foodCode : pastFoodsCodes){
				pastFoods.add(_foodDatabase.findByCode(foodCode));
			}
		}
		return pastFoods;
	}

	private void addFoodsToHistory(List<Food> pastFoods, List<String> pastFoodsDates){
		if(pastFoodsDates!=null && pastFoodsDates.size() == pastFoods.size()){
			for (int i=0; i<pastFoodsDates.size();i++){
				_userHistoryDatabase.addToHistory(user, pastFoods.get(i),pastFoodsDates.get(i));
			}
		}
	}

	private JSONObject recommendItems(List<Food> pastFoods, Float maxEnergy, Float maxFat, Float maxProt, Float maxCarbo, String category){
		_knowledgeBased.updateUser(user);
		ArrayList<Food> recommendedFoods = _knowledgeBased.recommend(pastFoods,maxEnergy,maxFat,maxProt,maxCarbo, category);
		//_recommenderSystem.updateData(recommendedFoods, new ArrayList<User>(_userDatabase.findAllUsers()), user, 10);
		//recommendedFoods = _recommenderSystem.recommendItems();
		JSONArray jsonFoods = new JSONArray();
		for(int i=0; i<Math.min(recommendedFoods.size(),10);i++){
			jsonFoods.add(recommendedFoods.get(i).toJSON());
		}
		JSONObject sendData = new JSONObject();
		sendData.put(RECOMMENDED_FOOD_LIST, jsonFoods);
		return sendData;
	}

	private Float computeJouleFromSport(JSONObject data){
		String sportName = (String) data.get(SPORT_NAME);
		Float jouleFromSport = 0F; 
		if(sportName != null){
			String sportDurationString = (String) data.get(SPORT_DURATION);
			Integer sportDuration = Integer.parseInt(sportDurationString);
			jouleFromSport = _sportsDatabase.findJouleByNameAndWeight(sportName, user.getWeight()) * sportDuration;
		}
		return jouleFromSport;
	}

	@Override
	public JSONObject manageRequest(Message msg){
		JSONObject data = (JSONObject) msg.toJSON().get(DATA);
		user = _server.getUser(msg);
		String category = (String) data.get(FOOD_CATEGORY);
		List<String> pastFoodsCodes = (List<String>) data.get(PAST_FOODS_LIST);
		List<String> pastFoodsDates = (List<String>) data.get(PAST_FOODS_DATES);
		List<Food> pastFoods = changeTypeOfListToFood(pastFoodsCodes);
		Float maxEnergy = Float.parseFloat( (String) data.get(MAX_ENERGY));
		maxEnergy = maxEnergy * CAL_TO_JOULE_FACTOR;
		Float maxFat = Float.parseFloat( (String) data.get(MAX_FAT));
		Float maxProt = Float.parseFloat( (String) data.get(MAX_PROT));
		Float maxCarbo = Float.parseFloat( (String) data.get(MAX_CARBOHYDRATES));

		maxEnergy = maxEnergy + computeJouleFromSport(data);
		
		addFoodsToHistory(pastFoods, pastFoodsDates);
		data = recommendItems(pastFoods, maxEnergy, maxFat, maxProt, maxCarbo, category);
		return data;
	}
}