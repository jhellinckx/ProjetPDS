package recommender;



/*****

Class not finished nor functionnal. Defines only structure of Item-Item and User-User.
Further changes will be made.

****/



import java.util.ArrayList;
import java.util.List;
import java.util.Collections;
import java.util.HashMap;
import java.util.Comparator;

import items.Food;
import items.User;
import dao.UserPrefDAO;


public abstract class CollaborativeStrategy implements RecommendationStrategy{

	protected int recommendationsRequired;
	protected ArrayList<Food> recommendations;
	protected ArrayList<FoodRatingPair> ratingPredictions;       // nested pair <Food, Rating>, used for sorting recommendations.
	protected ArrayList<Food> foodData = null;
	protected ArrayList<User> userData = null;
	protected User currentUser = null;
	protected RatingMatrix ratingMatrix;
	protected SimilarityMatrix similarityMatrix = null;

	public CollaborativeStrategy(UserPrefDAO pref){
		ratingPredictions = new ArrayList<FoodRatingPair>();
		ratingMatrix = new RatingMatrix(pref);
		recommendationsRequired = 0;
	}

	@Override
	public void updateData(ArrayList<Food> foods, ArrayList<User> users, User curUser){  

		foodData = foods;
		userData = users;
		currentUser = curUser;
	}

	public void setRecommendationsNumber(int nb){
		recommendationsRequired = nb;
	}

	protected final class RatingMatrix{		// nested class used to get all notes for a food/a user.

		private UserPrefDAO preferences;

		public RatingMatrix(UserPrefDAO pref){
			preferences = pref;
		}

		public List getRankForFood(Food food){
			return preferences.findRankForFood(food);
		}

		public HashMap getFoodandRankForUser(User user){
			return preferences.findFoodsAndRankForUser(user);
		}

	}

	protected final class FoodRatingPair{		// nested Pair used by the Item-Item and User-User algorithms.

		private Food food;
		private float rating;

		public FoodRatingPair(Food f, float r){
			food = f;
			rating = r;
		}

		public Food getFood(){
			return food;
		}

		public float getRating(){
			return rating;
		}
	}

	protected void extractRecommendations(){		// Extract the n recommendations in the sorted list ratingPredictions. (n = recommendationsRequired).
		int size = ratingPredictions.size();

		for (int i = 0; i < size && i < recommendationsRequired; i++){
			recommendations.add(ratingPredictions.get(i).getFood());
		}

	}

	public void sortRatingPredictions(){
		Collections.sort(ratingPredictions, new Comparator<FoodRatingPair> (){
			@Override
			public int compare(FoodRatingPair p1, FoodRatingPair p2){
				return (p1.getRating() < p2.getRating() ? 1 : (p1.getRating() == p2.getRating() ? 0 : -1));   // Test the order relationship between two FoodRatingPair
			}

		});

	}

}
