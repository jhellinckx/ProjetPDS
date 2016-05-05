package recommender;

import java.util.List;
import java.util.Map;

import org.calorycounter.shared.models.Recipe;
import org.calorycounter.shared.models.User;

public interface HybridationStrategy {

	public List<Recipe> recommend();
	public void addRecommendationStrategy(RecommendationStrategy strat);
	public void setRecommendationNumber(int recom);
	public void updateData(List<Recipe> foods, List<User> users, User curUser, int recom);
	public Map<Long, RecipeRatingPair> getRatingsPrediction();

}
