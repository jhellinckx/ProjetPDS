package recommender;

import items.Food;
import items.User;
import java.util.ArrayList;

public interface RecommendationStrategy {
	
	public ArrayList<Food> recommend();
	public void updateData(ArrayList<Food> foods, User currentUser, int nbRecom);
	public void updateData(ArrayList<Food> foods, ArrayList<User> users, User currentUser, int nbRecom);
	public void setRecommendationNumber(int recom);
	public int getRecommendationNumber();

}
 