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
	private RecommendationRequestManager _recomManager;

	public RatingRequestManager(AbstractNIOServer srv, FoodDAO fdb, UserPrefDAO updb, RecommendationRequestManager rm){
		_server = srv;
		_foodDatabase = fdb;
		_userprefDatabase = updb;
		_recomManager = rm;
	}

	private void addRatingsToDB(JSONObject data, Message msg){
		String currUrl;
		double currRank;
		for(int i = 0 ; i < data.size()/2 ; ++i){
			User usr = _server.getUser(msg);
			currUrl = (String) data.get(FOOD_IMAGE_URL+String.valueOf(i));
			currRank = (double) data.get(FOOD_RATING+String.valueOf(i));
			float rank = (float) currRank;
			
			Food currFood = _foodDatabase.findByUrl(currUrl);
			_userprefDatabase.create(usr.getId(),currFood.getId(), rank);

			//TODO INSTEAD OF FOOD ID NEED RECIPE ID
			_recomManager.notifyNewRating(usr.getId(), currFood.getId());
		}
	}

	@Override
	public JSONObject manageRequest(Message msg){
		JSONObject data = (JSONObject) msg.toJSON().get(DATA);
		addRatingsToDB(data,msg);
		return null;
	}
}