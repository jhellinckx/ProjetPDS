package recommender;

import java.util.*;

import dao.RecipeSimilarityDAO;
import org.calorycounter.shared.models.Recipe;
import items.CategoryRating;
import org.calorycounter.shared.models.User;
import dao.CategoryRatingDAO;
import static org.calorycounter.shared.Constants.network.*;


public class NearestNeighborStrategy extends ContentBasedStrategy {

	private List<Recipe> _recipes;
	private User _user;
	private int _recommendations;
	private Map<Recipe, Map<Recipe, Float>> neighborhoods;
	private RecipeSimilarityDAO similarityDAO;

	public NearestNeighborStrategy(RecipeSimilarityDAO dao){
		this.similarityDAO = dao;
	}

	private void getNeighborHoods(){
		neighborhoods = new HashMap<>();
		for (Recipe recipe: _recipes){
			neighborhoods.put(recipe, similarityDAO.getNearestNeighbor(recipe.getId(), 50));
		}
	}

	@Override
	public void updateData(List<Recipe> toFilter, List<User> users, User user, int recoms){
		super.updateData(toFilter, users, user, recoms);
		_recipes = toFilter;
		_user = user;
		_recommendations = 10;
		if (neighborhoods == null) {
			getNeighborHoods();
		}
	}

	protected float computeRating(Recipe recipe){
		Map<Recipe, Float> neighborhood = neighborhoods.get(recipe);
		float numerator = 0.0f;
		float denominator = 0.0f;

		for (Recipe item : neighborhood.keySet()){
			if (_user.hasNotedEdibleItem(item)){
				numerator += ((neighborhood.get(item))*(_user.getRankForEdibleItem(item)));
				denominator += ((neighborhood.get(item)));
			}
		}
		denominator = (denominator == 0 ? 1 : denominator);
		return (numerator/denominator);
	}

	protected void computePredictions(){
		for (Recipe recipe : _recipes){
			if(!_user.hasNotedEdibleItem(recipe)){
				addRatingPrediction(recipe, computeRating(recipe));
			}
		}
	}

	@Override
	public ArrayList<Recipe> recommend(){
		computePredictions();
		return null;
	}

}
