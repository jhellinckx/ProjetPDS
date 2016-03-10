package manager;

import nioserver.Message;
import nioserver.AbstractNIOServer;

import org.json.simple.JSONObject;
import org.calorycounter.shared.models.User;
import dao.UserDAO;
import static org.calorycounter.shared.Constants.network.*;

public class AuthenticationRequestManager implements RequestManager{

	private UserDAO _userDatabase;
	private AbstractNIOServer server;

	public AuthenticationRequestManager(UserDAO udb, AbstractNIOServer srv){
		_userDatabase = udb;
		server = srv;
	}

	private void manageUserNotFound(JSONObject responseData, String username){
		responseData.put(LOG_IN_RESPONSE, LOG_IN_FAILURE);
		responseData.put(REASON, LOG_IN_USERNAME_NOT_FOUND);
		responseData.put(USERNAME, username);
	}

	private void manageLogUserAlreadyConnected(JSONObject responseData, String username){
		responseData.put(LOG_IN_RESPONSE, LOG_IN_FAILURE);
		responseData.put(REASON, LOG_IN_ALREADY_CONNECTED);
		responseData.put(USERNAME, username);
	}

	private void manageWrongPassword(JSONObject responseData, String username){
		responseData.put(LOG_IN_RESPONSE, LOG_IN_FAILURE);
		responseData.put(REASON, LOG_IN_WRONG_PASSWORD);
		responseData.put(USERNAME, username);
	}

	private void manageLogSuccess(JSONObject responseData, User user){
		responseData.put(LOG_IN_RESPONSE, LOG_IN_SUCCESS);
		responseData.put(USERNAME, user.getUsername());
		responseData.put(UPDATE_DATA_GENDER, user.getGender());
	}

	private void manageSignUserAlreadyConnected(JSONObject responseData, String username){
		responseData.put(SIGN_UP_RESPONSE, SIGN_UP_FAILURE);
		responseData.put(REASON, SIGN_UP_ALREADY_CONNECTED);
		responseData.put(USERNAME, username);
	}

	private void manageSignSuccess(JSONObject responseData, String username){
		responseData.put(SIGN_UP_RESPONSE, SIGN_UP_SUCCESS);
		responseData.put(USERNAME, username);
	}

	private void manageUsernameExists(JSONObject responseData, String username){
			responseData.put(SIGN_UP_RESPONSE, SIGN_UP_FAILURE);
			responseData.put(REASON, SIGN_UP_USERNAME_EXISTS);
			responseData.put(USERNAME, username);
	}
	
	private JSONObject onLogRequest(Message msg){
		JSONObject data = (JSONObject) msg.toJSON().get(DATA);
		String username = (String) data.get(USERNAME);
		String password = (String) data.get(PASSWORD);
		JSONObject responseData = new JSONObject();
		User usr = this._userDatabase.findByUsername(username);
		if(usr == null){
			manageUserNotFound(responseData, username);
		}
		else{
			if(server.userConnected(usr.getUsername())){
				manageLogUserAlreadyConnected(responseData, username);
			}
			else{
				if(validatePassword(usr, password)){
					server.addClient(username, msg);
					manageLogSuccess(responseData, usr);
				}
				else{
					manageWrongPassword(responseData, username);
				}
			}
		}
		return responseData;
	}

	private Boolean validatePassword(User usr, String password){
		return usr.getPassword().equals(password);
	}

	private JSONObject onSignupRequest(Message msg){
		JSONObject data = (JSONObject) msg.toJSON().get(DATA);
		String username = (String) data.get(USERNAME);
		String password = (String) data.get(PASSWORD);
		JSONObject responseData = new JSONObject();
		User usr = new User(username,password, "M");
		if(!(this._userDatabase.create(usr))){
			if(server.userConnected(usr.getUsername())){
				manageSignUserAlreadyConnected(responseData, username);
			}
			else{
				server.addClient(username, msg);
				manageSignSuccess(responseData, username);
			}
		}
		else{
			manageUsernameExists(responseData, username);
		}
		return responseData;
	}

	@Override
	public JSONObject manageRequest(Message msg){
		JSONObject received = msg.toJSON();
		String request = (String) received.get(REQUEST_TYPE);
		JSONObject responseData = null;
		if (request.equals(LOG_IN_REQUEST)){
			responseData = onLogRequest(msg);
		}
		else if (request.equals(SIGN_UP_REQUEST)){
			responseData = onSignupRequest(msg);
		}
		return responseData;
	}
}