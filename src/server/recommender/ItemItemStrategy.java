package recommender;

/*
	From now on, Item-based recommendation for Food using a n-scale rating system (with n = natural  >= 2).
	Intermediate values, such as 3.5/5, taken into account.
*/


import java.lang.Math;
import java.util.ArrayList;
import java.util.List;

import items.Food;
import items.User;
import dao.UserPrefDAO;
import java.util.HashSet;

public class ItemItemStrategy extends CollaborativeStrategy {

	private static final int NeighborSize = 30;			// 30 has been proven to be an effective neighborhood size for Item-Item.

	private HashSet neighbor;
	private int dataSize;

	public ItemItemStrategy(UserPrefDAO pref){
		super(pref);
		neighbor = new HashSet(ItemItemStrategy.NeighborSize);
	}

	@Override
	public void updateData(ArrayList<Food> foods, ArrayList<User> users, User curUser){  

		foodData = foods;
		currentUser = curUser;
		dataSize = foodData.size();
	}

	private double computeSimilarityDenominator(List<Float> i_ratings, List<Float> j_ratings){
		double denom = VectorMath.euclideanNorm(Float.class, i_ratings);
		denom *= VectorMath.euclideanNorm(Float.class, j_ratings);

		denom = (denom == 0) ? 1 : denom;			// This is used to prevent a zero-division error. 
		return denom;
	}

	private double computeSimilarityNumerator(List<Float> i_ratings, List<Float> j_ratings){
		double num = VectorMath.dotProduct(Float.class, i_ratings, j_ratings);
		return num;
	}

	private double computeCosineSimilarity(Food i, Food j){
		List<Float> i_ratings = ratingMatrix.getRankForFood(i);
		List<Float> j_ratings = ratingMatrix.getRankForFood(j);
		double similarity = computeSimilarityNumerator(i_ratings, j_ratings);

		similarity = (similarity/computeSimilarityDenominator(i_ratings, j_ratings));

		return similarity;
	}

	private void extractNearestNeighbor(Food food, int foodIndex){

	}

	private void calculateRatingPredictionForFood(Food food, int foodIndex){
		for (int i = 0; i < dataSize; i++){
			if (foodData.get(i).getId() != food.getId() && currentUser.hasNotedFood(foodData.get(i))){
				double similarity = computeCosineSimilarity(food, foodData.get(i));
				similarityMatrix.put(foodIndex, i, similarity);
				extractNearestNeighbor(food, foodIndex);
			}

		}
		
	}

	private void computeRatingPredictions(){
		for (int i = 0; i < dataSize; i++){
			if (!currentUser.hasNotedFood(foodData.get(i))){
				calculateRatingPredictionForFood(foodData.get(i), i);
			}
		}

	}
	
	@Override
	public ArrayList<Food> recommend(){

		similarityMatrix = new SimilarityMatrix(dataSize);

		if (foodData != null && currentUser != null){

			computeRatingPredictions();
			sortRatingPredictions();
			extractRecommendations();
		}

		return recommendations;
		
	}

}
