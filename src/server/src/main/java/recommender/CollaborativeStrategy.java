package recommender;


import java.util.*;

import org.calorycounter.shared.models.Recipe;
import org.calorycounter.shared.models.User;
import dao.UserPrefDAO;


public abstract class CollaborativeStrategy implements RecommendationStrategy{

	protected int recommendationsRequired;
	protected ArrayList<Recipe> recommendations;
	protected Map<Long, RecipeRatingPair> ratingPredictions;       // nested pair <Food, Rating>, used for sorting recommendations.
	protected List<Recipe> recipeData = null;
	protected List<User> userData = null;
	protected User currentUser = null;
	protected RatingMatrix ratingMatrix;
	protected SimilarityMatrix similarityMatrix = null;

	public CollaborativeStrategy(UserPrefDAO pref){
		ratingPredictions = new HashMap<>();
		ratingMatrix = null;
		recommendationsRequired = 0;
		recommendations = new ArrayList<>();
		ratingMatrix = new RatingMatrix(pref);
	}

	@Override
	public void updateData(List<Recipe> recipes, List<User> users, User curUser, int nbRecom){
		recipeData = recipes;
		userData = users;
		currentUser = curUser;
		recommendationsRequired = nbRecom;
	}

	protected void resetData(){
		recommendations = new ArrayList<>();
		ratingPredictions = new HashMap<>();
	}

	protected final class RatingMatrix{		// nested class used to get all notes for a food/a user.

		private UserPrefDAO preferences;

		public RatingMatrix(UserPrefDAO pref){
			preferences = pref;
		}

		public List getRankForFood(Recipe food){
			return preferences.findRankForFood(food);
		}

		public HashMap getFoodandRankForUser(User user){
			return preferences.findRecipesAndRankForUser(user);
		}

	}

	@Override
	public void setRecommendationNumber(int recm){
		recommendationsRequired = recm;
	}

	@Override
	public int getRecommendationNumber(){
		return recommendationsRequired;
	}

	@Override
	public Map<Long, RecipeRatingPair> getRatingsPrediction(){
		return ratingPredictions;
	}

	protected void addRatingPrediction(Recipe f, float r){
		ratingPredictions.put(f.getId(), new RecipeRatingPair(f, r));
	}

	protected void extractRecommendations(){		// Extract the n recommendations in the sorted list ratingPredictions. (n = recommendationsRequired).
		int size = ratingPredictions.size();

		for (Long pos : ratingPredictions.keySet()){
			recommendations.add(ratingPredictions.get(pos).getFood());
		}

	}

	public void sortRatingPredictions(){
		Collections.sort(new ArrayList<>(ratingPredictions.values()), new Comparator<RecipeRatingPair> (){
			@Override
			public int compare(RecipeRatingPair p1, RecipeRatingPair p2){
				return (p1.getRating() < p2.getRating() ? 1 : (p1.getRating() == p2.getRating() ? 0 : -1));   // Test the order relationship between two RecipeRatingPair
			}

		});

	}

}