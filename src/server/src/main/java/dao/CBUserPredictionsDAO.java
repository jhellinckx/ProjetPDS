package dao;
import java.util.List;
import recommender.RecipePrediction;

public interface CBUserPredictionsDAO{
	public void removePredictions(List<Long> predIDs);
	public List<RecipePrediction> getAllPredictionsForUser(Long userID);
	public void updatePredictions(List<RecipePrediction> preds);
}