

import java.util.List;

import dao.DAOFactory;
import dao.FoodDAO;
import dao.UserDAO;
import dao.UserPrefDAO;
import items.Food;
import items.User;
import items.Random_user_generator;
import java.util.HashMap;
import java.util.Set;
import java.util.ArrayList;
import recommender.KnowledgeBasedFilter;

public class doTest {
	
	public static void test(DAOFactory d) {
		boolean error = false;
		
		UserDAO uDao = d.getUserDAO();
		List<String> messages = uDao.findAll();
		System.out.println(messages);
		User u = new User("test_username2","F");
		error = uDao.create(u);
		System.out.println(error);
		List<String> messages2 = uDao.findAll();
		System.out.println(messages2);
		uDao.delete(u);
		List<String> messages3 = uDao.findAll();
		System.out.println(messages3);
	}

	public static void test_user_generator(DAOFactory d)
	{
		/*
		Long start_time = System.currentTimeMillis();
		
		UserDAO uDao = d.getUserDAO();
		uDao.quick_createRandomUsers(10);

		Long end_time = System.currentTimeMillis();
		System.out.println("\n"+(end_time-start_time)+"ms");
		*/
	}

	public static void test_findUsersAndRankForFood(DAOFactory d){
		//init des DAO
		UserPrefDAO uprefDAO = d.getUserPrefDAO();
		FoodDAO fDao = d.getFoodDAO();
		UserDAO uDao = d.getUserDAO();

		//création User random 
		Random_user_generator r = new Random_user_generator(1, fDao, false);
		ArrayList<User> randUserList = r.get_random_users_list();
		User userTest = randUserList.get(0);

		//création user avec food test
		User userTest2 = new User("test_username4","F");
		long id = 164;

		Food foodPref = fDao.findByName("Corn starch");
		float rank = 4.5f;
		userTest2.addRankedFood(foodPref, rank);

		//création des users dans la db
		uDao.create(userTest);
		uDao.create(userTest2);

		
		List<Float> m = uprefDAO.findRankForFood(foodPref);
		System.out.println("\n rank pour : "+foodPref.getProductName()+"\n");
		for(float rating : m){
			System.out.println(rating + "\n");
		}
		
	}

	public static void test_findFoodsAndRankForUser(DAOFactory d){
		UserPrefDAO uprefDAO = d.getUserPrefDAO();
		FoodDAO fDao = d.getFoodDAO();
		UserDAO uDao = d.getUserDAO();

		User user = uDao.findById((long)1); // cause une erreur si pas d'user avec cet id dans votre table
		HashMap m = uprefDAO.findFoodsAndRankForUser(user); 
		Set<Food> s = m.keySet();
		System.out.println("\nFoods , rank pour l'User' : "+user.getUsername()+"\n");
		for(Food f : s){
			System.out.println(f.getProductName()+" , "+m.get(f)+"\n");
		}
	}

	public static void test_knowledgeBased(DAOFactory d) {
		FoodDAO fDAO = d.getFoodDAO();
		User u = new User("test_username2","M");
		User u2 = new User("test_username2","F");
		KnowledgeBasedFilter k = new KnowledgeBasedFilter(fDAO,u);
		List<Food> testFood = new ArrayList<Food>();
		testFood.add(fDAO.findByCode("96092521"));
		List<Food> a=k.recommend();
		System.out.println(Integer.toString(a.size())); //prints the number of food matching patterns
		Food x = a.get(0);
		System.out.println(x.toString());
		a=k.recommend(testFood);
		System.out.println(Integer.toString(a.size()));
		x=a.get(0);
		System.out.println(x.toString());
	}

	public static void test_UserWithWeight(DAOFactory d) {
		UserDAO uDAO = d.getUserDAO();
		User u = new User("testWeight","M",87.0f);
		User u2 = uDAO.findByUsername("omg");
		User u3 = new User("whynot","C");
		//uDAO.create(u);
		//uDAO.create(u3);
		//uDAO.updateUserWeight(u2,75.0f);
		//uDAO.updateUserGender(u2,"K");
		float we= u2.getWeight();
		System.out.println(Float.toString(we));

	}
}
