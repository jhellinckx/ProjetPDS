package manager;


import dao.FoodDAO;
import dao.RecipeDAO;
import dao.UserPrefDAO;
import nioserver.Message;
import nioserver.AbstractNIOServer;

import org.json.simple.JSONObject;
import org.calorycounter.shared.models.User;
import org.calorycounter.shared.models.Recipe;

import java.util.List;
import java.util.ArrayList;
import java.util.Random;
import java.util.Arrays;
import util.ImageLoader;

import static org.calorycounter.shared.Constants.network.*;


public class RecipeRequestManager implements RequestManager{

	private RecipeDAO _recipeDatabase;
	private UserPrefDAO _userprefDatabase;
	private AbstractNIOServer _server;

	public RecipeRequestManager(RecipeDAO rdb, AbstractNIOServer srv, UserPrefDAO uprfdao ){
		_recipeDatabase = rdb;
		_userprefDatabase = uprfdao;
		_server = srv;
	}

	private List<Long> generateRandomRecipeIds(int nb, Message msg,  List<Long> recipeIds){
		Random r = new Random();
		int min = 0, max = recipeIds.size()-1;
		/*
		ArrayList<Food> foods = new ArrayList(_userprefDatabase.findFoodsForUser(_server.getUser(msg)));
		int[] alreadyRankedIds = new int[foods.size()];

		for(int j=0 ; j<foods.size(); ++j){
			alreadyRankedIds[j] = foods.get(j).getId().intValue();
		}

		Arrays.sort(alreadyRankedIds);//Sort needed for random with excluded values
		*/
		List<Long> randomRecipeIds = new ArrayList<Long>();
		for(int i=0 ; i<nb ; ++i){
			int idIndex = getRandomWithExclusion(r,min,max);
			Long id = recipeIds.get(idIndex);
			if(!randomRecipeIds.contains(id) && !_recipeDatabase.findById((int)(long)id).getImageUrl().isEmpty()){
				randomRecipeIds.add(id);
			}
			//else{i-=1;}
		}
		return randomRecipeIds;
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
				responseData.put(FOOD_NAME+String.valueOf(i), recipes.get(i).toJSON());
			}
		}
	}

	private JSONObject onRandomUnrankedRecipesForCategoryRequest(Message msg){
		JSONObject data = (JSONObject) msg.toJSON().get(DATA);
		String categoryName = (String) data.get(RECIPE_CATEGORY);
		List<Long> recipeIds = _recipeDatabase.getRecipeIdsByCategory(categoryName);
		
		recipeIds = generateRandomRecipeIds(NUMBER_RANDOM_FOODS, msg, recipeIds);
		List<Recipe> recipes = _recipeDatabase.findByIds(recipeIds);
		loadRecipeImages(recipes);
		
		JSONObject responseData = new JSONObject();
		makeJSON_onRandomUnrankedRecipesForCategoryRequest(recipes, responseData);
		
		return responseData;
	}

	private void loadRecipeImages(List<Recipe> recipes){
		ImageLoader.loadImages(recipes);
	}

	@Override
	public JSONObject manageRequest(Message msg){
		JSONObject response = msg.toJSON();
		String request = (String) response.get(REQUEST_TYPE);
		JSONObject responseData = null;
		if(request.equals(RANDOM_RECIPES_FOR_CATEGORY_REQUEST)){
			responseData = onRandomUnrankedRecipesForCategoryRequest(msg);
		}
		return responseData;
	}
}