package org.calorycounter.shared;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.charset.Charset;
import java.net.InetAddress;
import java.security.ProtectionDomain;
import java.text.ParseException;
import java.util.HashMap;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.nio.file.Files;
import java.nio.file.Paths;

public final class Constants {

	/* Network related constants, used to normalise communication */
	public static final class network {
		/* Network config */
		public static final int MAX_CLIENTS = 100;
		public static final int BUFFER_SIZE = 1000;
		public static final int INT_SIZE = 4;
		public static final String ENCODING = "UTF-8";
		public static final int JSON_THRESHOLD = 15;

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

		/* Food code request */
		public static final String FOOD_CODE = "d_foodcode";
		public static final String FOOD_NAME = "d_foodname";
		public static final String FOOD_IMAGE_URL = "d_food_imgurl";
		public static final String FOOD_TOTAL_ENERGY = "d_food_total_energy";

		public static final String FOOD_CODE_REQUEST = "r_foodcode";
		public static final String FOOD_CODE_RESPONSE = "d_foodcode_res";
		public static final String FOOD_CODE_SUCCESS = "d_foodcode_ok";
		public static final String FOOD_CODE_FAILURE = "d_foodcode_nope";
		public static final String FOOD_CODE_NOT_FOUND = "d_foodcode_nope_null";

		/*RANDOM_UNRANKED_FOODS_REQUEST */
		public static final String RANDOM_UNRANKED_FOODS_REQUEST = "r_randfoods";
		public static final String RANDOM_UNRANKED_FOODS_RESPONSE = "d_randfoods_res";
		public static final String RANDOM_UNRANKED_FOODS_FAILURE = "d_randfoods_nope";
		public static final String RANDOM_UNRANKED_FOODS_NOT_FOUND = "d_randfoods_nope_empty";
		public static final String RANDOM_UNRANKED_FOODS_SUCCESS = "d_randfoods_ok";
		public static final String FOOD_IMAGE_URL_LIST = "d_food_imgurl_list";

		/* SEND RATINGS FOR ITEMS */
		public static final String SEND_RATINGS_REQUEST = "r_sendranks";
		public static final String FOOD_RATING = "d_rating";

		/* Sports */
		public static final String SPORTS_LIST_REQUEST = "r_sportslist";
		public static final String SPORTS_LIST_EMPTY = "d_sportslist_empty";
		public static final String SPORTS_LIST_SUCCESS = "d_sportslist_ok";
		public static final String SPORTS_LIST_FAILURE = "d_sportslist_nope";
		public static final String SPORTS_LIST_RESPONSE = "d_sportslist_res";
		public static final String SPORT_NAME = "d_sportname";
		public static final int SPORTS_LIST_SIZE = 45;
		public static final String CHOSEN_SPORT_REQUEST = "r_chosensport";
		public static final String SPORT_DURATION = "d_sportduration";



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

	public static final class client{
		public static final boolean FUCK_DEFAULT_BEHAVIOUR = false;
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