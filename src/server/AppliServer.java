import java.io.IOException;

import org.json.simple.JSONObject;
import org.json.simple.parser.ParseException;

import static org.calorycounter.shared.Constants.network.*;
import org.calorycounter.shared.Constants;

import nioserver.AbstractNIOServer;
import nioserver.Message;

import items.User;
import items.Food;

public class AppliServer extends AbstractNIOServer{
	public AppliServer(){
		super();
	}

	public void handleMessage(Message msg) throws IOException{
		JSONObject received = msg.toJSON();
		if(!received.containsKey(REQUEST_TYPE) || !received.containsKey(DATA))
			throw new IOException("Network message has to contain a " +
				REQUEST_TYPE +" key and a " + DATA + " key.");
		String request = (String) received.get(REQUEST_TYPE);
		if(request.equals(LOG_IN_REQUEST)){
			onLoginRequest(msg);
		}
		else if(request.equals(LOG_OUT_REQUEST)){
			onLogoutRequest(msg);
		}
		else if(request.equals(SIGN_UP_REQUEST)){
			onSignupRequest(msg);
		}
	}

	public void onLoginRequest(Message msg){
		JSONObject data = (JSONObject) msg.toJSON().get(DATA);
		String username = (String) data.get(USERNAME);
		JSONObject responseData = new JSONObject();
		User usr = this._userDatabase.findByUsername(username);
		if(usr == null){
			responseData.put(LOG_IN_RESPONSE, LOG_IN_FAILURE);
			responseData.put(REASON, LOG_IN_USERNAME_NOT_FOUND);
			responseData.put(USERNAME, username);
		}
		else{
			if(userConnected(usr.getUsername())){ // Check if username is already connected
				responseData.put(LOG_IN_RESPONSE, LOG_IN_FAILURE);
				responseData.put(REASON, LOG_IN_ALREADY_CONNECTED);
				responseData.put(USERNAME, username);
			}
			else{
				addClient(username, msg);
				responseData.put(LOG_IN_RESPONSE, LOG_IN_SUCCESS);
				responseData.put(USERNAME, username);
			}
		}
		/* Adjust message with json response. networkJSON is defined in Constants.network */
		msg.setJSON(networkJSON(LOG_IN_REQUEST, responseData));
		send(msg);
	}

	/* No response is sent on logout request */
	public void onLogoutRequest(Message msg){
		removeClient(msg);
	}

	public void onSignupRequest(Message msg){

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