package manager;

import java.lang.Math;
import dao.SportsDAO;
import nioserver.Message;

import org.json.simple.JSONObject;
import java.util.List;

import static org.calorycounter.shared.Constants.network.*;


public class SportRequestManager implements RequestManager{

	private SportsDAO _sportsDatabase;

	public SportRequestManager(SportsDAO sdb){
		_sportsDatabase = sdb;
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

	@Override
	public JSONObject manageRequest(Message msg){
		int nbPackets = (int) Math.ceil((double)SPORTS_LIST_SIZE/JSON_THRESHOLD);
		List<String> db_names = _sportsDatabase.findSportsNames();
		JSONObject response = new JSONObject();
		if(db_names.size() == 0){
			manageFailure(response);
		}
		else{
			manageSuccess(response, db_names, nbPackets);		
		}
		return response;
	}
}