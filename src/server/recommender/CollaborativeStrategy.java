package recommender;

import items.Food;
import items.User;
import java.util.ArrayList;

public abstract class CollaborativeStrategy implements RecommendationStrategy{

	protected int recommendationsRequired = 0;
	protected ArrayList<Food> recommendations;
	protected ArrayList<Food> foodData;
	protected ArrayList<User> userData;

	@Override
	public void updateData(){  // Add DATA USER, FOOD

		// Save useful data (users prerences/ratings, food rating vector,  )
	}

	protected class RatingMatrix{

		private ArrayList<Food> foodItems;

	}

}
