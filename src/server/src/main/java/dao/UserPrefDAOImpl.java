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

public class UserPrefDAOImpl implements UserPrefDAO {
	private DAOFactory daoFactory;
	private static final String SQL_INSERT = "INSERT INTO User_preferences (numUser,numFood,rank) VALUES (?,?,?)";
	private static final String SQL_FIND_USER_DE_APPRECIATED_FOOD = "SELECT numFood FROM User_preferences WHERE numUser = ? and rank = ?";
	private static final String SQL_FIND_USERS_APPRECIATING_FOOD = "SELECT numUser FROM User_preferences WHERE numFood = ?";
	private static final String SQL_DELETE = "DELETE FROM User_preferences WHERE numUser = ? AND numFood = ? AND rank = ?";
	private static final String SQL_UPDATE = "UPDATE User_preferences SET numFood = ? WHERE numUser = ?";

	private static final String	SQL_FIND_USER_RANK = "SELECT numFood, rank FROM User_preferences WHERE numUser = ?";
	private static final String SQL_FIND_FOOD_RANK = "SELECT rank FROM User_preferences WHERE numFood = ?";
	
	UserPrefDAOImpl( DAOFactory daoFactory) {
		this.daoFactory = daoFactory;
	}
	

	@Override
	public void create(Long id_user, Long id_food, float rank) throws IllegalArgumentException, DAOException {
		Connection connection = null;
		PreparedStatement preparedStatement = null;
		
		try {
			connection = daoFactory.getConnection();
			preparedStatement = initializationPreparedRequest( connection, SQL_INSERT, false, id_user, id_food, rank );
			int statut = preparedStatement.executeUpdate();
			if (statut == 0) {
				throw new DAOException ("Failed to create a user preference, no new line added to the DB");
			}
		} catch (SQLException e) {
			throw new DAOException(e);
		} finally {
			silentClosures(preparedStatement, connection );
		}
	}

	@Override
	public void create(User user, Food food, float rank) throws IllegalArgumentException, DAOException {
		create(user.getId(), food.getId(), rank);
	}

	@Override
	public List<Food> findFoodsForUser(User user){
		List<Food> foods = new ArrayList<Food>();
		for(float rank = 0.0f; rank<5.0f ; rank+= 0.5f){
			foods.addAll(findFoodsForUserAndRank(user,rank));
		}
		return foods;
	}
	
	@Override
	public List<User> findUsersAppreciating(Food food) throws DAOException {
		List<User> usersAppreciatingFood = new ArrayList<User>();
		Connection connection = null;
		PreparedStatement preparedStatement = null;
		ResultSet resultSet = null;
		UserDAO userDAO = null;
		
		try {
			connection = daoFactory.getConnection();
			preparedStatement = initializationPreparedRequest( connection, SQL_FIND_USERS_APPRECIATING_FOOD, false, food.getId() );
			resultSet = preparedStatement.executeQuery();
			userDAO = this.daoFactory.getUserDAO();
			while (resultSet.next()) {
				Long idUser = (long) resultSet.getInt("numUser");
				User user = userDAO.findById(idUser);
				usersAppreciatingFood.add(user);
			}
		} catch (SQLException e) {
			throw new DAOException (e);
		} finally {
			silentClosures( resultSet, preparedStatement, connection );
		}
		return usersAppreciatingFood;
	}
	

	@Override
	public void delete(User user, Food food, float rank) throws IllegalArgumentException, DAOException {
	Connection connection = null;
    PreparedStatement preparedStatement = null;

    try {
        connection = daoFactory.getConnection();
        preparedStatement = initializationPreparedRequest( connection, SQL_DELETE, false, user.getId(), food.getId(), rank);
        int statut = preparedStatement.executeUpdate();
        if ( statut == 0 ) {
            throw new DAOException( "Failed to delete the user preference, no modifications to the table." );
        }
    } catch ( SQLException e ) {
        throw new DAOException( e );
    } finally {
        silentClosures( preparedStatement, connection );
    }
    }
	
	@Override
	public List<Food> findFoodsForUserAndRank( User user, float rank ) throws DAOException {
		List<Food> rankedFood = new ArrayList<Food>();
		Connection connection = null;
		PreparedStatement preparedStatement = null;
		ResultSet resultSet = null;
		FoodDAO foodDAO = null;
		
		try {
			connection = daoFactory.getConnection();
			preparedStatement = initializationPreparedRequest( connection, SQL_FIND_USER_DE_APPRECIATED_FOOD, false, user.getId(), rank );
			resultSet = preparedStatement.executeQuery();
			foodDAO = this.daoFactory.getFoodDAO();
			while (resultSet.next()) {
				Long idFood = (long) resultSet.getInt("numFood");
				Food food = foodDAO.findById( idFood );
				rankedFood.add(food);
			}
		} catch (SQLException e) {
			throw new DAOException (e);
		} finally {
			silentClosures( resultSet, preparedStatement, connection );
		}
		return rankedFood;
	}

	@Override
	public List<Float> findRankForFood(Food food) throws DAOException {
		Connection connection = null;
		PreparedStatement preparedStatement = null;
		ResultSet resultSet = null;
		ArrayList<Float> rankList = new ArrayList<Float>();
		try {
			connection = daoFactory.getConnection();
			preparedStatement = initializationPreparedRequest( connection, SQL_FIND_FOOD_RANK, false, food.getId() );
			resultSet = preparedStatement.executeQuery();
			while (resultSet.next()) {
				float rank = (float) resultSet.getFloat("rank");
				rankList.add(rank);
			}
		} catch (SQLException e) {
			throw new DAOException (e);
		} finally {
			silentClosures( resultSet, preparedStatement, connection );
		}
		return rankList ;
	}

	@Override
	public HashMap findFoodsAndRankForUser(User user) throws DAOException {
		Connection connection = null;
		PreparedStatement preparedStatement = null;
		ResultSet resultSet = null;
		FoodDAO foodDAO = null;
		HashMap m = new HashMap();
		try {
			connection = daoFactory.getConnection();
			preparedStatement = initializationPreparedRequest( connection, SQL_FIND_USER_RANK, false, user.getId() );
			resultSet = preparedStatement.executeQuery();
			foodDAO = this.daoFactory.getFoodDAO();
			while (resultSet.next()) {
				Long idFood = (long) resultSet.getInt("numFood");
				Food food = foodDAO.findById(idFood);
				float rank = (float) resultSet.getFloat("rank"); 
				m.put(food,rank);
			}
		} catch (SQLException e) {
			throw new DAOException (e);
		} finally {
			silentClosures( resultSet, preparedStatement, connection );
		}
		return m ;
	}
}