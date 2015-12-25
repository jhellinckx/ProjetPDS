import recommender.RecommenderSystem;
import recommender.VectorMath;

import java.util.ArrayList;
import java.util.Arrays;

public class TestRecommender {

	public static void main(String[] args) {
		
		RecommenderSystem recomsys = new RecommenderSystem();

		System.out.println("VectorMath Test");
		testVectorMath();

	}

	public static void testVectorMath(){
		ArrayList<Double> v1 = new ArrayList<Double>(Arrays.asList(0.5,10.15,12.12,3.125,2.5));
		ArrayList<Double> v2 = new ArrayList<Double>(Arrays.asList(5.4,88.5,7.84,2.5,30.25));

		System.out.println(VectorMath.euclideanNorm(Double.class, v1));
		System.out.println(VectorMath.dotProduct(Double.class, v1, v2));
	}

}
