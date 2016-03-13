package manager;

import nioserver.Message;
import nioserver.AbstractNIOServer;
import items.CategoryRating;

import org.json.simple.JSONObject;
import org.calorycounter.shared.models.User;
import org.calorycounter.shared.models.Recipe;
import dao.RecipeDAO;
import dao.UserPrefDAO;
import dao.CategoryRatingDAO;
import dao.DAOException;
import static org.calorycounter.shared.Constants.network.*;
import org.calorycounter.shared.Constants;

import java.util.List;
import java.util.ArrayList;


public class RatingRequestManager implements RequestManager{

	private AbstractNIOServer _server;
	private RecipeDAO _recipeDatabase;
	private UserPrefDAO _userprefDatabase;
	private CategoryRatingDAO _categoryRatingDatabase;

	public RatingRequestManager(AbstractNIOServer srv, RecipeDAO rdb, UserPrefDAO updb, CategoryRatingDAO crdb){
		_server = srv;
		_recipeDatabase = rdb;
		_userprefDatabase = updb;
		_categoryRatingDatabase = crdb;
	}

	private void addRatingsToDB(JSONObject data, Message msg){
		String currUrl;
		double currRank;
		for(int i = 0 ; i < data.size()/2 ; ++i){
			currUrl = (String) data.get(FOOD_IMAGE_URL+String.valueOf(i));
			currRank = (double) data.get(FOOD_RATING+String.valueOf(i));
			float rank = (float) currRank;
			
			//Recipe currRecipe = _recipeDatabase.findByUrl(currUrl);
			//_userprefDatabase.create(_server.getUser(msg).getId(),currRecipe.getId(), rank);
		}
	}

	@Override
	public JSONObject manageRequest(Message msg){
		JSONObject data = (JSONObject) msg.toJSON().get(DATA);
		addRatingsToDB(data,msg);
		return null;
	}
}