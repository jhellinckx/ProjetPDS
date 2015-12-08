

import java.util.List;

import dao.DAOFactory;
import dao.FoodDAO;
import dao.UserDAO;
import items.Food;
import items.User;

public class doTest {
	
	public static void test(DAOFactory d) {
		UserDAO uDao = d.getUserDao();
		List<String> messages = uDao.findAll();
		System.out.println(messages);
		User u = new User("testest","F");
		uDao.create(u);
		List<String> messages2 = uDao.findAll();
		System.out.println(messages2);
		uDao.delete(u);
		List<String> messages3 = uDao.findAll();
		System.out.println(messages3);
		
		FoodDAO fDao = d.getFoodDao();
		/*List<String> messages4 = fDao.findAll();
		System.out.println(messages4); */
		Food foodPref = fDao.findByName("oeuf");
		User user = new User("testTrol","K");
		user.addToDeppreciatedFood(foodPref);
		uDao.create(user);
		uDao.delete(user);
	}

}
