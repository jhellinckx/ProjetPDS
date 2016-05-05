package recommender;
import java.util.List;
import java.util.Map;

import org.calorycounter.shared.models.Recipe;
import org.calorycounter.shared.models.User;

public class RecommenderSystem {

	private static final CascadeStrategy default_hybride_strat = new CascadeStrategy();
	
	private RecommendationStrategy recomstrategy;
	private HybridationStrategy hybridstrategy;
	private int recommendationsRequired = 0;
	
	public RecommenderSystem(RecommendationStrategy rstrat, HybridationStrategy hstrat){
		recomstrategy = rstrat;
		hybridstrategy = hstrat;
		hybridstrategy.addRecommendationStrategy(rstrat);
		
	}
	
	public RecommenderSystem(RecommendationStrategy rstrat){
		recomstrategy = rstrat;
		hybridstrategy = null;
	} 

	public void setNumberRecommendations(int nb){
		recommendationsRequired = nb;
	}
	
	public void addRecommendationStrategyToHybridStrategy(RecommendationStrategy rstrat){

		if (hybridstrategy == null){

			hybridstrategy = RecommenderSystem.default_hybride_strat;
		}
		hybridstrategy.addRecommendationStrategy(rstrat);
	}
	
	public void setHybridationStrategy(HybridationStrategy hstrat){
		hybridstrategy = hstrat;
	}
	
	public List<Recipe> recommendItems(){
		
		if (hybridstrategy == null){			// Hybridation useless for 1 recommendation system.
			return recomstrategy.recommend();
		}
		else{
			return hybridstrategy.recommend();
		}
		
		
	}

	public void updateData(List<Recipe> foods, List<User> users, User currentUser, int nbRecom){

		if (hybridstrategy == null){
			recomstrategy.updateData(foods, users, currentUser, nbRecom);
		}
		else{
			hybridstrategy.updateData(foods, users, currentUser, nbRecom);
		}
	}

	public Map<Long, RecipeRatingPair> getRatingsPrediction(){
		if (hybridstrategy == null) {
			return recomstrategy.getRatingsPrediction();
		} else{
			return hybridstrategy.getRatingsPrediction();
		}
	}



}
