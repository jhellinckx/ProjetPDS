package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.HashMap;
import static dao.DAOUtilitaire.*;


import items.Food;
import items.User;

public class CategoryRatingDAOImpl {
	public class CategoryRating {
		private String _categoryName;
		private float _rating;
		private int _nbRatings;
		private int _userID;

		public CategoryRating(String name, float rating, int n, int user){
			_categoryName = name;
			_rating = rating;
			_nbRatings = n;
			_userID = user;
		}

		public String name() { return _categoryName; }
		public float rating() { return _rating; }
		public int timesRated() { return _nbRatings; }
		public int userID() { return _userID; }

		public String setName(String name) { _categoryName = name; }
		public float setRating(float rating) { _rating = rating; }
		public int setTimesRated(int n) { _nbRatings = n; }
		public int setUserID(int user) { _userID = user; }
	}

	private static final String SQL_FIND_RATED_CATEGORY = "SELECT rating, n_ratings FROM CategoriesRatings WHERE user_id = ? AND category_name = ?";
	private static final String SQL_UPDATE_CATEGORY_RATING_AND_N_RATINGS_BY_NAME = "UPDATE CategoriesRatings SET rating = ?, n_ratings = ? WHERE category_name = ? AND user_id = ?";
	private static final String SQL_INSERT_NEW_CATEGORY_RATING = "INSERT INTO CategoriesRatings (category_name, rating, n_ratings, user_id) VALUES (?,?,?,?)";
	private static final String SQL_FIND_ALL_CATEGORIES_RATINGS_BY_USER = "SELECT category_name, rating, n_ratings FROM CategoryRating WHERE user_id = ?";
	private DAOFactory _daoFactory;

	CategoryRatingDAOImpl( DAOFactory daoFactory) {
		this.daoFactory = daoFactory;
	}

	public CategoryRating findRatedCategory(User user, String category) throws DAOException{
		Connection connection = null;
		PreparedStatement preparedStatement = null;
		ResultSet resultSet = null;
		CategoryRating result = null;
		try{
			connection = daoFactory.getConnection();
			preparedStatement = initializationPreparedRequest(connection, SQL_FIND_RATED_CATEGORY, false, user.getId(), category);
			resultSet = preparedStatement.executeQuery();
			while(resultSet.next()){
				CategoryRating = new CategoryRating(category, (float)resultSet.getFloat("rating"), (int)resultSet.getInt("n_ratings"), user.getId());
			}
		}catch (SQLException e) {
			throw new DAOException (e);
		} finally {
			silentClosures(resultSet, preparedStatement, connection);
		}
		return result;
	}

	public void addRatingForCategory(User user, String category, double rating, int n) throws DAOException{
		CategoryRating dbCategory = findRatedCategory(user, category);
		if(dbCategory == null){
			Connection connection = null;
			PreparedStatement preparedStatement = null;
			try{
				connection = daoFactory.getConnection();
				preparedStatement = initializationPreparedRequest(connection, SQL_INSERT_NEW_CATEGORY_RATING, false, category, rating, n, user.getId());
				int status = preparedStatement.executeUpdate();
				if (statut == 0) {
					throw new DAOException ("Could not create new category rating line in database");
				}
			} catch (SQLException e) {
				throw new DAOException(e);
			} finally {
				silentClosures(preparedStatement, connection);
			}
		}
		else{
			Connection connection = null;
			PreparedStatement preparedStatement = null;
			try{
				connection = daoFactory.getConnection();
				preparedStatement = initializationPreparedRequest(connection, SQL_UPDATE_CATEGORY_RATING_AND_N_RATINGS_BY_NAME, false, rating, n, category, user.getId());
				int status = preparedStatement.executeUpdate();
				if (statut == 0) {
					throw new DAOException ("Could not update category rating line in database");
				}
			} catch (SQLException e) {
				throw new DAOException(e);
			} finally {
				silentClosures(preparedStatement, connection);
			}
		}

	}

	public ArrayList<CategoryRating> findAllRatedCategoriesForUser(User user){
		Connection connection = null;
		PreparedStatement preparedStatement = null;
		ResultSet resultSet = null;
		ArrayList<CategoryRating> ratedCategories = new ArrayList<CategoryRating>();
		try{
			connection = daoFactory.getConnection();
			preparedStatement = initializationPreparedRequest(connection, SQL_FIND_ALL_CATEGORIES_RATINGS_BY_USER, false, user.getId());
			resultSet = preparedStatement.executeQuery();
			while(resultSet.next()){
				ratedCategories.add(new CategoryRating(category, (float)resultSet.getFloat("rating"), (int)resultSet.getInt("n_ratings"), user.getId()));
			}
		}catch (SQLException e) {
			throw new DAOException (e);
		} finally {
			silentClosures(resultSet, preparedStatement, connection);
		}
		return ratedCategories;
	}
}