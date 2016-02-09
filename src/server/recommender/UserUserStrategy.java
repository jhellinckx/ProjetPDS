package recommender;

import java.lang.Math;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import java.util.List;
import java.util.Iterator;
import items.Food;
import items.User;
import dao.UserPrefDAO;

public class UserUserStrategy extends CollaborativeStrategy {

	private static final float neutralRank = 2.5f; //MAGIC NUMBER
	private static final int NeighborSize = 20;	//MAGIC NUMBER - 20 has been proven to be a good starting point User-User.  ITEMLIKE


	private int dataSize; //ITEMLIKE


	public UserUserStrategy(UserPrefDAO pref){
		super(pref);

	}

	@Override
	public void updateData(ArrayList<Food> foods, ArrayList<User> users, User curUser){  

		//userData = users;
		//currentUser = curUser;
		dataSize = userData.size();
	}

	public double computeConstrainedPearsonCorrelation(User u, User v){
		/*Calculate similarity score between 2 users */
		HashMap uFoodRank = ratingMatrix.getFoodandRankForUser(u);
		HashMap vFoodRank = ratingMatrix.getFoodandRankForUser(v);

		//Find foods that both users have ranked
		Set<Food> commonRankedFoods = new HashSet<Food>(uFoodRank.keySet());
		commonRankedFoods.retainAll(vFoodRank.keySet());
		return(pearsonNumerator(uFoodRank,vFoodRank, commonRankedFoods)/pearsonDenominator(uFoodRank,vFoodRank, commonRankedFoods));
	}

	private double pearsonNumerator(HashMap u, HashMap v, Set commonFoods){
		double res = 0;
		Iterator<Food> it = commonFoods.iterator();
		while(it.hasNext()){ //Iterate through common ranked foods by users u and v
			Food nextFood = it.next();
			res += ((float)u.get(nextFood) - neutralRank)*((float)v.get(nextFood) - neutralRank);
		}
		return res;
	}

	private double pearsonDenominator(HashMap u,HashMap v, Set commonFoods){	
		double u_comp = pearsonDenominator_inner(u,commonFoods);
		double v_comp = pearsonDenominator_inner(v,commonFoods);
		double denom = Math.sqrt(u_comp)*Math.sqrt(v_comp);
		denom = (denom == 0) ? 1 : denom;
		return denom;
	}

	private double pearsonDenominator_inner(HashMap u, Set commonFoods){
		double res = 0;
		Iterator<Food> it = commonFoods.iterator();
		while(it.hasNext()){ //Iterate through common ranked foods by users u and v
			Food nextFood = it.next();
			res += Math.pow(((float)u.get(nextFood) - neutralRank) , 2);
		}
		return res;
	}

	/*
	private void computeRatingPredictions(){
		for (int i = 0; i < dataSize; i++){
			if (!currentUser.hasNotedFood(foodData.get(i))){
				calculateRatingPredictionForFood(foodData.get(i), i);
			}
		}
	}
	*/

	@Override
	public ArrayList<Food> recommend(){
		similarityMatrix = new SimilarityMatrix(dataSize);

		if (userData != null && currentUser != null){

			//computeRatingPredictions();
			//sortRatingPredictions();
			//extractRecommendations();
		}

		return recommendations;
		
	}

}