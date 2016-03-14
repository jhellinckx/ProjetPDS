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

	public ContentBasedWorker(RecommendationRequestManager m, CBUserPredictionsDAO predDB){
		_manager = m;
		_predictionsDatabase = predDB;
		_run = false;
	}

	@Override
	public void run(){
		setRunning();
		try{
			while(isRunning()){
				Map.Entry<Long, List<Long>> userRatings = _manager.requestNewRatings();
				_predictionsDatabase.removePredictions(userRatings.getValue());
				List<NearestNeighboursPrediction> predictions = _predictionsDatabase.getAllNearestNeighboursPredictionsForUser(userRatings.getKey());
				System.out.println("GOT ALL PREDICTIONS ! STARTING COMPUTATIONS...");
				for(NearestNeighboursPrediction pred : predictions){
					pred.compute();
				}
				System.out.println("COMPUTATIONS DONE ! STARTING UPDATES... ");
				_predictionsDatabase.updatePredictions(predictions);
				System.out.println("UPDATES DONE !");
			}
		}
		catch(InterruptedException e){}
		System.out.println("Worker stopped");
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