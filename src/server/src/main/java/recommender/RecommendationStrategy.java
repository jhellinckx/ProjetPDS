package recommender;

import org.calorycounter.shared.models.Recipe;
import org.calorycounter.shared.models.User;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public interface RecommendationStrategy {
	
	public ArrayList<Recipe> recommend();
	public void updateData(List<Recipe> recipes, List<User> users, User currentUser, int nbRecom);
	public void setRecommendationNumber(int recom);
	public int getRecommendationNumber();
	public Map<Long, RecipeRatingPair> getRatingsPrediction();

}
 