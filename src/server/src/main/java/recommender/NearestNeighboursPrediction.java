package recommender;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.AbstractMap;

public class NearestNeighboursPrediction{
	private long _prediction_id;
	private long _recipe_id;
	private long _user_id;
	private float _prediction;
	private List<Map.Entry<Float, Float>> _neighbours;

	private static final float MIN_RATING = 3;
	private static final int K_MAX_NEIGHBOURS = 3;

	public NearestNeighboursPrediction(long predid, long recid, long usrid){
		_prediction_id = predid;
		_recipe_id = recid;
		_user_id = usrid;
		_prediction = 0;
		_neighbours = new ArrayList<Map.Entry<Float, Float>>(K_MAX_NEIGHBOURS);
	}

	public void compute(){
		float similaritiesSum = 0;
		float similaritiesSumWeighted = 0;
		for(Map.Entry<Float, Float> neighbour : _neighbours){
			if(neighbour.getKey() >= MIN_RATING){
				//similaritiesSum += neighbour.getValue();
				similaritiesSumWeighted += neighbour.getValue() * neighbour.getKey(); // similarity * rating
			}
		}
		// if(similaritiesSum == 0 || similaritiesSumWeighted == 0){
		// 	setPrediction(0);
		// }
		//else{
		setPrediction(similaritiesSumWeighted);
		//}
	}

	public void addNeighbour(Float rating, Float similarity){
		_neighbours.add(new AbstractMap.SimpleEntry<Float, Float>(rating, similarity));
	}

	public boolean hasMaxNeighbours(){
		return _neighbours.size() == K_MAX_NEIGHBOURS;
	}

	public int maxNeighbours(){
		return K_MAX_NEIGHBOURS;
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