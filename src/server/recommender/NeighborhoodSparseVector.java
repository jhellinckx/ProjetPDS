package recommender;

import java.util.TreeMap;
import java.util.Map;

/*
**	This class is an extension of the classic SparseVector.
**	It uses a Treeset in order the keep the highest values of the SparseVector in memory.
**	The size of the TreeSet is defined by the threshold.
**	
**  The TreeMap guarantees that the Nearest Neighbor can be gotten in O(log(n)) (Obviously, it can be gotten in constant time 
**	but keeping the Tree structure raises the cost to log(n)).
**  But, in order to minimise the place in memory and due to the threshold, adding a value to the vector
**  is no longer in constant time. As so, this is useful if the neighborhood size is relatively small (for the Item-Item e.g.).
**
*/


class NeighborhoodSparseVector extends SparseVector{

	private final int threshold;
	private TreeMap<Double, Integer> neighborhood;

	public NeighborhoodSparseVector(int trh){
		threshold = trh;
		neighborhood = new TreeMap<Double, Integer>();

	}

	public NeighborhoodSparseVector(int n, int trh){
		super(n);
		threshold = trh;
		neighborhood = new TreeMap<Double, Integer>();
	}

	public int getThreshold(){
		return threshold;
	}

	@Override
	public void add(int key, double value){
		super.add(key, value);
		if (value != 0.0){
			if (neighborhood.size() < threshold){
				neighborhood.put(value, key);
			}
			else {
				neighborhood.put(value, key);
				neighborhood.pollFirstEntry();		// Remove the lowest element of the set.
			}
		}
	}

	@Override
	public Map getNearestNeighborhood(){
		return neighborhood;
	}
}