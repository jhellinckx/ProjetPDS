import dao.DAOFactory;

public class testDAO {

	public static void main(String[] args) {
		DAOFactory daoFactory = DAOFactory.getInstance();
		//doTest.test(daoFactory);	
		//doTest.test_user_generator(daoFactory);

		//doTest.test_findUsersAndRankForFood(daoFactory);
		//doTest.test_findFoodsAndRankForUser(daoFactory);
		//doTest.test_knowledgeBased(daoFactory);
		doTest.test_UserWithWeight(daoFactory);
	}

}