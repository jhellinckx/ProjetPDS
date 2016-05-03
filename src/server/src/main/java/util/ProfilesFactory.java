package util;


import org.calorycounter.shared.models.User;
import items.RandomUserGenerator;

import java.util.Collections;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.Random;

import dao.DAOFactory;
import dao.UserDAO;
import dao.FoodDAO;
import dao.UserPrefDAO;
import dao.CategoryRatingDAO;
import dao.AllCategoriesDAO;


public class ProfilesFactory {

	private static final int lower_bound = 3;
	private static final int upper_bound = 5;

	private static RandomUserGenerator user_generator;
	private static int profile_number;
	private static List<User> users;
	private static List<String> categories;

	private static DAOFactory _daoFactory;
	private static UserDAO _userDatabase;
	private static FoodDAO _foodDatabase;
	private static UserPrefDAO _userprefDatabase;
	private static CategoryRatingDAO _categoryRatingDatabase;
	private static AllCategoriesDAO _categoriesDatabase;

	private static void createUsers(){
		user_generator = new RandomUserGenerator(profile_number, null, true);
		users = user_generator.get_random_users_list();
	}

	private static void fetchCategories(){
		categories = _categoriesDatabase.getAllRecipeCategories();
	}

	private static List<Integer> getCategoriesPos(){
		Random rand = new Random();
		List<Integer> categories_pos = new ArrayList<>();
		int number = rand.nextInt(upper_bound+1) + lower_bound;
		number = (number >= upper_bound ? upper_bound : number);
		int categories_size = categories.size();

		int i = 0;
		int pos;
		while (i < number){
			pos = rand.nextInt(categories_size);
			if (!categories_pos.contains(pos)){
				categories_pos.add(pos);
				i++;
			}
		}
		return categories_pos;
	}

	private static void addNoiseForUser(User user){
		List<Float> noise = ProfileGenerator.generateNoise();

	}

	private static void createUserProfile(User user){
		List<Integer> categories_pos = getCategoriesPos();
		Map<Integer, List<Float>> user_profile;
		ProfileGenerator.setCategoryIds(categories_pos);
		ProfileGenerator.generateProfile();
		user_profile = ProfileGenerator.getProfile();
	}

	public static void initDAOS(){
		_daoFactory = DAOFactory.getInstance();
		_userDatabase = _daoFactory.getUserDAO();
		_foodDatabase = _daoFactory.getFoodDAO();
		_userprefDatabase = _daoFactory.getUserPrefDAO();
		_categoryRatingDatabase = _daoFactory.getCategoryRatingDAO();
		_categoriesDatabase = _daoFactory.getAllCategoriesDAO();
	}

	public static void setProfileNumber(int nbr){
		profile_number = nbr;
	}

	public static void createProfiles(){
		createUsers();
		fetchCategories();
		for (int i = 0; i < profile_number; i++){
			Collections.shuffle(categories);
			createUserProfile(users.get(i));
			addNoiseForUser(users.get(i));
		}
	}

}