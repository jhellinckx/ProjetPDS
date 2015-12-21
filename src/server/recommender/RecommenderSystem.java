package recommender;
import java.util.ArrayList;

public class RecommenderSystem {

	private static FeatureAugmentationStrategy default_hybride_strat = new FeatureAugmentationStrategy();
	private static ItemItemStrategy default_recom_strat = new ItemItemStrategy();
	
	private ArrayList<RecommendationStrategy> recomstrategies;
	private HybridationStrategy hybridstrategy;
	
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
	
	public void addRecommendationStrategy(RecommendationStrategy rstrat){
		recomstrategies.add(rstrat);
		if (hybridstrategy == null && recomstrategies.size() > 1){

			hybridstrategy = RecommenderSystem.default_hybride_strat;
		}
	}
	
	public void setHybridationStrategy(HybridationStrategy hstrat){
		hybridstrategy = hstrat;
	}
	
	public void recommendAnItem(){
		if (recomstrategies.isEmpty()){
			recomstrategies.add(RecommenderSystem.default_recom_strat);
		}

		if (recomstrategies.size() == 1){			// Hybridation useless for 1 recommendation system.
			recomstrategies.get(0).recommend();
		}
		else{
			hybridstrategy.recommend(recomstrategies);
		}
		
		
	}

}
