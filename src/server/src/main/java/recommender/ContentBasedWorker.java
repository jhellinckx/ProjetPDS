package recommender;

import manager.RecommendationRequestManager;
import java.util.List;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Map;
import java.util.LinkedHashMap;
import dao.CBUserPredictionsDAO;

public class ContentBasedWorker implements Runnable{
	private static final int K_NUMBER_OF_NEIGHBOURS = 3; //Value retrieved from article, seems to be the best

	private Boolean _run;
	private RecommendationRequestManager _manager;
	private CBUserPredictionsDAO _predictionsDatabase;

	public ContentBasedWorker(RecommendationRequestManager m, CBUserPredictionsDAO predDB){
		_manager = m;
		_predictionsDatabase = predDB;
	}

	@Override
	public void run(){
		setRunning();
		while(isRunning()){
			Map.Entry<Long, List<Long>> userRatings = _manager.requestNewRatings();
			_predictionsDatabase.removePredictions(userRatings.getValue());
			List<RecipePrediction> predictions = _predictionsDatabase.getAllPredictionsForUser(userRatings.getKey());
			for(RecipePrediction pred : predictions){
				computePrediction(pred);
			}
			_predictionsDatabase.updatePredictions(predictions);
		}
	}

	/* Entry = Rating, Similarity */
	private void computePrediction(RecipePrediction pred){
		List<Map.Entry<Float, Float>> neighbours = _predictionsDatabase.getNeighboursInUserProfileLimitK(pred.getRecipeID(), pred.getUserID(), K_NUMBER_OF_NEIGHBOURS);
		float similaritiesSum = 0;
		float similaritiesSumWeighted = 0;
		for(Map.Entry<Float, Float> neighbour : neighbours){
			similaritiesSum += neighbour.getValue();
			similaritiesSumWeighted += neighbour.getValue() * neighbour.getKey(); // similarity * rating
		}
		if(similaritiesSum == 0 || similaritiesSumWeighted == 0){
			pred.setPrediction(0);
		}
		else{
			pred.setPrediction(similaritiesSumWeighted / similaritiesSum);
		}
	}

	public void setRunning(){
		synchronized(_run){
			_run = true;
		}
	}

	public boolean isRunning(){
		synchronized(_run){
			return _run;
		}
	}
}