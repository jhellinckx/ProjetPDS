package manager;

import nioserver.Message;
import nioserver.AbstractNIOServer;
import items.CategoryRating;

import org.json.simple.JSONObject;
import org.calorycounter.shared.models.User;
import org.calorycounter.shared.models.Food;
import dao.FoodDAO;
import dao.UserPrefDAO;
import dao.CategoryRatingDAO;
import dao.DAOException;
import static org.calorycounter.shared.Constants.network.*;
import org.calorycounter.shared.Constants;

import java.util.List;
import java.util.ArrayList;


public class RatingRequestManager implements RequestManager{

	private AbstractNIOServer _server;
	private FoodDAO _foodDatabase;
	private UserPrefDAO _userprefDatabase;
	private CategoryRatingDAO _categoryRatingDatabase;

	public RatingRequestManager(AbstractNIOServer srv, FoodDAO fdb, UserPrefDAO updb, CategoryRatingDAO crdb){
		_server = srv;
		_foodDatabase = fdb;
		_userprefDatabase = updb;
		_categoryRatingDatabase = crdb;
	}

	private void insertRatingsToTable(List<String> categories, float rank, Message msg) throws DAOException{
		for(String category : categories){
			CategoryRating categoryRating = _categoryRatingDatabase.findRatedCategory(_server.getUser(msg), category);
			float newRating; int timesRated;
			if(categoryRating == null){
				newRating = rank;
				timesRated = 1;
			}
			else{
				newRating = (categoryRating.rating() * categoryRating.timesRated() + rank) / (categoryRating.timesRated() + 1);
				timesRated = categoryRating.timesRated() + 1;
			}
			_categoryRatingDatabase.addRatingForCategory(_server.getUser(msg), category, newRating, timesRated);
		}
	}

	private void addRatingsToDB(JSONObject data, Message msg){
		String currUrl;
		double currRank;
		for(int i = 0 ; i < data.size()/2 ; ++i){
			currUrl = (String) data.get(FOOD_IMAGE_URL+String.valueOf(i));
			currRank = (double) data.get(FOOD_RATING+String.valueOf(i));
			float rank = (float) currRank;
			
			Food currFood = _foodDatabase.findByUrl(currUrl);
			_userprefDatabase.create(_server.getUser(msg).getId(),currFood.getId(), rank);
			try{
				ArrayList<String> categories =  _categoryRatingDatabase.findCategoriesForFood(currFood);
				insertRatingsToTable(categories, rank, msg);
				
			}catch(DAOException e){
				System.out.println(Constants.errorMessage(e.getMessage(), this));
			}
		}
	}

	@Override
	public JSONObject manageRequest(Message msg){
		JSONObject data = (JSONObject) msg.toJSON().get(DATA);
		addRatingsToDB(data,msg);
		return null;
	}
}