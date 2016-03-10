package manager;

import nioserver.Message;
import nioserver.AbstractNIOServer;

import org.calorycounter.shared.models.User;
import dao.UserDAO;

import org.json.simple.JSONObject;
import static org.calorycounter.shared.Constants.network.*;

public class DataRequestManager implements RequestManager{

	private AbstractNIOServer _server;
	private UserDAO _userDatabase;
	private User user;

	public DataRequestManager(AbstractNIOServer srv, UserDAO udb){
		_server = srv;
		_userDatabase = udb;
	}

	public JSONObject onDataRequest(Message msg){
		JSONObject data = new JSONObject();
		data.put(UPDATE_DATA_GENDER, user.getGender());
		data.put(UPDATE_DATA_WEIGHT, user.getWeight());
		return data;
	}


	private JSONObject onUpdateDataRequest(Message msg){
		JSONObject data = (JSONObject) msg.toJSON().get(DATA);
		String gender = (String) data.get(UPDATE_DATA_GENDER);
		gender = getGenderDbId(gender);
		Float weight = Float.parseFloat( (String) data.get(UPDATE_DATA_WEIGHT));
		Float height = Float.parseFloat( (String) data.get(UPDATE_DATA_HEIGHT));
		_userDatabase.updateUserWeight(user, weight);
		_userDatabase.updateUserGender(user, gender);
		_userDatabase.updateUserHeight(user, height);
		return null;
	}

	private String getGenderDbId(String gender){

		if(gender.equals("Femme")) {return "F";}
		else if (gender.equals("Homme")) {return "M";}
		else if (gender.equals("Adolescent")) {return "T";}
		else {return "C";}
	}

	@Override
	public JSONObject manageRequest(Message msg){
		JSONObject received = msg.toJSON();
		String request = (String) received.get(REQUEST_TYPE);
		JSONObject responseData = null;
		user = _server.getUser(msg);
		if (request.equals(UPDATE_DATA_REQUEST)){
			responseData = onUpdateDataRequest(msg);
		}
		else if (request.equals(DATA_REQUEST)){
			responseData = onDataRequest(msg);
		}
		return responseData;
	}
}