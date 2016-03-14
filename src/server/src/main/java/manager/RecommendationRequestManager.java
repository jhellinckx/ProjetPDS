package manager;

import static org.calorycounter.shared.Constants.network.*;

import java.util.List;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Map;
import java.util.LinkedHashMap;
import nioserver.Message;
import nioserver.AbstractNIOServer;

import recommender.RecommenderSystem;
import recommender.NearestNeighborStrategy;
import recommender.KnowledgeBasedFilter;
import recommender.ContentBasedWorker;
import util.ImageLoader;

import org.json.simple.JSONObject;
import org.json.simple.JSONArray;

import org.calorycounter.shared.models.User;
import org.calorycounter.shared.models.Food;
import org.calorycounter.shared.models.EdibleItem;
import org.calorycounter.shared.models.Recipe;

import dao.UserDAO;
import dao.FoodDAO;
import dao.SportsDAO;
import dao.UserHistoryDAO;
import dao.RecipeDAO;
import dao.CBUserPredictionsDAO;

public class RecommendationRequestManager implements RequestManager{
	private static final int NBR_WORKERS = 2;

	private User user;
	private AbstractNIOServer _server;
	private FoodDAO _foodDatabase;
	private SportsDAO _sportsDatabase;
	private RecommenderSystem _recommenderSystem;
	private KnowledgeBasedFilter _knowledgeBased;
	private UserDAO _userDatabase;
	private UserHistoryDAO _userHistoryDatabase;
	private RecipeDAO _recipeDatabase;
	private CBUserPredictionsDAO _predictionsDatabase;

	private Boolean _runWorkers;
	private List<ContentBasedWorker> _workers;
	private List<Thread> _workerThreads;
	private Map<Long, List<Long>> _userRatings;

	public RecommendationRequestManager(AbstractNIOServer srv, FoodDAO fdb, SportsDAO sdb, RecommenderSystem rs, KnowledgeBasedFilter kb, UserDAO udb, UserHistoryDAO uhdb, RecipeDAO rdao, CBUserPredictionsDAO preddao){
		_server = srv;
		_foodDatabase = fdb;
		_sportsDatabase = sdb;
		_recommenderSystem = rs;
		_knowledgeBased = kb;
		_userDatabase = udb;
		_userHistoryDatabase = uhdb;
		_recipeDatabase = rdao;
		_predictionsDatabase = preddao;
		_userRatings = Collections.synchronizedMap(new LinkedHashMap<Long, List<Long>>());
		_workers = new ArrayList<>();
		_workerThreads = new ArrayList<>();
		_runWorkers = false;
		startWorkers();
	}

	private List<EdibleItem> changeTypeOfListToFood(List<String> pastFoodsCodes){
		List<EdibleItem> pastFoods = new ArrayList<EdibleItem>();
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

	private void loadImages(List<Food> foods){
		ImageLoader.loadImages(foods);
	}

	private void loadRecipeImages(List<Recipe> recipes){
		ImageLoader.loadImages(recipes);
	}

	private JSONObject recommendItems(List<EdibleItem> pastFoods, Float maxEnergy, Float maxFat, Float maxProt, Float maxCarbo, String category){
		_knowledgeBased.updateUser(user);
		ArrayList<Food> recommendedFoods = _knowledgeBased.recommend(pastFoods,maxEnergy,maxFat,maxProt,maxCarbo, category);
		//_recommenderSystem.updateData(recommendedFoods, new ArrayList<User>(_userDatabase.findAllUsers()), user, 10);
		//recommendedFoods = _recommenderSystem.recommendItems();
		if(recommendedFoods.size()>10){
			recommendedFoods = new ArrayList<Food>(recommendedFoods.subList(0,10));
		}
		loadImages(recommendedFoods);
		JSONArray jsonFoods = new JSONArray();
		for(int i=0; i<Math.min(recommendedFoods.size(),10);i++){
			jsonFoods.add(recommendedFoods.get(i).toJSON());
		}
		JSONObject sendData = new JSONObject();
		sendData.put(RECOMMENDED_FOOD_LIST, jsonFoods);
		return sendData;
	}

	private JSONObject recommendRecipes(List<EdibleItem> pastFoods, Float maxEnergy, Float maxFat, Float maxProt, Float maxCarbo, String category){
		_knowledgeBased.updateUser(user);
		ArrayList<Recipe> recommendedRecipes = _knowledgeBased.recommendRecipe(pastFoods,maxEnergy,maxFat,maxProt,maxCarbo, category);
		if(recommendedRecipes.size()>10){
			recommendedRecipes = new ArrayList<Recipe>(recommendedRecipes.subList(0,10));
		}
		loadRecipeImages(recommendedRecipes);
		JSONArray jsonRecipe = new JSONArray();
		for(int i=0; i<Math.min(recommendedRecipes.size(),10);i++){
			jsonRecipe.add(recommendedRecipes.get(i).toJSON());
		}
		JSONObject sendData = new JSONObject();
		sendData.put(RECOMMENDED_FOOD_LIST, jsonRecipe);
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

	private void addRecipesToPasstMeals(List<EdibleItem> pastMeals, List<String> pastRecipeIds){
		if(pastRecipeIds != null){
			for(String recipeId : pastRecipeIds){
				pastMeals.add(_recipeDatabase.findById(Integer.parseInt(recipeId)));
			}
		}
	}

	@Override
	public JSONObject manageRequest(Message msg){
		JSONObject data = (JSONObject) msg.toJSON().get(DATA);
		user = _server.getUser(msg);
		String category = (String) data.get(FOOD_CATEGORY);
		List<String> pastFoodsCodes = (List<String>) data.get(PAST_FOODS_LIST);
		List<String> pastFoodsDates = (List<String>) data.get(PAST_FOODS_DATES);
		List<String> pastRecipesIds = (List<String>) data.get(PAST_RECIPES_LIST);
		List<String> pastRecipesDates = (List<String>) data.get(PAST_RECIPES_DATES);
		List<EdibleItem> pastMeals = changeTypeOfListToFood(pastFoodsCodes);
		addRecipesToPasstMeals(pastMeals, pastRecipesIds);
		String recipeOrFood = (String) data.get(RECIPE_OR_FOOD);
		Float maxEnergy = Float.parseFloat( (String) data.get(MAX_ENERGY));
		maxEnergy = maxEnergy * CAL_TO_JOULE_FACTOR;
		Float maxFat = Float.parseFloat( (String) data.get(MAX_FAT));
		Float maxProt = Float.parseFloat( (String) data.get(MAX_PROT));
		Float maxCarbo = Float.parseFloat( (String) data.get(MAX_CARBOHYDRATES));

		//maxEnergy = maxEnergy + computeJouleFromSport(data); Its done in dayRecording
		
		//addFoodsToHistory(pastFoods, pastFoodsDates); Its done in dayRecording
		if(recipeOrFood.equals("food")){
			data = recommendItems(pastMeals, maxEnergy, maxFat, maxProt, maxCarbo, category);
		}else{
			data = recommendRecipes(pastMeals, maxEnergy/CAL_TO_JOULE_FACTOR, maxFat, maxProt, maxCarbo, category);
		}
		return data;
	}

	public boolean workersRunning(){
		synchronized(_runWorkers){
			return _runWorkers;
		}
	}

	public void startWorkers(){
		synchronized(_runWorkers){
			_runWorkers = true;
		}
		for(int i = 0; i < NBR_WORKERS; ++i){
			ContentBasedWorker worker = new ContentBasedWorker(this, _predictionsDatabase);
			_workers.add(worker);
			Thread thread = new Thread(worker);
			_workerThreads.add(thread);
			thread.start();
		}
	}

	public void stopWorkers(){
		for(ContentBasedWorker worker : _workers){
			worker.stop();
		}
		synchronized(_runWorkers){
			_runWorkers = false;
		}
		synchronized(_userRatings){
			_userRatings.notifyAll();
		}
		for(Thread workerThread : _workerThreads){
			try{
				workerThread.join();
			}
			catch(InterruptedException e){}
		}
	}

	public void notifyNewRating(Long userID, Long recipeID){
		synchronized(_userRatings){
			if(_userRatings.containsKey(userID)){
				_userRatings.get(userID).add(recipeID);
			}
			else{
				List<Long> ids = new ArrayList<Long>();
				ids.add(recipeID); 
				_userRatings.put(userID, ids);
			}
			_userRatings.notify();
		}	
	}

	public Map.Entry<Long, List<Long>> requestNewRatings() throws InterruptedException{
		synchronized(_userRatings){
			while(_userRatings.isEmpty() && workersRunning()){
				try{
					_userRatings.wait();	
				}
				catch(InterruptedException e){}
			}
			if(!workersRunning()){
				throw new InterruptedException();
			}
			Map.Entry<Long, List<Long>> entry = _userRatings.entrySet().iterator().next();
			_userRatings.remove(entry.getKey());
			return entry;
		}
	}

}