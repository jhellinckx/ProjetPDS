package recommender;

import items.Food;
import items.User;
import java.util.ArrayList;

public interface RecommendationStrategy {
	
	public ArrayList<Food> recommend();
	public void updateData(ArrayList<Food> foods, ArrayList<User> users, User currentUser);
	public void setRecommendationsNumber(int nb);

}
