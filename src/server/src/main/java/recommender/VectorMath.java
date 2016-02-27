package recommender;

/*

	Mathematical vector operations and functions.

	Generic Methods that can be used with Float, Long, Integer, Double.

	When calling a method, the user has to precise the type of the items of the Collections (using Class.class or Class.getClass()).
*/

import java.util.List;
import java.lang.Math;
import java.lang.IllegalArgumentException;
import java.lang.Number;


public final class VectorMath {

	public static <T extends Number> boolean testClass(Class<T> clas){
		if (clas.equals(Integer.class) || clas.equals(Long.class) || clas.equals(Float.class) || clas.equals(Double.class) || clas.equals(Short.class)){
			return true;
		}
		return false;

	}

	public static <T extends Number> double euclideanNorm(Class<T> clas, List<T> vector){		// Euclidean norm. Also known as 2-norm.
		if (VectorMath.testClass(clas)){
			double norm = 0;
			double item1;
			int size = vector.size();

			for (int i = 0; i < size; i++){
				item1 = Double.class.cast(vector.get(i));
				norm += (item1*item1);
			}
			return Math.sqrt(norm);
		}
		else{
			throw new IllegalArgumentException("The type of the argument must be Short, Long, Integer, Float or Double");
		}
	}

	public static <T extends Number> double dotProduct(Class<T> clas, List<T> u, List<T> v){
		if (VectorMath.testClass(clas)){
			double product = 0;
			double item1;
			double item2;
			int sizeU = u.size();
			int sizeV = v.size();
			if (sizeU == sizeV){
				for (int i = 0; i < sizeU; i++){
					item1 = Double.class.cast(u.get(i));
					item2 = Double.class.cast(v.get(i));
					product += (item1*item2);
				}

			}
			else{
				throw new IllegalArgumentException("The vectors must have the same size.");
			}
			return product;
		}
		else{
			throw new IllegalArgumentException("The type of the argument must be Short, Long, Integer, Float or Double");
		}
	}

}