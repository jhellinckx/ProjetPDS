package manager;

import nioserver.Message;
import nioserver.AbstractNIOServer;
import items.CategoryRating;

import org.json.simple.JSONObject;
import org.calorycounter.shared.models.User;
import org.calorycounter.shared.models.Food;
import org.calorycounter.shared.models.Recipe;
import dao.FoodDAO;
import dao.RecipeDAO;
import dao.UserPrefDAO;
import dao.CategoryRatingDAO;
import dao.DAOException;
import static org.calorycounter.shared.Constants.network.*;
import org.calorycounter.shared.Constants;

import java.util.List;
import java.util.ArrayList;

import ui.Displayer;
import ui.action.UserAction;
import ui.action.ActionIdContainer;


public class RatingRequestManager implements RequestManager{

	private static final int RATING_NUMBER = 1;
	private static final String ENDING = " recettes";

	private AbstractNIOServer _server;
	private FoodDAO _foodDatabase;
	private RecipeDAO _recipeDatabase;
	private UserPrefDAO _userprefDatabase;
	private CategoryRatingDAO _categoryRatingDatabase;
	private RecommendationRequestManager _recomManager;
	private Displayer _displayer;

	public RatingRequestManager(AbstractNIOServer srv, FoodDAO fdb, UserPrefDAO updb, RecommendationRequestManager rm, Displayer displayer){
		_server = srv;
		_foodDatabase = fdb;
		_userprefDatabase = updb;
		_recomManager = rm;
		_displayer = displayer;
	}

	private void addRatingsToDB(JSONObject data, Message msg){
		User usr = _server.getUser(msg);
		Long id = (Long) data.get(FOOD_ID);
		double ratingD = (double) data.get(FOOD_RATING);
		float rating = (float) ratingD;
		//Recipe recipe = _recipeDatabase.findById(id);
		_userprefDatabase.create(usr.getId(), id, rating);
		_recomManager.notifyNewRating(usr.getId(), id);
		_displayer.displayUserAction(_server.getUser(msg).getUsername(), new UserAction(ActionIdContainer.RATING, RATING_NUMBER), ENDING);
	}

	@Override
	public JSONObject manageRequest(Message msg){
		JSONObject data = (JSONObject) msg.toJSON().get(DATA);
		addRatingsToDB(data,msg);
		return null;
	}
}