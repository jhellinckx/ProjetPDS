package recommender;

import java.util.HashMap;


/*
**	This class represents a SparseVector used by the SimilarityMatrix. It contains a HashMap.
**  Typically, the keys of the HashMap will be the ids of some items and the values will be a similarity score.
**  It is a SparseVector because when the similarity between two items is equal to 0, the key corresponding to one
**  of the items is deleted (the one that is stored in the HashMap). So, only values greater than 0 are stored.
**
**
**  A method returning the K max values of the vector will be added later.
*/

public class SparseVector {

	private final int n;
	private HashMap<Integer, Double> vector;

	public SparseVector(){
		n = 0;
		vector = new HashMap<Integer, Double>(); 
	}

	public SparseVector(int i){
		n = i;
		vector = new HashMap<Integer, Double>();
	}

	public double get(int key){
		if (vector.containsKey(key)){
			return vector.get(key);
		}
		return 0.0;
	}

	public int getSize(){
		return n;
	}

	public void add(int key, double value){
		if (value == 0.0){
			vector.remove(key);
		}
		else{
			vector.put(key, value);
		}
	}
}