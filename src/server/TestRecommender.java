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
		//Factory
		DAOFactory d = DAOFactory.getInstance();
		//DAO's
		UserPrefDAO uprefDAO = d.getUserPrefDAO();
		FoodDAO fDao = d.getFoodDAO();
		UserDAO uDao = d.getUserDAO();

		//Création 2 users sans préf => similarité == 0
		User u = new User("test_username1","F");
		User v = new User("test_username2","M");

		//lancement du test
		expected_result = 0.0;
		UserUserStrategy uustrat = new UserUserStrategy(uprefDAO);
		result = uustrat.computeConstrainedPearsonCorrelation( u, v);
		checkResult(result, expected_result);

		//TODO moar tests 
	}

	public static void checkResult(double res, double expected_res){
		if(res == expected_res){
			System.out.println("Test OK");
		}
		else{
			System.out.println("Test FAILED");
		}
	}

}
