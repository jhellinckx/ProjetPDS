package recommender;

import java.util.*;
import items.Food;
import items.CategoryRating;
import items.User;
import dao.CategoryRatingDAO;

public class NearestNeighborStrategy extends ContentBasedStrategy {
	private ArrayList<Food> _foodToFilter;
	private User _user;
	private int _recommendations;
	private CategoryRatingDAO _daoCategoryRating;
	private ArrayList<User> _otherUsers;

	public NearestNeighborStrategy(CategoryRatingDAO daoCategoryRating){
		_daoCategoryRating = daoCategoryRating;
	}

	@Override
	public void updateData(ArrayList<Food> toFilter, ArrayList<User> users, User user, int recoms){  
		_foodToFilter = toFilter;
		_user = user;
		_recommendations = recoms;
		_otherUsers = users;
	}

	@Override
	public ArrayList<Food> recommend(){
		if(_foodToFilter.isEmpty())
			_foodToFilter = new ArrayList<Food>();	
		Map<Food, Float> ratingPredictions = new HashMap<>();
		for(Food food : _foodToFilter){
			ratingPredictions.put(food, prediction(food));
		}
		Map<Food, Float> sortedRatingPredictions = sortByValue(ratingPredictions);
		List<Food> recommendations = new ArrayList<Food>(sortedRatingPredictions.keySet());
		return new ArrayList<Food>(recommendations.subList(0, _recommendations));
	}

	private Float prediction(Food food){
		ArrayList<String> categories = _daoCategoryRating.findCategoriesForFood(food);
		ArrayList<CategoryRating> ratedCategories = new ArrayList<>();
		for(String category : categories){
			CategoryRating result = _daoCategoryRating.findRatedCategory(_user, category);
			if(result != null) ratedCategories.add(result);
		}
		return meanRating(ratedCategories);
	}

	private Float meanRating(ArrayList<CategoryRating> categoryRatings){
		Float sum = 0.0f;  // mettre a 2.5 a la fin.
		for(CategoryRating categoryRating : categoryRatings){
			sum += categoryRating.rating();
		}
		return sum/categoryRatings.size();
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
