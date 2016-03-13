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
	}

	@Override
	public void run(){
		setRunning();
		while(isRunning()){
			Map.Entry<Long, List<Long>> userRatings = _manager.requestNewRatings();
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