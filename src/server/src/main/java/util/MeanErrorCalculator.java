package util;

import java.lang.Math;
import java.util.List;


public class MeanErrorCalculator {

	private static List<List<Float>> correct_values;

	public static void setCorrectValues(List<List<Float>> values){
		correct_values = values;
	}

	public static double getMeanAbsoluteError(int index, List<Float> predicted_values){
		int size = predicted_values.size();
		List<Float> current_list = correct_values.get(index);
		float correct;
		float predicted;
		double mean = 0;

		for (int i = 0; i < size; i++){
			correct = current_list.get(i);
			predicted = predicted_values.get(i);
			mean += Math.abs(predicted-correct);
		}
		mean = mean/size;
		return mean;
	}

	public static double getGlobalMeanAbsoluteError(List<List<Float>> predicted_values){
		int size = predicted_values.size();
		double global_mean = 0;
		
		for (int i = 0; i < size; i++){
			global_mean += getMeanAbsoluteError(i, predicted_values.get(i));
		}
		global_mean = global_mean/size;
		return global_mean;
	}
}