package manager;

import java.lang.Math;
import dao.SportsDAO;
import dao.UserHistoryDAO;
import nioserver.Message;
import nioserver.AbstractNIOServer;

import org.calorycounter.shared.models.User;
import org.calorycounter.shared.models.Sport;

import org.json.simple.JSONObject;
import java.util.List;

import static org.calorycounter.shared.Constants.network.*;


public class SportRequestManager implements RequestManager{

	private SportsDAO _sportsDatabase;
	private AbstractNIOServer _server;
	private UserHistoryDAO _userHistoryDatabase;

	public SportRequestManager(AbstractNIOServer srv, SportsDAO sdb, UserHistoryDAO uhb){
		_server = srv;
		_sportsDatabase = sdb;
		_userHistoryDatabase = uhb;
	}

	private void manageFailure(JSONObject response){
		response.put(SPORTS_LIST_RESPONSE, SPORTS_LIST_FAILURE);
		response.put(REASON, SPORTS_LIST_EMPTY);
	}

	private void manageSuccess(JSONObject response, List<String> db_names, int nbPackets){
		for (int j = 0; j < nbPackets; j++){
			response.put(SPORTS_LIST_RESPONSE, SPORTS_LIST_SUCCESS);
			for (int i = 0; i < Math.min(JSON_THRESHOLD,db_names.size()); i++){
				response.put(SPORT_NAME+Integer.toString(i), db_names.get(i+Math.min(JSON_THRESHOLD,db_names.size())*j));
			}
		}
	}

	private JSONObject onChosenSportRequest(Message msg, JSONObject response){
		User user = _server.getUser(msg);
		JSONObject data = (JSONObject) msg.toJSON().get(DATA);
		String sportName = (String) data.get(SPORT_NAME);
		String date = (String) data.get(HISTORY_DATE);
		Float jouleFromSport = 0F; 
		if(sportName != null){
			int sportDuration = Integer.parseInt((String) data.get(SPORT_DURATION));
			jouleFromSport = _sportsDatabase.findJouleByNameAndWeight(sportName, user.getWeight()) * sportDuration;
			Sport sport = new Sport(sportName, sportDuration, jouleFromSport);
			_userHistoryDatabase.addSportToHistory(user, sport, date);
			return sport.toJSON();
		}else{
			return response;
		}
	}

	@Override
	public JSONObject manageRequest(Message msg){
		JSONObject infos = (JSONObject) msg.toJSON();
		String request = (String) infos.get(REQUEST_TYPE);
		JSONObject response = new JSONObject();
		if (request.equals(SPORTS_LIST_REQUEST)){
			int nbPackets = (int) Math.ceil((double)SPORTS_LIST_SIZE/JSON_THRESHOLD);
			List<String> db_names = _sportsDatabase.findSportsNames();
			if(db_names.size() == 0){
				manageFailure(response);
			}
			else{
				manageSuccess(response, db_names, nbPackets);		
			}
		} else if(request.equals(CHOSEN_SPORT_REQUEST)){
			response = onChosenSportRequest(msg,response);
		}
		return response;
	}
}