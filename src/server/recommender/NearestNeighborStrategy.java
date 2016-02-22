package recommender;

import java.util.ArrayList;
import items.Food;

public class NearestNeighborStrategy extends ContentBasedStrategy {
	private ArrayList<Food> _foodToFilter;
	private User _user;
	private int _recommandations;

	@Override
	public void updateData(ArrayList<Food> toFilter, User user, int recoms){  
		_foodToFilter = toFilter;
		_user = user;
		_recommandations = recoms;
	}


	/* Called if no filtering is required. In that case, the system has to explore
	the database to find relevant food and mark them */
	@Override
	public ArrayList<Food> recommend(){

		return new ArrayList<Food>();
		
	}

	/*  */
	public ArrayList<Food> recommend(ArrayList<Food> toFilter){

	}

}
