package dao;
import java.util.List;
import recommender.NearestNeighboursPrediction;

public interface CBUserPredictionsDAO{
	public void removePredictions(List<Long> recipes);
	public void updatePredictions(List<NearestNeighboursPrediction> preds);
	public List<NearestNeighboursPrediction> getAllNearestNeighboursPredictionsForUser(Long user_id);
}