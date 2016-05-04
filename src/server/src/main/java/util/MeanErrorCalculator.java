package util;

import java.lang.Math;
import java.util.List;


public class MeanErrorCalculator {

	public static double getMeanAbsoluteError(List<Float> correct_values, List<Float> predicted_values){
		int size = predicted_values.size();
		float correct;
		float predicted;
		double mean = 0;

		for (int i = 0; i < size; i++){
			correct = correct_values.get(i);
			predicted = predicted_values.get(i);
			mean += Math.abs(predicted-correct);
		}
		mean = mean/size;
		return mean;
	}

	public static double getGlobalMeanAbsoluteErrorList(List<List<Float>> correct_values, List<List<Float>> predicted_values){
		int size = predicted_values.size();
		double global_mean = 0;
		
		for (int i = 0; i < size; i++){
			global_mean += getMeanAbsoluteError(correct_values.get(i), predicted_values.get(i));
		}
		global_mean = global_mean/size;
		return global_mean;
	}
}