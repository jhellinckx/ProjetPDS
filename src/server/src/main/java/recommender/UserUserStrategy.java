package recommender;

import java.lang.Math;
import java.util.*;

import org.calorycounter.shared.models.Food;
import org.calorycounter.shared.models.Recipe;
import org.calorycounter.shared.models.User;
import dao.UserPrefDAO;

public class UserUserStrategy extends CollaborativeStrategy {

	protected static final float neutralRank = 2.5f; //MAGIC NUMBER
	protected static final int NEIGHBORHOOD_SIZE = 30;	//MAGIC NUMBER - 20 has been proven to be a good starting point User-User.  ITEMLIKE

	protected int dataSize; //ITEMLIKE
	protected ArrayList<Double> similarityVec = new ArrayList<>();


	public UserUserStrategy(UserPrefDAO pref){
		super(pref);
	}

	@Override
	public void updateData(List<Recipe> recipes, List<User> users, User curUser, int nbRecom){
		userData = users;
		currentUser = curUser;
		dataSize = userData.size();
		recipeData = recipes;
		recommendationsRequired = nbRecom;
		resetData();
	}

	public double computeConstrainedPearsonCorrelation(User u, User v){
		/*Calculate similarity score between 2 users */
		HashMap uFoodRank = u.getRankedEdibleItems();
		HashMap vFoodRank = v.getRankedEdibleItems();

		//Find foods that both users have ranked
		Set<Recipe> commonRankedFoods = new HashSet<Recipe>(uFoodRank.keySet());
		commonRankedFoods.retainAll(vFoodRank.keySet());
		return(pearsonNumerator(uFoodRank,vFoodRank, commonRankedFoods)/pearsonDenominator(uFoodRank,vFoodRank, commonRankedFoods));
	}

	protected double pearsonNumerator(HashMap u, HashMap v, Set commonFoods){
		double res = 0;
		Iterator<Recipe> it = commonFoods.iterator();
		while(it.hasNext()){ //Iterate through common ranked foods by users u and v
			Recipe nextFood = it.next();
			res += ((float)u.get(nextFood) - neutralRank)*((float)v.get(nextFood) - neutralRank);
		}
		return res;
	}

	protected double pearsonDenominator(HashMap u,HashMap v, Set commonFoods){
		double u_comp = pearsonDenominator_inner(u,commonFoods);
		double v_comp = pearsonDenominator_inner(v,commonFoods);
		double denom = Math.sqrt(u_comp)*Math.sqrt(v_comp);
		denom = (denom == 0) ? 1 : denom;
		return denom;
	}

	protected double pearsonDenominator_inner(HashMap u, Set commonFoods){
		double res = 0;
		Iterator<Recipe> it = commonFoods.iterator();
		while(it.hasNext()){ //Iterate through common ranked foods by users u and v
			Recipe nextFood = it.next();
			res += Math.pow(((float)u.get(nextFood) - neutralRank) , 2);
		}
		return res;
	}



	protected float computeRating(Recipe food){
		float meanRankCurrUser = currentUser.getMeanRank();
		float stdDevCurrUser = currentUser.getStdDeviation();
		float predictedRank;

		float numerator = 0.0f;
		float denominator = 1.0f;
		for(int i = 0; i<dataSize ; i++){
			User otherUser = userData.get(i);
			if(otherUser.hasNotedEdibleItem(food)){
				numerator += (float) ((similarityVec.get(i))*(otherUser.getRankForEdibleItem(food)- otherUser.getMeanRank()))/otherUser.getStdDeviation();
				denominator += (float) Math.abs(similarityVec.get(i));
			}

		}
		predictedRank = currentUser.getMeanRank() + currentUser.getStdDeviation()*(numerator/denominator);
		return predictedRank;
	}
	
	protected void calculateSimilarityMatrix(){
		for (int i = 0; i < dataSize; i++){
			if (userData.get(i).getId() != currentUser.getId()){
				double similarity = computeConstrainedPearsonCorrelation(currentUser, userData.get(i));  //computeCosineSimilarity(food, foodData.get(i));
				similarityVec.add(similarity);
			} else {
				similarityVec.add(0.0);
			}
		}
	}
	
	
	protected void computeRatingPredictions(){
		for (int i = 0; i < recipeData.size(); i++){
			if (!currentUser.hasNotedEdibleItem(recipeData.get(i))){
				addRatingPrediction(recipeData.get(i), computeRating(recipeData.get(i)));
			}
		}
	}
	

	@Override
	public ArrayList<Recipe> recommend(){
		similarityMatrix = new SimilarityMatrix(dataSize, UserUserStrategy.NEIGHBORHOOD_SIZE);
		similarityVec = new ArrayList<>();
		if (recipeData != null && userData != null && currentUser != null){

			calculateSimilarityMatrix();
			computeRatingPredictions();
			sortRatingPredictions();
			extractRecommendations();
			}

		return recommendations;
		
	}

}