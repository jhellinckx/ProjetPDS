package recommender;

import java.util.ArrayList;
import java.util.List;

import org.calorycounter.shared.models.Recipe;
import org.calorycounter.shared.models.User;

public class FeatureAugmentationStrategy implements HybridationStrategy {

	public List<Recipe> recommend(){

		return new ArrayList<>();

	}

	public void addRecommendationStrategy(RecommendationStrategy strat){

	}

	public void setRecommendationNumber(int recom){

	}

	public void updateData(List<Recipe> foods, List<User> users, User curUser, int recom){

	}

}
