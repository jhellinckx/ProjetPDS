package dao;
import java.util.List;
import java.util.ArrayList;
import recommender.RecipePrediction;

public class CBUserPredictionsDAOImpl implements CBUserPredictionsDAO{
	private DAOFactory _daoFactory;

	CBUserPredictionsDAOImpl(DAOFactory fac){
		_daoFactory = fac;
	}

	public void removePredictions(List<Long> predIDs){

	}

	public List<RecipePrediction> getAllPredictionsForUser(Long userID){
		return new ArrayList<RecipePrediction>();
	}

	public void updatePredictions(List<RecipePrediction> preds){

	}
}