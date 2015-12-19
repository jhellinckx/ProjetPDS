

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

public class doTest {
	
	public static void test(DAOFactory d) {
		boolean error = false;
		/*
		UserDAO uDao = d.getUserDAO();
		List<String> messages = uDao.findAll();
		System.out.println(messages);
		User u = new User("testest","F");
		error = uDao.create(u);
		System.out.print(error);
		List<String> messages2 = uDao.findAll();
		System.out.println(messages2);
		uDao.delete(u);
		List<String> messages3 = uDao.findAll();
		System.out.println(messages3);

		*/

		/*
		HashMap m = new HashMap();//16 cap
		User u = new User("testest","F");
		String r = "+";
		m.put(u,r);

		Set<User> s = m.keySet();
		
		for(User p : s){
			System.out.print(u.getUsername());
		}
		*/


		
		
		
		FoodDAO fDao = d.getFoodDAO();
		//List<String> messages4 = fDao.findAll();
		//System.out.println(messages4); 
		Food foodPref = fDao.findByName("Corn starch");
		//User user = new User("testTrol","K");
		//user.addToDeppreciatedFood(foodPref);
		//uDao.create(user);
		//uDao.delete(user);
		UserPrefDAO uprefDAO = d.getUserPrefDAO();
		HashMap m = uprefDAO.findUsersAndRankForFood(foodPref);

		Set<User> s = m.keySet();
		for(User p : s){
			System.out.print("\n"+p.getUsername()+"\n");
		}

	}

	public static void test_user_generator()
	{
		Random_user_generator r = new Random_user_generator(20); //arg = quantity of users wanted
		System.out.print(r.SQL_generateInsertionInstruction());

	}

	public static void test_findUsersAndRankForfood(DAOFactory d){
		UserPrefDAO uprefDAO = d.getUserPrefDAO();
		FoodDAO fDao = d.getFoodDAO();

		Food foodPref = fDao.findByName("Corn starch");
		HashMap m = uprefDAO.findUsersAndRankForFood(foodPref);

		Set<User> s = m.keySet();
		System.out.println("\nUtilisateurs , rank pour la Food : "+foodPref.getProductName()+"\n");
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
