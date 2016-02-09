package recommender;

/*
	From now on, Item-based recommendation for Food using a n-scale rating system (with n = natural  >= 2).
	Intermediate values, such as 3.5/5, taken into account.
*/


import java.lang.Math;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import items.Food;
import items.User;
import dao.UserPrefDAO;

public class ItemItemStrategy extends CollaborativeStrategy {

	private static final int NEIGHBORHOOD_SIZE = 30;			// 30 has been proven to be an effective neighborhood size for Item-Item.

	private Map<Double, Integer> neighbor;
	private int dataSize;

	public ItemItemStrategy(UserPrefDAO pref){
		super(pref);
	}

	@Override
	public void updateData(ArrayList<Food> foods, ArrayList<User> users, User curUser){  

		foodData = foods;
		currentUser = curUser;
		dataSize = foodData.size();
	}


	/*
	*** Calculate the Cosine Similarity between the ratings vector of two different Items.	
	***
	***
	*/


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



	private void extractNearestNeighbor(int foodIndex){
		neighbor = similarityMatrix.getNearestNeighborhood(foodIndex);
	}


	/*
	**	Compute the rating prediction according to a formula defined by B.M. Sarwar et al.
	*/

	private float computeRating(){
		float denom = 0;
		float num = 0;
		for (Map.Entry<Double, Integer> entry : neighbor.entrySet()){

			// num = Similarity between Food 1 and Food 2  *  Rating of currentUser for Food 2 (if the rating of Food 1 needs to be predicted).

			num += (entry.getKey().floatValue())*currentUser.getRankForFood(foodData.get(entry.getValue()));
			denom += (float) Math.abs(entry.getKey());
		}
		denom = (denom == 0) ? 1 : denom;
		return (num/denom);

	}



	private void calculateRatingPredictionForFood(Food food, int foodIndex){
		for (int i = 0; i < dataSize; i++){
			if (foodData.get(i).getId() != food.getId() && currentUser.hasNotedFood(foodData.get(i))){
				double similarity = computeCosineSimilarity(food, foodData.get(i));
				similarityMatrix.put(foodIndex, i, similarity);
			}

		}
		extractNearestNeighbor(foodIndex);
		addRatingPrediction(food, computeRating());
		
	}

	private void computeRatingPredictions(){
		for (int i = 0; i < dataSize; i++){
			if (!currentUser.hasNotedFood(foodData.get(i))){
				calculateRatingPredictionForFood(foodData.get(i), i);
			}
			else{
				addRatingPrediction(foodData.get(i), currentUser.getRankForFood(foodData.get(i)));
			}
		}

	}
	
	@Override
	public ArrayList<Food> recommend(){

		similarityMatrix = new SimilarityMatrix(dataSize, ItemItemStrategy.NEIGHBORHOOD_SIZE);

		if (foodData != null && currentUser != null){

			computeRatingPredictions();
			sortRatingPredictions();
			extractRecommendations();
		}

		return recommendations;
		
	}

}
