import dao.DAOFactory;

public class testDAO {

	public static void main(String[] args) {
		DAOFactory daoFactory = DAOFactory.getInstance();
		doTest.test(daoFactory);	
		//doTest.test_user_generator();
	}

}
