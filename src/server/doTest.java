

import java.util.List;

import dao.DAOFactory;
import dao.FoodDAO;
import dao.UserDAO;
import items.Food;
import items.User;
import items.Random_user_generator;

public class doTest {
	
	public static void test(DAOFactory d) {
		boolean error = false;

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
		
		/*
		FoodDAO fDao = d.getFoodDAO();
		//List<String> messages4 = fDao.findAll();
		//System.out.println(messages4); 
		Food foodPref = fDao.findByName("oeuf");
		User user = new User("testTrol","K");
		user.addToDeppreciatedFood(foodPref);
		uDao.create(user);
		uDao.delete(user);
		*/

	}

	public static void test_user_generator()
	{
		Random_user_generator r = new Random_user_generator(20); //arg = quantity of users wanted
		System.out.print(r.SQL_generateInsertionInstruction());

	}

}
