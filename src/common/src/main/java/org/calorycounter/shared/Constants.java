package org.calorycounter.shared;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.charset.Charset;
import java.net.InetAddress;
import java.security.ProtectionDomain;
import java.text.ParseException;
import java.util.HashMap;

import java.util.Arrays;
import java.util.List;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.nio.file.Files;
import java.nio.file.Paths;

public final class Constants {

	/* Network related constants, used to normalise communication */
	public static final class network {
		/* PIZZA MAN CONFIG YO */
		public static final String PIZZA_MAN_USERNAME = "pizza_man";
		public static final List<Long> PIZZA_MAN_UNRANKED_FOODS = Arrays.asList(new Long(58595L), new Long(58401L), new Long(58400L),new Long(38790L), new Long(35350L), new Long(40413L), new Long(29348L), new Long(20575L), new Long(7351L)); 
		/* Network config */
		public static final String LOCALHOST = "127.0.0.1";
		public static final String LOCALHOST_STRING = "localhost";
		public static final String EMULATOR_DEVICE_ADDRESS = "10.0.2.2";
		public static final int MAX_CLIENTS = 100;
		public static final int BUFFER_SIZE = 100000;
		public static final int INT_SIZE = 4;
		public static final String ENCODING = "UTF-8";
		public static final int JSON_THRESHOLD = 1000;

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
		public static final String FOOD_ID = "f_id";
		public static final String FOOD_URL = "f_url";
		public static final String FOOD_CODE = "f_code";
		public static final String FOOD_NAME = "f_p_name";
		public static final String FOOD_IMAGE_URL = "f_image";
		public static final String FOOD_TOTAL_ENERGY = "f_nrj";
		public static final String FOOD_TOTAL_FAT = "f_fat";
		public static final String FOOD_TOTAL_PROTEINS = "f_proteins";
		public static final String FOOD_TOTAL_SATURATED_FAT = "f_s_fat";
		public static final String FOOD_TOTAL_CARBOHYDRATES = "f_carbo";
		public static final String FOOD_TOTAL_SUGARS = "f_sugars";
		public static final String FOOD_TOTAL_SODIUM = "f_sodiums";
		public static final String FOOD_QUANTITY = "f_quantity";

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
		public static final int NUMBER_RANDOM_FOODS = 9;

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

		/* Personal Data */
		public static final String UPDATE_DATA_REQUEST = "r_updatedata";
		public static final String UPDATE_DATA_GENDER = "d_updatadata_gender";
		public static final String UPDATE_DATA_WEIGHT = "d_updatedata_weight";
		public static final String DATA_REQUEST = "r_data";

		/* History */ 
		public static final String HISTORY_REQUEST = "r_history";
		public static final String HISTORY_NAME = "d_history_name";
		public static final String HISTORY_DATE = "d_history_date";
		public static final String HISTORY_NAMES_DATES = "d_history_dates";

		/* Recommendation */
		public static final int RECOMMENDATIONS_REQUIRED = 20;
		public static final String RECOMMEND_REQUEST = "recom_request";
		public static final String RECOMMENDED_FOOD_LIST = "recom_foodlist";

		/* Past Food */
		public static final String PAST_FOODS_LIST = "d_past_foodslist";

		/* Recommendation Constraints */
		public static final String MAX_ENERGY = "d_maxenergy";
		public static final String MAX_FAT = "d_maxfat";
		public static final String MAX_PROT = "d_maxprot";
		public static final String MAX_CARBOHYDRATES = "d_maxcarbo";

		/*
		Human's daily intake requirements
		Info's from : http://www.mydailyintake.net/daily-intake-levels/
		*/
		public static final float HUMAN_DAILY_FAT = 70; //g
		public static final float HUMAN_DAILY_PROTEINS = 50; //g
		public static final float HUMAN_DAILY_SATURATED_FAT = 20; //g
		public static final float HUMAN_DAILY_CARBOHYDRATES = 310; //g
		public static final float HUMAN_DAILY_SUGARS = 90; //g
		public static final float HUMAN_DAILY_SODIUM = 2.3f; //g

		/* 
		Men & Women's daily energy/calories
		Energetic infos are an average from : http://onmangequoi.lamutuellegenerale.fr/besoins-alimentaires
		*/
		public static final float MEN_DAILY_ENERGY = 9734.31f; //kJoule (2325 kcal)
		public static final float WOMEN_DAILY_ENERGY = 7745.58f; //kJoule (1850 kcal)
		public static final float TEEN_DAILY_ENERGY = 11304.36f; //kJoule (2700 kcal)
		public static final float CHILD_DAILY_ENERGY = 9286.3224f; //kJoule (2218 kcal)
		public static final float TOTAL_MEAL_PER_DAY = 4;
		public static final float CAL_TO_JOULE_FACTOR = 4.1868f;

		/* NearestNeighbor values */
		public static final float DEFAULT_RATING = 2.5f;

		/* Possible Gender */
		public static final String MAN = "M";
		public static final String WOMAN = "F";
		public static final String CHILD = "C";
		public static final String TEEN = "T";


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