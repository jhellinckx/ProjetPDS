

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
		userTest2.addRankedfood(foodPref, rank);

		//création des users dans la db
		uDao.create(userTest);
		uDao.create(userTest2);

		
		HashMap m = uprefDAO.findUsersAndRankForFood(foodPref);
		Set<User> s = m.keySet();
		System.out.println("\n[Utilisateurs,rank] pour : "+foodPref.getProductName()+"\n");
		for(User p : s){
			System.out.println(p.getUsername()+" , "+m.get(p)+"\n");
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

}
