package recommender;

import java.util.*;
import items.Food;
import dao.CategoryRatingDAOImpl;

public class NearestNeighborStrategy extends ContentBasedStrategy {
	private ArrayList<Food> _foodToFilter;
	private User _user;
	private int _recommendations;
	private CategoryRatingDAOImpl _daoCategoryRating;

	NearestNeighborStrategy(CategoryRatingDAOImpl daoCategoryRating){
		_daoCategoryRating = daoCategoryRating;
	}

	@Override
	public void updateData(ArrayList<Food> toFilter, User user, int recoms){  
		_foodToFilter = toFilter;
		_user = user;
		_recommendations = recoms;
	}

	@Override
	public ArrayList<Food> recommend(){
		Map<Food, Float> ratingPredictions = new HashMap<>();
		for(Food food : _foodToFilter){
			ratingPredictions.put(food, prediction(food));
		}
		Map<Food, Float> sortedRatingPredictions = sortByValue(ratingPredictions);
		List<Food> recommendations = new ArrayList<Food>(sortedRatingPredictions.keySet())
		return recommendations.subList(0, _recommendations);
	}

	private Float prediction(Food food){

	}

	public <K, V extends Comparable<? super V>> Map<K, V> sortByValue(Map<K, V> map) {
		List<Map.Entry<K, V>> entryList = new LinkedList<>(map.entrySet());
		Collections.sort(entryList, new Comparator<Map.Entry<K, V>>(){
        	@Override
        	public int compare(Map.Entry<K, V> first, Map.Entry<K, V> second){
            	return (first.getValue()).compareTo(second.getValue());
        	}
    	});
		Map<K, V> sortedMap = new LinkedHashMap<>();
		for (Map.Entry<K, V> entry : entryList){
        	sortedMap.put(entry.getKey(), entry.getValue());
        }
		return sortedMap;
	}

}
