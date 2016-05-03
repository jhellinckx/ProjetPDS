package util;

import org.apache.commons.math3.random.RandomDataGenerator;
import java.lang.Math;
import java.util.List;
import java.util.ArrayList;


public class RCRGenerator {

	private static final double std_deviation = 0.6;
	private static final double max_rate = 5.0;
	private static final double small_gap = 0.6;
	private static final double gap = 1.2;
	private static final double large_gap = 1.8;
	private static final double mid_value = 2.5;

	private RandomDataGenerator generator;

	public RCRGenerator(){
		generator = new RandomDataGenerator();
	}

	private float filterValue(double value){
		double floor = Math.floor(value);
		double ceil = Math.ceil(value);

		if (max_rate < value){
			value = max_rate;
		} else if (value == 0) {
			value = value;
		} else if (value < ceil-0.5){
			if (value < floor+0.25){
				value = floor;
			} else if (value >= floor+0.25){
				value = ceil-0.5;
			}
		} else if (value >= ceil-0.5){
			if (value < ceil-0.25){
				value = ceil-0.5;
			} else if (value >= ceil-0.25){
				value = ceil;
			}
		}
		return (new Double(value)).floatValue();
	}

	private float getGaussianRandomValue(double mu, double sigma){
		double value = generator.nextGaussian(mu, sigma);
		return filterValue(value);
	}

	private float getReallyLikedRating(){
		return getGaussianRandomValue(mid_value+large_gap, std_deviation);
	}

	private float getLikedRating(){
		return getGaussianRandomValue(mid_value+gap, std_deviation);
	}

	private float getBitLikedRating(){
		return getGaussianRandomValue(mid_value+small_gap, std_deviation);
	}

	private float getBitDislikedRating(){
		return getGaussianRandomValue(mid_value-small_gap, std_deviation);
	}

	private float getDislikedRating(){
		return getGaussianRandomValue(mid_value-gap, std_deviation);
	}

	private float getReallyDislikedRating(){
		return getGaussianRandomValue(mid_value-large_gap, std_deviation);
	}

	private float getNeutralRating(){
		double value = generator.nextUniform(0.0, max_rate+1, true);
		return filterValue(value);
	}

	private int getIntvalue(int lower, int upper){
		float value = (new Double(generator.nextUniform(0.0, upper-lower+1, true))).floatValue();
		return Math.round(value+lower);
	}

	public List<Float> generateNoiseValues(int lower, int upper){
		int noise_size = getIntvalue(lower, upper);
		List<Float> noise_values = new ArrayList<>();

		for (int i = 0; i < noise_size; i++){
			noise_values.add(getNeutralRating());
		}
		return noise_values;
	}

	public List<Float> generateReallyLikedRatings(int lower, int upper){
		int number = getIntvalue(lower, upper);
		List<Float> ratings = new ArrayList<>();

		for (int i = 0; i < number; i++){
			ratings.add(getReallyLikedRating());
		}

		return ratings;
	}

	public List<Float> generateLikedRatings(int lower, int upper){
		int number = getIntvalue(lower, upper);
		List<Float> ratings = new ArrayList<>();

		for (int i = 0; i < number; i++){
			ratings.add(getLikedRating());
		}
		return ratings;
	}

	public List<Float> generateBitLikedRatings(int lower, int upper){
		int number = getIntvalue(lower, upper);
		List<Float> ratings = new ArrayList<>();

		for (int i = 0; i < number; i++){
			ratings.add(getBitLikedRating());
		}
		return ratings;
	}

	public List<Float> generateBitDislikedRatings(int lower, int upper){
		int number = getIntvalue(lower, upper);
		List<Float> ratings = new ArrayList<>();

		for (int i = 0; i < number; i++){
			ratings.add(getBitDislikedRating());
		}
		return ratings;
	}

	public List<Float> generateDislikedRatings(int lower, int upper){
		int number = getIntvalue(lower, upper);
		List<Float> ratings = new ArrayList<>();

		for (int i = 0; i < number; i++){
			ratings.add(getDislikedRating());
		}
		return ratings;
	}

	public List<Float> generateReallyDislikedRatings(int lower, int upper){
		int number = getIntvalue(lower, upper);
		List<Float> ratings = new ArrayList<>();

		for (int i = 0; i < number; i++){
			ratings.add(getReallyDislikedRating());
		}
		return ratings;
	}

	public List<List<Float>> generateRatingsSequences(int nbr, int lower, int upper){			// Generate a random sequence of random list of  random ratings.

		List<List<Float>> ratings_sequences = new ArrayList<>();
		int func;

		for (int i = 0; i < nbr; i++){
			func = getIntvalue(0, 5);
			switch (func){
				case 0:
					ratings_sequences.add(generateReallyDislikedRatings(lower, upper));
					break;
				case 1:
					ratings_sequences.add(generateLikedRatings(lower, upper));
					break;
				case 2:
					ratings_sequences.add(generateBitDislikedRatings(lower, upper));
					break;
				case 3:
					ratings_sequences.add(generateReallyLikedRatings(lower, upper));
					break;
				case 4:
					ratings_sequences.add(generateDislikedRatings(lower, upper));
					break;
				default:
					ratings_sequences.add(generateBitLikedRatings(lower, upper));
					break;
			}
		}
		return ratings_sequences;
	}
}