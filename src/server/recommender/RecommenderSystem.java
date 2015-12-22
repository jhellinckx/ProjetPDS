package recommender;
import java.util.ArrayList;

public class RecommenderSystem {

	private static final FeatureAugmentationStrategy default_hybride_strat = new FeatureAugmentationStrategy();
	
	private ArrayList<RecommendationStrategy> recomstrategies;
	private HybridationStrategy hybridstrategy;
	private int recommendationsRequired = 0;
	
	public RecommenderSystem(RecommendationStrategy rstrat, HybridationStrategy hstrat){
		recomstrategies = new ArrayList<RecommendationStrategy>();
		recomstrategies.add(rstrat);
		hybridstrategy = hstrat;
		
	}
	
	public RecommenderSystem(RecommendationStrategy rstrat){
		recomstrategies = new ArrayList<RecommendationStrategy>();
		recomstrategies.add(rstrat);
		hybridstrategy = null;
	}
	
	public RecommenderSystem(){
		recomstrategies = new ArrayList<RecommendationStrategy>();
	}

	public void setNumberRecommendations(int nb){
		recommendationsRequired = nb;
	}
	
	public void addRecommendationStrategy(RecommendationStrategy rstrat){
		rstrat.setRecommendationsNumber(recommendationsRequired);
		recomstrategies.add(rstrat);
		if (hybridstrategy == null && recomstrategies.size() > 1){

			hybridstrategy = RecommenderSystem.default_hybride_strat;
		}
	}
	
	public void setHybridationStrategy(HybridationStrategy hstrat){
		hybridstrategy = hstrat;
	}
	
	public void recommendAnItem(){
		
		if (recomstrategies.size() == 1){			// Hybridation useless for 1 recommendation system.
			recomstrategies.get(0).recommend();
		}
		else{
			hybridstrategy.recommend(recomstrategies);
		}
		
		
	}

}
