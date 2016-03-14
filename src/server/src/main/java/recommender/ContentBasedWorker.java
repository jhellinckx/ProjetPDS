package recommender;

import manager.RecommendationRequestManager;
import java.util.List;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Map;
import java.util.LinkedHashMap;
import dao.CBUserPredictionsDAO;

public class ContentBasedWorker implements Runnable{
	private Boolean _run;
	private RecommendationRequestManager _manager;
	private CBUserPredictionsDAO _predictionsDatabase;
	private int _nbrNeighbours;

	public ContentBasedWorker(RecommendationRequestManager m, CBUserPredictionsDAO predDB, int k){
		_manager = m;
		_predictionsDatabase = predDB;
		_nbrNeighbours = k;
		_run = false;
	}

	@Override
	public void run(){
		setRunning();
		try{
			while(isRunning()){
				Map.Entry<Long, List<Long>> userRatings = _manager.requestNewRatings();
				_predictionsDatabase.removePredictions(userRatings.getValue());
				for(Long l : userRatings.getValue()){
					System.out.println("REMOVED RECIPE -> " + Long.toString(l));
				}
				List<RecipePrediction> predictions = _predictionsDatabase.getAllPredictionsForUser(userRatings.getKey());
				System.out.println("GOT ALL PREDICTIONS");
				for(RecipePrediction pred : predictions){
					computePrediction(pred);
				}
				System.out.println("UPDATING");
				_predictionsDatabase.updatePredictions(predictions);
				System.out.println("UPDATED");
			}
		}
		catch(InterruptedException e){}
		System.out.println("Worker stopped");
	}

	/* Entry = Rating, Similarity */
	private void computePrediction(RecipePrediction pred){
		List<Map.Entry<Float, Float>> neighbours = _predictionsDatabase.getNeighboursInUserProfileLimitK(pred.getRecipeID(), pred.getUserID(), _nbrNeighbours);
		System.out.println("START COMPUTE");
		float similaritiesSum = 0;
		float similaritiesSumWeighted = 0;
		System.out.println();
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
		System.out.println("END COMPUTE");
	}

	public void stop(){
		synchronized(_run){
			_run = false;
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