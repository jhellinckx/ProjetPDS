package recommender;

public class RecipePrediction{
	private long _prediction_id;
	private long _recipe_id;
	private long _user_id;
	private float _prediction;

	public RecipePrediction(long predid, long recid, long usrid, float pred){
		_prediction_id = predid;
		_recipe_id = recid;
		_user_id = usrid;
		_prediction = pred;
	}
	
	public long getPredictionID(){
		return _prediction_id;
	}

	public long getUserID(){
		return _user_id;
	}

	public long getRecipeID(){
		return _recipe_id;
	}

	public float getPrediction(){
		return _prediction;
	}

	public void setPredictionID(long id){
		_prediction_id = id;
	}

	public void setUserID(long id){
		_user_id = id;
	}

	public void setRecipeID(long id){
		_recipe_id = id;
	}

	public void setPrediction(float pred){
		_prediction = pred;
	}
}