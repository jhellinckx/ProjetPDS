package recommender;

import java.util.ArrayList;
import org.calorycounter.shared.models.Food;
import org.calorycounter.shared.models.User;

public class CascadeStrategy implements HybridationStrategy {

	private ArrayList<RecommendationStrategy> strategies;
    private int recomRequired;
    private int scalingFactor;          // Scaling Factor used to define the hierarchy of the recommendation strategies.
    private ArrayList<Food> foodData = null;
    private ArrayList<User> userData = null;
    private User currentUser = null;


    public CascadeStrategy(){

        strategies = new ArrayList<RecommendationStrategy>();

    }

    @Override
	public ArrayList<Food> recommend(){

        scalingFactor = strategies.size();
        for (RecommendationStrategy strat: strategies){
            strat.setRecommendationNumber(recomRequired*scalingFactor);             // Here the scaling factor raises the number of foods returned by the recommendation strat.
            strat.updateData(foodData, userData, currentUser, recomRequired);
            foodData = strat.recommend();
            --scalingFactor;                                                        // The scaling factor is decremented in order to reduce the impact of minor recom. strat.
        }

        return foodData;
	}

    @Override
    public void setRecommendationNumber(int recom){
        recomRequired = recom;
    }

    @Override
    public void updateData(ArrayList<Food> foods, ArrayList<User> users, User curUser, int recom){
        foodData = foods;
        userData = users;
        currentUser = curUser;
        recomRequired = recom;

    }

    @Override
    public void addRecommendationStrategy(RecommendationStrategy strat){
        strategies.add(strat);
    }



}
