package recommender;

import java.lang.Math;
import java.util.ArrayList;

import items.Food;
import items.User;
import dao.UserPrefDAOImpl;

public class ItemItemStrategy extends CollaborativeStrategy {

	public ItemItemStrategy(UserPrefDAOImpl pref){
		super(pref);
	}
	
	@Override
	public ArrayList<Food> recommend(){


		return new ArrayList<Food>();
		
	}

}
