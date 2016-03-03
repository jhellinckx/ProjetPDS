package recommender;

import java.util.ArrayList;
import org.calorycounter.shared.models.Food;
import org.calorycounter.shared.models.User;

public abstract class ContentBasedStrategy implements RecommendationStrategy{

	protected int recommendationsRequired;

	public void updateData(ArrayList<Food> foods, ArrayList<User> users, User curUser, int nbRecom){
		
	}

	@Override
	public void setRecommendationNumber(int recom){
		recommendationsRequired = recom;
	}

	@Override
	public int getRecommendationNumber(){
		return recommendationsRequired;
	}

}
