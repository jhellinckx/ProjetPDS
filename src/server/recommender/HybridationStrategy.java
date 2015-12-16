package recommender;

import java.util.ArrayList;
import items.Food;

public interface HybridationStrategy {

	public ArrayList<Food> recommend(ArrayList<RecommendationStrategy> strategies);

}
