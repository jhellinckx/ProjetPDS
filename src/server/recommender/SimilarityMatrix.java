package recommender;


import java.util.ArrayList;
import java.lang.Math;

/*
**	Similarity Matrix. This class is used to save the similarity between two items using their indexes as keys and their simimilarity
**	as value. For instance, if one wants to save the similarity between two Foods,
**  the command will be put(position Food 1, position Food 2, similarity).
**  Actually, the position is not mandatory, any id of type int can be used but remember that all the ids must be different.
*/  

public class SimilarityMatrix{

	private final int n;
	private SparseVector[] rows;

	public SimilarityMatrix(int nbRows){
		n = nbRows;
		rows = new SparseVector[n];
		for (int i = 0; i < n; i++){
			rows[i] = new SparseVector(n);
		}
	}

	public void put(int i, int j, double value){
		rows[i].add(j, value);
	}

	public double get(int i, int j){
		return rows[i].get(j);
	}
}