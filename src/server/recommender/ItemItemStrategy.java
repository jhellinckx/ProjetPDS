package recommender;

/*
	From now on, Item-based recommendation for Food using a n-scale rating system (with n = natural  >= 2).
	Intermediate values, such as 3.5/5, taken into account.
*/


import java.lang.Math;
import java.util.ArrayList;

import items.Food;
import items.User;
import dao.UserPrefDAOImpl;
import java.util.HashSet;

public class ItemItemStrategy extends CollaborativeStrategy {

	private static final int NeighborSize = 30;

	private HashSet neighbor;

	public ItemItemStrategy(UserPrefDAOImpl pref){
		super(pref);
		neighbor = new HashSet(ItemItemStrategy.NeighborSize);
	}

	private float computeSimilarityDenominator(Food i, Food j){
		float denom = 0;
		return denom;
	}

	private float computeSimilarityNumerator(Food i, Food j){
		float num = 0;
		return num;
	}

	private float computeCosineSimilarity(Food i, Food j){
		float similarity = computeSimilarityNumerator(i, j);

		similarity = (similarity/computeSimilarityDenominator(i, j));

		return similarity;
	}

	private void calculateRatingPredictionForFood(Food food){
		
	}

	private void computeRatingPredictions(){

	}
	
	@Override
	public ArrayList<Food> recommend(){

		if (foodData != null && currentUser != null){

			computeRatingPredictions();
			sortRatingPredictions();
			extractRecommendations();
		}

		return recommendations;
		
	}

}
