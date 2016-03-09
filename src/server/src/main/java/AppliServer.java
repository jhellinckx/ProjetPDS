import java.io.IOException;

import org.json.simple.JSONObject;
import org.json.simple.JSONArray;

import static org.calorycounter.shared.Constants.network.*;
import org.calorycounter.shared.Constants;

import nioserver.AbstractNIOServer;
import nioserver.Message;

import org.calorycounter.shared.models.User;
import org.calorycounter.shared.models.Food;
import items.CategoryRating;

import manager.*;

import dao.DAOFactory;
import dao.UserDAO;
import dao.FoodDAO;
import dao.UserPrefDAO;
import dao.SportsDAO;
import dao.CategoryRatingDAO;
import dao.UserHistoryDAO;
import dao.DAOException;
import dao.AllCategoriesDAO;

import recommender.RecommenderSystem;
import recommender.NearestNeighborStrategy;
import recommender.KnowledgeBasedFilter;

import java.util.Random;
import java.util.ArrayList;
import java.util.List;
import java.util.Arrays;
import java.lang.Math;

import java.util.HashMap;

public class AppliServer extends AbstractNIOServer{

	/* Database fields */
	private DAOFactory _daoFactory;
	private UserDAO _userDatabase;
	private FoodDAO _foodDatabase;
	private UserPrefDAO _userprefDatabase;
	private SportsDAO _sportsDatabase;
	private CategoryRatingDAO _categoryRatingDatabase;
	private UserHistoryDAO _userHistoryDatabase;
	private AllCategoriesDAO _categoriesDatabase;

	/* Recommendations fields */
	private KnowledgeBasedFilter _knowledgeBased;
	private RecommenderSystem _recommenderSystem;

	/* Manager fields */

	private HashMap<String, RequestManager> _managers;

	public AppliServer(){
		super();
		_daoFactory = DAOFactory.getInstance();
		_userDatabase = _daoFactory.getUserDAO();
		_foodDatabase = _daoFactory.getFoodDAO();
		_userprefDatabase = _daoFactory.getUserPrefDAO();
		_sportsDatabase = _daoFactory.getSportsDAO();
		_categoryRatingDatabase = _daoFactory.getCategoryRatingDAO();
		_userHistoryDatabase = _daoFactory.getUserHistoryDAO();
		_categoriesDatabase = _daoFactory.getAllCategoriesDAO();

		_recommenderSystem = new RecommenderSystem(new NearestNeighborStrategy(_categoryRatingDatabase));
		_knowledgeBased = new KnowledgeBasedFilter(_foodDatabase);
		_managers = new HashMap<>();
		_foodDatabase.findById(22L);
		initManagers();

	}

	private void initManagers(){
		AuthenticationRequestManager arm = new AuthenticationRequestManager(_userDatabase, this);
		FoodRequestManager frm = new FoodRequestManager(_foodDatabase, this, _userprefDatabase);
		RatingRequestManager rrm = new RatingRequestManager(this, _foodDatabase, _userprefDatabase, _categoryRatingDatabase);
		DataRequestManager drm = new DataRequestManager(this, _userDatabase);
		HistoryRequestManager hrm = new HistoryRequestManager(this, _foodDatabase, _userHistoryDatabase);
		SportRequestManager srm = new SportRequestManager(this,_sportsDatabase, _userHistoryDatabase);
		RecommendationRequestManager rerm = new RecommendationRequestManager(this, _foodDatabase, _sportsDatabase, 
			_recommenderSystem, _knowledgeBased, _userDatabase, _userHistoryDatabase);
		CategoriesRequestManager crm = new CategoriesRequestManager(_categoriesDatabase);

		initMap(arm, frm, rrm, drm, hrm, srm, rerm, crm);
	}

	private void initMap(AuthenticationRequestManager arm, FoodRequestManager frm, RatingRequestManager rrm, DataRequestManager drm, 
		HistoryRequestManager hrm, SportRequestManager srm, RecommendationRequestManager rerm, CategoriesRequestManager crm){

		_managers.put(LOG_IN_REQUEST, arm);
		_managers.put(SIGN_UP_REQUEST, arm);
		_managers.put(FOOD_CODE_REQUEST, frm);
		_managers.put(RANDOM_UNRANKED_FOODS_REQUEST, frm);
		_managers.put(SEND_RATINGS_REQUEST, rrm);
		_managers.put(SPORTS_LIST_REQUEST, srm);
		_managers.put(CHOSEN_SPORT_REQUEST, srm);
		_managers.put(UPDATE_DATA_REQUEST, drm);
		_managers.put(DATA_REQUEST, drm);
		_managers.put(HISTORY_REQUEST, hrm);
		_managers.put(FOOD_CODE_REQUEST_HISTORY, hrm);
		_managers.put(HISTORY_FOR_DATE_REQUEST, hrm);
		_managers.put(RECOMMEND_REQUEST, rerm);
		_managers.put(FOOD_CATEGORIES_REQUEST, crm);
		_managers.put(RECIPE_CATEGORIES_REQUEST, crm);
		_managers.put(CHANGE_EATEN_STATUS_REQUEST, hrm);
		_managers.put(DELETE_FOOD_HISTORY_REQUEST, hrm);
		_managers.put(DELETE_SPORT_HISTORY_REQUEST, hrm);
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
		if (request.equals(LOG_OUT_REQUEST)){
			onLogoutRequest(msg);
		}
		else{
			RequestManager manager = _managers.get(request);
			JSONObject data = manager.manageRequest(msg);
			sendResponse(data, msg, request);
		}		
	}

	private void sendResponse(JSONObject data, Message msg, String request){
		if (data != null){
			msg.setJSON(networkJSON(request, data));
			send(msg);
		}
	}

	/* No response is sent on logout request */
	
	public void onLogoutRequest(Message msg){
		removeClient(msg);
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