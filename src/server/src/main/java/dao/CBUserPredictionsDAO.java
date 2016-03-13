package dao;
import java.util.List;
import recommender.RecipePrediction;
import java.util.Map;

public interface CBUserPredictionsDAO{
	public void removePredictions(List<Long> recipes);
	public List<RecipePrediction> getAllPredictionsForUser(Long userID);
	public void updatePredictions(List<RecipePrediction> preds);
	public List<Map.Entry<Float, Float>> getNeighboursInUserProfileLimitK(Long recipe_id, Long user_id, int k);
}