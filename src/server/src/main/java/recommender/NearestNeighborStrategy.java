package recommender;

import java.util.*;

import org.calorycounter.shared.models.Food;
import items.CategoryRating;
import org.calorycounter.shared.models.User;
import dao.CategoryRatingDAO;
import static org.calorycounter.shared.Constants.network.*;


public class NearestNeighborStrategy extends ContentBasedStrategy {
	private class Prediction{
		private Float _mean;
		private int _n_categories;
		public Prediction(Float m, int n){
			_mean = m;
			_n_categories = n;
		}
		public Float mean() { return _mean; }
		public int categories() { return _n_categories; }
	}
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
		_recommendations = 30;
		_otherUsers = users;
	}

	@Override
	public ArrayList<Food> recommend(){
		if(_foodToFilter.isEmpty())
			_foodToFilter = new ArrayList<Food>();	
		Map<Food, Float> ratingPredictions = new HashMap<>();
		Map<Long, Integer> categoriesNumberForID = new HashMap<>();
		for(Food food : _foodToFilter){
			Prediction pred = prediction(food);
			ratingPredictions.put(food, pred.mean());
			categoriesNumberForID.put(food.getId(), pred.categories());
		}
		Map<Food, Float> sortedRatingPredictions = sortByValue(ratingPredictions);
		List<Food> recommendations = new ArrayList<Food>(sortedRatingPredictions.keySet());

		ArrayList<Food> resizedRecoms = new ArrayList<Food>();
 		if(recommendations.size()>_recommendations) {
 			resizedRecoms = new ArrayList<Food>(recommendations.subList(recommendations.size()-_recommendations-1, recommendations.size()));
 		}
		Map<Food, Integer> resizedRecomsWithCategoriesNumber = new HashMap<>();
		for(Food recom : resizedRecoms)
			resizedRecomsWithCategoriesNumber.put(recom, categoriesNumberForID.get(recom.getId()));

		Map<Food, Integer> sortedResizedRecoms = sortByValue(resizedRecomsWithCategoriesNumber);
		ArrayList<Food> sortedResizedRecomsList = new ArrayList<Food>(sortedResizedRecoms.keySet());
		System.out.println(sortedResizedRecomsList.toString());
		System.out.println("TAKE ONLY RECOM : "+Integer.toString(_recommendations));

		ArrayList<Food> sortedInOrder = new ArrayList<Food>(sortedResizedRecomsList);
		int i = sortedResizedRecomsList.size()-1;
		for(Food food : sortedResizedRecomsList){
			sortedInOrder.set(i, food);
			i--;
		}
		return sortedInOrder;
	}

	private Prediction prediction(Food food){
		ArrayList<String> categories = _daoCategoryRating.findCategoriesForFood(food);
		ArrayList<CategoryRating> ratedCategories = new ArrayList<>();
		boolean atLeastOneRated = false;
		for(String category : categories){
			CategoryRating result = _daoCategoryRating.findRatedCategory(_user, category);
			if(result != null) {
				ratedCategories.add(result);
				atLeastOneRated = true;
			}
			else ratedCategories.add(new CategoryRating(category, 2.5f, 1, _user.getId()));
		}
		Float mean = meanRating(ratedCategories, atLeastOneRated);
		return new Prediction(mean, ratedCategories.size());
		}

	private Float meanRating(ArrayList<CategoryRating> categoryRatings, boolean oneRated){
		if(! oneRated)
			return 2.5f;
		Float sum = 0.0f;
		for(CategoryRating categoryRating : categoryRatings){
			sum += categoryRating.rating();
		} 
		if(categoryRatings.isEmpty()) {
			return DEFAULT_RATING;
		}
		else{
			return sum/categoryRatings.size();
		}
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
