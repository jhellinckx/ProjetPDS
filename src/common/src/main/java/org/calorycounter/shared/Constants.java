package org.calorycounter.shared;

import java.nio.charset.StandardCharsets;
import java.nio.charset.Charset;
import java.net.InetAddress;

import org.json.simple.JSONObject;

public final class Constants {

	/* Network related constants, used to normalise communication */
	public static final class network {
		/* Network config */
		public static final int PORT = 2015;
		public static final String HOST = "127.0.0.1";
		public static final String EMULATOR_DEVICE_ADDRESS = "10.0.2.2";
		public static final int MAX_CLIENTS = 100;
		public static final int BUFFER_SIZE = 1000;
		public static final int INT_SIZE = 4;
		public static final String ENCODING = "UTF-8";

		/* Base keys of a JSON network message */
		public static final String REQUEST_TYPE = "requesttype";
		public static final String DATA = "data";

		/* Client connection handling */
		public static final String CONNECTION_NOTIFIER = "co_notifier";
		public static final String CONNECTION_STATUS = "co_status";
		public static final String CONNECTION_SUCCESS = "co_success";
		public static final String CONNECTION_FAILURE = "co_failure";
		
		/* Login / signup */
		public static final String REASON = "d_reason";
		public static final String USERNAME = "d_username";
		public static final String PASSWORD = "d_password";

		public static final String LOG_IN_REQUEST = "r_login";
		public static final String LOG_IN_RESPONSE = "d_login_res";
		public static final String LOG_IN_SUCCESS = "d_login_ok";
		public static final String LOG_IN_FAILURE = "d_login_nope";
		public static final String LOG_IN_USERNAME_NOT_FOUND = "d_login_nope_null";
		public static final String LOG_IN_ALREADY_CONNECTED = "d_login_nope_usr_connected";

		public static final String LOG_OUT_REQUEST = "r_logout";

		public static final String SIGN_UP_REQUEST = "r_signup";
		public static final String SIGN_UP_RESPONSE = "d_signup_res";
		public static final String SIGN_UP_SUCCESS = "d_signup_ok";
		public static final String SIGN_UP_FAILURE = "d_signup_nope";
		public static final String SIGN_UP_USERNAME_EXISTS = "d_signup_nope_usr_exists";
		public static final String SIGN_UP_ALREADY_CONNECTED = "d_signup_nope_usr_connected";



		public static String local_ip(){
			try{
				return InetAddress.getLocalHost().getHostAddress();
			}catch(Exception e){
				return "";
			}
		}

		public static JSONObject networkJSON(String request, JSONObject data){
			JSONObject obj = new JSONObject();
			obj.put(REQUEST_TYPE, request);
			obj.put(DATA, data);
			return obj;
		}
	}

	/* Console color output, used for logging */
	public static final class color{
		public static final String RESET = "\u001B[0m";
		public static final String BLACK = "\u001B[30m";
		public static final String RED = "\u001B[31m";
		public static final String GREEN = "\u001B[32m";
		public static final String YELLOW = "\u001B[33m";
		public static final String BLUE = "\u001B[34m";
		public static final String PURPLE = "\u001B[35m";
		public static final String CYAN = "\u001B[36m";
		public static final String WHITE = "\u001B[37m";
	}

	
	/* Misc */
	public static final boolean SHOW_LOG = true;
	public static final String IN = color.CYAN + " >> " + color.RESET;
	public static final String OUT = color.PURPLE + " << " + color.RESET;


	public static String repr(Object obj){
		return "[" + obj.getClass().getName() + "]";
	}

	public static String errorMessage(String msg, Object obj){
		return repr(obj) + color.RED + " Error : " + msg + color.RESET;  
	}

	public static String errorMessage(String msg, String str){
		return "[" + str +"]" + color.RED + " Error : " + msg + color.RESET;  
	}
}