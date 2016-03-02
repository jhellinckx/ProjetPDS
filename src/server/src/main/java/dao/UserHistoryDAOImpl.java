package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import static dao.DAOUtilitaire.*;


import items.Food;
import items.User;

public class UserHistoryDAOImpl implements UserHistoryDAO {
	private DAOFactory daoFactory;
	private static final String SQL_INSERT = "INSERT INTO Users_history (idUser,idFood,date) VALUES (?, ?, ?)";
	private static final String SQL_FIND_HISTORY_FOODS = "SELECT idFood FROM Users_history WHERE idUser = ?";
	private static final String SQL_FIND_HISTORY_DATES = "SELECT date FROM Users_history WHERE idUser = ?";
	private static final String SQL_FIND_HISTORY_DATE_FOR_FOOD = "SELECT date FROM Users_history WHERE idUser = ? AND idFood = ?";


	UserHistoryDAOImpl( DAOFactory daoFactory ) {
		this.daoFactory = daoFactory;
	}

	@Override
	public void addToHistory(Long idUser, Long idFood, String date) throws DAOException {
		Connection connection = null;
		PreparedStatement preparedStatement = null;
		
		try {
			connection = daoFactory.getConnection();
			preparedStatement = initializationPreparedRequest( connection, SQL_INSERT, false, idUser, idFood, date );
			int statut = preparedStatement.executeUpdate();
			if (statut == 0) {
				throw new DAOException ("Failed to create a user food history, no new line added to the DB");
			}
		} catch (SQLException e) {
			throw new DAOException(e);
		} finally {
			silentClosures(preparedStatement, connection );
		}
	}

	@Override
	public void addToHistory(User user, Food food, String date) throws DAOException {
		addToHistory(user.getId(), food.getId(), date);
	}

	@Override
	public List<Food> getHistoryFoods(User user) throws DAOException {
		List<Food> historyFoods = new ArrayList<Food>();
		Connection connection = null;
		PreparedStatement preparedStatement = null;
		ResultSet resultSet = null;
		FoodDAO foodDAO = null;
		
		try {
			connection = daoFactory.getConnection();
			preparedStatement = initializationPreparedRequest( connection, SQL_FIND_HISTORY_FOODS, false, user.getId() );
			resultSet = preparedStatement.executeQuery();
			foodDAO = this.daoFactory.getFoodDAO();
			while (resultSet.next()) {
				Long idFood = (long) resultSet.getInt("idFood");
				Food food = foodDAO.findById(idFood);
				historyFoods.add(food);
			}
		} catch (SQLException e) {
			throw new DAOException (e);
		} finally {
			silentClosures( resultSet, preparedStatement, connection );
		}
		return historyFoods;
	}

	@Override
	public List<String> getHistoryFoodNames(User user) throws DAOException {
		List<String> historyFoodNames = new ArrayList<String>();
		List<Food> historyFoods = getHistoryFoods(user);
		for(Food food : historyFoods){
			historyFoodNames.add(food.getProductName());
		}
		return historyFoodNames;
	}

	@Override
	public List<String> getHistoryDates(User user) throws DAOException {
		List<String> historyDates = new ArrayList<String>();
		Connection connection = null;
		PreparedStatement preparedStatement = null;
		ResultSet resultSet = null;
		
		try {
			connection = daoFactory.getConnection();
			preparedStatement = initializationPreparedRequest( connection, SQL_FIND_HISTORY_DATES, false, user.getId() );
			resultSet = preparedStatement.executeQuery();
			while (resultSet.next()) {
				String date = resultSet.getString("date");
				historyDates.add(date);	
			}
		} catch (SQLException e) {
			throw new DAOException (e);
		} finally {
			silentClosures( resultSet, preparedStatement, connection );
		}
		return historyDates;
	}

	@Override
	public String getHistoryDate(User user, Food food) throws DAOException {
		String historyDate = new String();
		Connection connection = null;
		PreparedStatement preparedStatement = null;
		ResultSet resultSet = null;
		
		try {
			connection = daoFactory.getConnection();
			preparedStatement = initializationPreparedRequest( connection, SQL_FIND_HISTORY_DATE_FOR_FOOD, false, user.getId(), food.getId() );
			resultSet = preparedStatement.executeQuery();
			while (resultSet.next()) {
				historyDate = resultSet.getString("date");
			}
		} catch (SQLException e) {
			throw new DAOException (e);
		} finally {
			silentClosures( resultSet, preparedStatement, connection );
		}
		return historyDate;
	}
}