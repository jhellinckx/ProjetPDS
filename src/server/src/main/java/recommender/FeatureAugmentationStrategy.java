package recommender;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

import org.calorycounter.shared.models.Recipe;
import org.calorycounter.shared.models.User;

public class FeatureAugmentationStrategy implements HybridationStrategy {

	private ContentBasedStrategy cb_strat;
	private FAUserUserStrategy cf_strat;
	private int nb_recom;
	private List<User> users;
	private List<Recipe> recipes;
	private User current_user;
	private Map<User, List<RecipeRatingPair>> cb_predictions;

	private void computeCbPredictions(){
		for (User user: users){
			cb_strat.updateData(recipes, users, user, nb_recom);
			cb_strat.recommend();
			cb_predictions.put(user, new ArrayList<>(cb_strat.getRatingsPrediction().values()));
		}
	}

	@Override
	public List<Recipe> recommend(){
		if (cb_predictions == null) {
			cb_predictions = new HashMap<>();
			computeCbPredictions();
		}
		cf_strat.updateData(recipes, users, current_user, nb_recom, cb_predictions);
		cf_strat.recommend();
		return new ArrayList<>();
	}

	@Override
	public void addRecommendationStrategy(RecommendationStrategy strat){
		if (strat instanceof ContentBasedStrategy){
			cb_strat = (ContentBasedStrategy) strat;
		} else if (strat instanceof CollaborativeStrategy){
			cf_strat = (FAUserUserStrategy) strat;
		}
	}

	@Override
	public void setRecommendationNumber(int recom){
		nb_recom = recom;
	}

	@Override
	public void updateData(List<Recipe> foods, List<User> u, User curUser, int recom){
		users = u;
		recipes = foods;
		nb_recom = recom;
		current_user = curUser;
	}

	@Override
	public Map<Long, RecipeRatingPair> getRatingsPrediction(){
		return cf_strat.getRatingsPrediction();
	}

}
