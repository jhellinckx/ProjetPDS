package recommender;

import items.Food;
import items.User;
import java.util.ArrayList;

public interface RecommendationStrategy {
	
	public ArrayList<Food> recommend();
	public void updateData();    // Add FOOD, USER

}
