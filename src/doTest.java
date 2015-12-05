

import java.util.List;

import dao.DAOFactory;
import dao.FoodDAO;
import dao.UserDAO;
import items.User;

public class doTest {
	
	public static void test(DAOFactory d) {
		UserDAO uDao = d.getUtilisateurDao();
		List<String> messages = uDao.findAll();
		System.out.println(messages);
		User u = new User("testest","female");
		uDao.create(u);
		List<String> messages2 = uDao.findAll();
		System.out.println(messages2);
		uDao.delete(u);
		List<String> messages3 = uDao.findAll();
		System.out.println(messages3);
		
		FoodDAO fDao = d.getFoodDao();
		List<String> messages4 = fDao.findAll();
		System.out.println(messages4);
		
	}

}
