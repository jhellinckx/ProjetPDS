package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Arrays;
import static dao.DAOUtilitaire.*;


import org.calorycounter.shared.models.Food;
import org.calorycounter.shared.models.User;
import items.CategoryRating;

public class CategoryRatingDAOImpl implements CategoryRatingDAO{
	private static final String SQL_FIND_RATED_CATEGORY = "SELECT rating, n_ratings FROM CategoriesRatings WHERE user_id = ? AND category_name = ?";
	private static final String SQL_UPDATE_CATEGORY_RATING_AND_N_RATINGS_BY_NAME = "UPDATE CategoriesRatings SET rating = ?, n_ratings = ? WHERE category_name = ? AND user_id = ?";
	private static final String SQL_INSERT_NEW_CATEGORY_RATING = "INSERT INTO CategoriesRatings (category_name, rating, n_ratings, user_id) VALUES (?,?,?,?)";
	private static final String SQL_FIND_ALL_CATEGORIES_RATINGS_BY_USER = "SELECT category_name, rating, n_ratings FROM CategoryRating WHERE user_id = ?";
	
	private static final String SQL_FIND_CATEGORIES_BY_FOOD_ID = "SELECT categories FROM Food WHERE id_food = ?";

	private DAOFactory _daoFactory;

	CategoryRatingDAOImpl( DAOFactory daoFactory) {
		this._daoFactory = daoFactory;
	}

	public CategoryRating findRatedCategory(User user, String category) throws DAOException{
		Connection connection = null;
		PreparedStatement preparedStatement = null;
		ResultSet resultSet = null;
		CategoryRating result = null;
		try{
			connection = _daoFactory.getConnection();
			preparedStatement = initializationPreparedRequest(connection, SQL_FIND_RATED_CATEGORY, false, user.getId(), category);
			resultSet = preparedStatement.executeQuery();
			while(resultSet.next()){
				result = new CategoryRating(category, (float)resultSet.getFloat("rating"), (int)resultSet.getInt("n_ratings"), (long)user.getId());
			}
		}catch (SQLException e) {
			throw new DAOException (e);
		} finally {
			silentClosures(resultSet, preparedStatement, connection);
		}
		return result;
	}

	public void addRatingForCategory(User user, String category, float rating, int n) throws DAOException{
		CategoryRating dbCategory = findRatedCategory(user, category);
		if(dbCategory == null){
			Connection connection = null;
			PreparedStatement preparedStatement = null;
			try{
				connection = _daoFactory.getConnection();
				preparedStatement = initializationPreparedRequest(connection, SQL_INSERT_NEW_CATEGORY_RATING, false, category, rating, n, user.getId());
				int status = preparedStatement.executeUpdate();
				if (status == 0) {
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
				connection = _daoFactory.getConnection();
				preparedStatement = initializationPreparedRequest(connection, SQL_UPDATE_CATEGORY_RATING_AND_N_RATINGS_BY_NAME, false, rating, n, category, user.getId());
				int status = preparedStatement.executeUpdate();
				if (status == 0) {
					throw new DAOException ("Could not update category rating line in database");
				}
			} catch (SQLException e) {
				throw new DAOException(e);
			} finally {
				silentClosures(preparedStatement, connection);
			}
		}

	}


	public ArrayList<CategoryRating> findAllRatedCategoriesForUser(User user) throws DAOException {
		Connection connection = null;
		PreparedStatement preparedStatement = null;
		ResultSet resultSet = null;
		ArrayList<CategoryRating> ratedCategories = new ArrayList<CategoryRating>();
		try{
			connection = _daoFactory.getConnection();
			preparedStatement = initializationPreparedRequest(connection, SQL_FIND_ALL_CATEGORIES_RATINGS_BY_USER, false, user.getId());
			resultSet = preparedStatement.executeQuery();
			while(resultSet.next()){
				ratedCategories.add(new CategoryRating((String)resultSet.getString("category_name"), (float)resultSet.getFloat("rating"), (int)resultSet.getInt("n_ratings"), user.getId()));
			}
		}catch (SQLException e) {
			throw new DAOException (e);
		} finally {
			silentClosures(resultSet, preparedStatement, connection);
		}
		return ratedCategories;
	}

	public ArrayList<String> findCategoriesForFood(Food food) throws DAOException{
		Connection connection = null;
		PreparedStatement preparedStatement = null;
		ResultSet resultSet = null;
		List<String> categories = new ArrayList<>();
		String result = "";
		try{
			connection = _daoFactory.getConnection();
			preparedStatement = initializationPreparedRequest(connection, SQL_FIND_CATEGORIES_BY_FOOD_ID, false, food.getId());
			resultSet = preparedStatement.executeQuery();
			while(resultSet.next()){
				result = resultSet.getString("categories");
			}
		}catch (SQLException e) {
			throw new DAOException (e);
		} finally {
			silentClosures(resultSet, preparedStatement, connection);
		}
		return new ArrayList<String>(Arrays.asList(result.split(",")));
	}
}