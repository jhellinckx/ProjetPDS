package manager;

import nioserver.Message;

import org.calorycounter.shared.models.User;
import dao.UserDAO;

import org.json.simple.JSONObject;
import static org.calorycounter.shared.Constants.network.*;

public class DataRequestManager implements RequestManager{

	private User user;
	private UserDAO _userDatabase;

	public DataRequestManager(User usr, UserDAO udb){
		user = usr;
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
		_userDatabase.updateUserWeight(user, weight);
		_userDatabase.updateUserGender(user, gender);
		return null;
	}

	private String getGenderDbId(String gender){

		if(gender.equals("Woman")) {return "F";}
		else if (gender.equals("Man")) {return "M";}
		else if (gender.equals("Teen")) {return "T";}
		else {return "C";}
	}

	@Override
	public JSONObject manageRequest(Message msg){
		JSONObject received = msg.toJSON();
		String request = (String) received.get(REQUEST_TYPE);
		JSONObject responseData = null;
		if (request.equals(UPDATE_DATA_REQUEST)){
			responseData = onUpdateDataRequest(msg);
		}
		else if (request.equals(DATA_REQUEST)){
			responseData = onDataRequest(msg);
		}
		return responseData;
	}
}