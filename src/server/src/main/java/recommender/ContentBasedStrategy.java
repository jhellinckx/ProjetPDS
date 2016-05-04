package recommender;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.calorycounter.shared.models.Recipe;
import org.calorycounter.shared.models.User;

public abstract class ContentBasedStrategy implements RecommendationStrategy{

	protected int recommendationsRequired;

	public void updateData(List<Recipe> foods, List<User> users, User curUser, int nbRecom){
		
	}

	@Override
	public void setRecommendationNumber(int recom){
		recommendationsRequired = recom;
	}

	@Override
	public int getRecommendationNumber(){
		return recommendationsRequired;
	}

	@Override
	public Map<Long, RecipeRatingPair> getRatingsPrediction(){
		return new HashMap<>();
	}

}
