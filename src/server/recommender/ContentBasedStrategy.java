package recommender;

import java.util.ArrayList;
import items.Food;
import items.User;

public abstract class ContentBasedStrategy implements RecommendationStrategy{

	protected int recommendationsRequired;

	public void updateData(ArrayList<Food> foods, ArrayList<User> users){
		
	}

	public void setRecommendationsNumber(int nb){
		recommendationsRequired = nb;
	}

}
