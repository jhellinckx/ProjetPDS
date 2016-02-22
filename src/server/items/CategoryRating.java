package items;

public class CategoryRating {
	private String _categoryName;
	private float _rating;
	private int _nbRatings;
	private long _userID;

	public CategoryRating(String name, float rating, int n, long user){
		_categoryName = name;
		_rating = rating;
		_nbRatings = n;
		_userID = user;
	}

	public String name() { return _categoryName; }
	public float rating() { return _rating; }
	public int timesRated() { return _nbRatings; }
	public long userID() { return _userID; }

	public void setName(String name) { _categoryName = name; }
	public void setRating(float rating) { _rating = rating; }
	public void setTimesRated(int n) { _nbRatings = n; }
	public void setUserID(long user) { _userID = user; }
}