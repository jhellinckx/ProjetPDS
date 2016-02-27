package recommender;

import java.util.ArrayList;
import items.Food;
import items.User;

public interface HybridationStrategy {

	public ArrayList<Food> recommend();
	public void addRecommendationStrategy(RecommendationStrategy strat);
	public void setRecommendationNumber(int recom);
	public void updateData(ArrayList<Food> foods, ArrayList<User> users, User curUser, int recom);

}
