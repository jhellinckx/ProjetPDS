package util;

import java.util.List;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import java.util.Collections;
import org.calorycounter.shared.models.User;
import items.RandomUserGenerator;


public class ProfileGenerator {

	private static int lower_bound = 40;
	private static int upper_bound = 70; 

	private static RCRGenerator generator = new RCRGenerator();
	private static HashMap<Integer, List<Float>> categoryid_ratings_map;
	private static List<Integer> category_ids;
	
	public static void setCategoryIds(List<Integer> ids){
		category_ids = ids;
	}

	public static void generateProfile(){
		List<List<Float>> sequence_ratings = generator.generateRatingsSequences(category_ids.size(), lower_bound, upper_bound);
		categoryid_ratings_map = new HashMap<>();
		for (int i = 0; i < category_ids.size(); i++){
			categoryid_ratings_map.put(category_ids.get(i), sequence_ratings.get(i));
		}
	}

	public static Map<Integer, List<Float>> getProfile(){
		return categoryid_ratings_map;
	}

	public static List<Float> generateNoise(){
		return generator.generateNoiseValues(lower_bound*2, upper_bound*2);
	}
}