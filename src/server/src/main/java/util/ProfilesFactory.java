package util;


import org.calorycounter.shared.models.User;
import items.RandomUserGenerator;

import java.util.Collections;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Random;

import dao.DAOFactory;
import dao.UserDAO;
import dao.UserPrefDAO;
import dao.RecipeDAO;
import dao.AllCategoriesDAO;


public class ProfilesFactory {

	private final int lower_bound = 3;
	private final int upper_bound = 5;

	private static final int THRESHOLD = 4;
	private static final long WAITING_TIME = 2000L;

	private RandomUserGenerator user_generator;
	private int profile_number;
	private List<User> users;
	private List<String> categories;
	private List<Long> _recipes_ids;
	private Map<String, List<Long>> category_recipes_map;

	private DAOFactory _daoFactory;
	private UserDAO _userDatabase;
	private RecipeDAO _recipeDatabase;
	private UserPrefDAO _userprefDatabase;
	private AllCategoriesDAO _categoriesDatabase;

	private final Object run_threads;
	private final Object profile_generator_lock;
	private Integer threads_nbr;
	private final Boolean blocked;


	public ProfilesFactory(){
		initDAOS();
		run_threads = new Object();
		profile_generator_lock = new Object();
		threads_nbr = 0;
		category_recipes_map = new HashMap<>();
		blocked = false;
	}

	private void createUsers(){
		user_generator = new RandomUserGenerator(profile_number, null, true);
		users = user_generator.get_random_users_list();
		for (User user: users){
			_userDatabase.create(user);
		}
	}

	private boolean contains(List<Long> alist, Long val){
		boolean contains = false;
		int size = alist.size();
		for (int i = 0; i < size; i++){
			contains = (alist.get(i).longValue() == val.longValue());
		}
		return contains;
	}

	private void fetchCategories(){
		categories = _categoriesDatabase.getAllRecipeCategories();
	}

	private void fetchRecipes(){
		_recipes_ids = _recipeDatabase.findAllRecipeIds();

		for (String category : categories){
			category_recipes_map.put(category, _recipeDatabase.getRecipeIdsByCategory(category));
		}
	}

	private List<Integer> getCategoriesPos(){
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

	private List<Long> generateRecipeIds(String category, List<Float> ratings){
		int size = ratings.size();
		List<Long> existing_ids = category_recipes_map.get(category);
		HashSet<String> already_added_ids = new HashSet<>();
		List<Long> chosen_ids = new ArrayList<>();
		Random rand = new Random();
		long id;
		String id_str;

		int i = 0;
		while (i < size){
			id = existing_ids.get(rand.nextInt(existing_ids.size()));
			id = (id == 0 ? 1 : id);
			id_str = Long.toString(id);
			if (!already_added_ids.contains(id_str)){
				already_added_ids.add(id_str);
				chosen_ids.add(id);
				i++;
			}
		}
		return chosen_ids;
	}

	private void addRatingsToDB(User user, List<Long> ids, List<Float> ratings){
		int size = ids.size();

		/*for (int i = 0; i < size; i++){
			_userprefDatabase.create(user.getId(), ids.get(i), ratings.get(i));
		}*/
		_userprefDatabase.createAll(user.getId(), ids, ratings);
	}

	private List<Long> addProfileToDB(User user, Map<Integer, List<Float>> profile){
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

	private void addNoiseForUser(User user, List<Long> existing_ids){
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
				++i;
			}
		}
		addRatingsToDB(user, chosen_ids, noise);
	}

	private List<Long> createUserProfile(User user){
		List<Integer> categories_pos = getCategoriesPos();
		Map<Integer, List<Float>> user_profile;
		User u = _userDatabase.findByUsername(user.getUsername());

		synchronized(profile_generator_lock){
		
			ProfileGenerator.setCategoryIds(categories_pos);
			ProfileGenerator.generateProfile();
			user_profile = ProfileGenerator.getProfile();
		}
		
		return addProfileToDB(user, user_profile);
	}

	private void initDAOS(){
		_daoFactory = DAOFactory.getInstance();
		_userDatabase = _daoFactory.getUserDAO();
		_userprefDatabase = _daoFactory.getUserPrefDAO();
		_categoriesDatabase = _daoFactory.getAllCategoriesDAO();
		_recipeDatabase = _daoFactory.getRecipeDAO();
	}

	private void launchThread(User u) throws InterruptedException{
		synchronized (run_threads){
			if (threads_nbr == THRESHOLD){
				run_threads.wait(WAITING_TIME);
			} else{
				++threads_nbr;
				(new ProfileThread(u)).start();
			}
		}
	}

	public void setProfileNumber(int nbr){
		profile_number = nbr;
	}

	public void createProfiles() throws InterruptedException{
		System.out.println("Creating Users ...");
		createUsers();
		System.out.println("Fetching Categories ...");
		fetchCategories();
		System.out.println("Fetching Recipes ...");
		fetchRecipes();

		for (int i = 0; i < profile_number; i++){
			System.out.println(Integer.toString( (int) (((((float)i)/profile_number))*100)) + " %");
			Collections.shuffle(categories);
			launchThread(users.get(i));
		}
	}

	private class ProfileThread extends Thread{

		private User user;

		public ProfileThread(User u){
			user = u;
		}

		private void wakeUpParent(){
			synchronized(run_threads){
				--threads_nbr;
				run_threads.notify();
			}
		}

		@Override
		public void run(){
			List<Long> ids;

			ids = createUserProfile(user);
			addNoiseForUser(user, ids);
			wakeUpParent();
		}
	}
}