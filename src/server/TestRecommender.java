import recommender.RecommenderSystem;
import recommender.VectorMath;
import recommender.UserUserStrategy;

import java.util.ArrayList;
import java.util.Arrays;

import dao.DAOFactory;
import dao.FoodDAO;
import dao.UserDAO;
import dao.UserPrefDAO;

import items.Food;
import items.User;
import items.Random_user_generator;

public class TestRecommender {

	public static void main(String[] args) {
		
		RecommenderSystem recomsys = new RecommenderSystem();

		System.out.println("VectorMath Test");
		testVectorMath();

		System.out.println("\nUser-User test");
		testUserUserRecom();

	}

	public static void testVectorMath(){
		ArrayList<Double> v1 = new ArrayList<Double>(Arrays.asList(0.5,10.15,12.12,3.125,2.5));
		ArrayList<Double> v2 = new ArrayList<Double>(Arrays.asList(5.4,88.5,7.84,2.5,30.25));

		System.out.println(VectorMath.euclideanNorm(Double.class, v1));
		System.out.println(VectorMath.dotProduct(Double.class, v1, v2));
	}

	public static void testUserUserRecom(){
		double expected_result;
		double result;
		int testnr = 0;
		
		DAOFactory d = DAOFactory.getInstance();
		//DAO's
		UserPrefDAO uprefDAO = d.getUserPrefDAO();
		FoodDAO fDao = d.getFoodDAO();
		UserDAO uDao = d.getUserDAO();

		Random_user_generator r = new Random_user_generator(10, fDao, true);

		//Création 2 users sans préf => similarité == 0
		User u = new User("test_username1","F");
		User v = new User("test_username2","M");

		//lancement du test 1
		testnr++;
		System.out.println("\nTEST "+testnr+": BEGIN");
		expected_result = 0.0;
		UserUserStrategy uustrat = new UserUserStrategy(uprefDAO);
		result = uustrat.computeConstrainedPearsonCorrelation(u,v);
		checkResult(result, expected_result, testnr);

		//test 2
		testnr++;
		System.out.println("\nTEST "+testnr+": BEGIN");
		expected_result = 1.0000000000000002;

		//init users 
		Food foodPref = fDao.findById(164l);
		u.addRankedFood(foodPref, 4.0f);
		v.addRankedFood(foodPref, 4.0f);
		foodPref = fDao.findById(170l);
		u.addRankedFood(foodPref, 4.0f);
		v.addRankedFood(foodPref, 4.0f);
		if(uDao.create(u)){
			u = uDao.findByUsername(u.getUsername());
		}
		if(uDao.create(v)){
			v = uDao.findByUsername(v.getUsername());
		}

		result = uustrat.computeConstrainedPearsonCorrelation(u,v);
		checkResult(result, expected_result, testnr);



		//TODO moar tests 
	}

	public static void checkResult(double res, double expected_res, int testnr){
		if(res == expected_res){
			System.out.println("Test "+testnr+": OK");
		}
		else{
			System.out.println("Test "+testnr+": FAILED");
		}
	}

}
