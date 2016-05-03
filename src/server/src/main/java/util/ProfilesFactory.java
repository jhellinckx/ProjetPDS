package util;


import org.calorycounter.shared.models.User;
import items.RandomUserGenerator;

import java.util.Collections;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;
import java.util.Random;

import dao.DAOFactory;
import dao.UserDAO;
import dao.UserPrefDAO;
import dao.RecipeDAO;
import dao.AllCategoriesDAO;


public class ProfilesFactory {

	private static final int lower_bound = 3;
	private static final int upper_bound = 5;

	private static RandomUserGenerator user_generator;
	private static int profile_number;
	private static List<User> users;
	private static List<String> categories;
	private static List<Long> _recipes_ids;
	private static Map<String, List<Long>> category_recipes_map = new HashMap<>();

	private static DAOFactory _daoFactory;
	private static UserDAO _userDatabase;
	private static RecipeDAO _recipeDatabase;
	private static UserPrefDAO _userprefDatabase;
	private static AllCategoriesDAO _categoriesDatabase;

	private static void createUsers(){
		user_generator = new RandomUserGenerator(profile_number, null, true);
		users = user_generator.get_random_users_list();
		for (User user: users){
			_userDatabase.create(user);
		}
	}

	private static boolean contains(List<Long> alist, Long val){
		boolean contains = false;
		int size = alist.size();
		for (int i = 0; !contains && i < size; i++){
			contains = (alist.get(i).longValue() == val.longValue());
		}
		return contains;
	}

	private static void fetchCategories(){
		categories = _categoriesDatabase.getAllRecipeCategories();
	}

	private static void fetchRecipes(){
		for (int i = 0; i < categories.size()/2; i++){
			category_recipes_map.put(categories.get(i), _recipeDatabase.getRecipeIdsByCategory(categories.get(i)));
		}
		_recipes_ids = _recipeDatabase.findAllRecipeIds();
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

	private static List<Long> generateRecipeIds(String category, List<Float> ratings){
		int size = ratings.size();
		List<Long> existing_ids = category_recipes_map.get(category);
		existing_ids = (existing_ids == null ? _recipeDatabase.getRecipeIdsByCategory(category) : existing_ids);
		List<Long> chosen_ids = new ArrayList<>();
		Random rand = new Random();
		long id;

		int i = 0;
		while (i < size){
			id = existing_ids.get(rand.nextInt(existing_ids.size()));
			id = (id == 0 ? 1 : id);
			if (!contains(chosen_ids, id)){
				chosen_ids.add(id);
				i++;
			}
		}
		return chosen_ids;
	}

	private static void addRatingsToDB(User user, List<Long> ids, List<Float> ratings){
		int size = ids.size();

		for (int i = 0; i < size; i++){
			_userprefDatabase.create(user.getId(), ids.get(i), ratings.get(i));
		}
	}

	private static List<Long> addProfileToDB(User user, Map<Integer, List<Float>> profile){
		String category;
		List<Float> ratings;
		List<Long> recipe_ids = new ArrayList<>();

		for (Integer pos : profile.keySet()){
			category = categories.get(pos);
			ratings = profile.get(pos);
			recipe_ids = generateRecipeIds(category, ratings);
			addRatingsToDB(user, recipe_ids, ratings);
		}
		return recipe_ids;
	}

	private static void addNoiseForUser(User user, List<Long> existing_ids){
		List<Float> noise = ProfileGenerator.generateNoise();
		User u = _userDatabase.findByUsername(user.getUsername());
		List<Long> chosen_ids = new ArrayList<>();
		Long id;
		int size = noise.size();
		Random rand = new Random();

		int i = 0;
		while(i < size){
			id = _recipes_ids.get(rand.nextInt(_recipes_ids.size()));
			if (!contains(existing_ids, id)){
				chosen_ids.add(id);
				++id;
			}
		}
		addRatingsToDB(user, chosen_ids, noise);
	}

	private static List<Long> createUserProfile(User user){
		List<Integer> categories_pos = getCategoriesPos();
		Map<Integer, List<Float>> user_profile;
		User u = _userDatabase.findByUsername(user.getUsername());
		
		ProfileGenerator.setCategoryIds(categories_pos);
		ProfileGenerator.generateProfile();
		user_profile = ProfileGenerator.getProfile();
		return addProfileToDB(user, user_profile);
	}

	public static void initDAOS(){
		_daoFactory = DAOFactory.getInstance();
		_userDatabase = _daoFactory.getUserDAO();
		_userprefDatabase = _daoFactory.getUserPrefDAO();
		_categoriesDatabase = _daoFactory.getAllCategoriesDAO();
		_recipeDatabase = _daoFactory.getRecipeDAO();
	}

	public static void setProfileNumber(int nbr){
		profile_number = nbr;
	}

	public static void createProfiles(){
		List<Long> ids;

		createUsers();
		fetchCategories();
		fetchRecipes();
		for (int i = 0; i < profile_number; i++){
			System.out.println(Integer.toString( (int) (((float) (i/profile_number))*100)) + " %");
			Collections.shuffle(categories);
			ids = createUserProfile(users.get(i));
			addNoiseForUser(users.get(i), ids);
		}
	}

}