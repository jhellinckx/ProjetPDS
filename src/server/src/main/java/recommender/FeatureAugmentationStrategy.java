package recommender;

import java.util.ArrayList;
import org.calorycounter.shared.models.Food;
import org.calorycounter.shared.models.User;

public class FeatureAugmentationStrategy implements HybridationStrategy {

	public ArrayList<Food> recommend(){

		return new ArrayList<Food>();

	}

	public void addRecommendationStrategy(RecommendationStrategy strat){

	}

	public void setRecommendationNumber(int recom){

	}

	public void updateData(ArrayList<Food> foods, ArrayList<User> users, User curUser, int recom){

	}

}
