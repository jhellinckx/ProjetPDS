package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import static dao.DAOUtilitaire.*;


import org.calorycounter.shared.models.Food;
import org.calorycounter.shared.models.User;
import org.calorycounter.shared.models.Sport;

public class UserHistoryDAOImpl implements UserHistoryDAO {
	private DAOFactory daoFactory;
	private static final String SQL_INSERT = "INSERT INTO Users_history (idUser,idFood,date,checked,is_food_or_sport) VALUES (?, ?, ?, ?, 'Food')";
	private static final String SQL_UPDATE_CHECKED = "UPDATE Users_history SET checked = ? WHERE idUser = ? AND idFood = ? AND date = ?";
	private static final String SQL_FIND_HISTORY_FOODS = "SELECT idFood, checked FROM Users_history WHERE idUser = ? AND is_food_or_sport = 'Food'";
	private static final String SQL_FIND_HISTORY_DATES = "SELECT date FROM Users_history WHERE idUser = ? AND is_food_or_sport = 'Food'";
	private static final String SQL_FIND_HISTORY_DATE_FOR_FOOD = "SELECT date FROM Users_history WHERE idUser = ? AND idFood = ? and is_food_or_sport = 'Food'";
	private static final String SQL_FIND_HISTORY_FOODS_FOR_DATE = "SELECT idFood, checked FROM Users_history WHERE idUser = ? AND date = ? AND is_food_or_sport = 'Food'";
	private static final String SQL_DELETE = "DELETE FROM Users_history WHERE idUser = ? AND idFood = ? AND date = ?";
	private static final String SQL_INSERT_SPORT = "INSERT INTO Users_history (idUser,sport_name,date,duration, energy_consumed, checked,is_food_or_sport) VALUES (?, ?, ?, ?, ?, 1, 'Sport')";
	private static final String SQL_FIND_HISTORY_SPORTS_FOR_DATE = "SELECT sport_name, duration, energy_consumed FROM Users_history WHERE idUser = ? AND date = ? AND is_food_or_sport = 'Sport'";
	private static final String SQL_DELETE_SPORT = "DELETE FROM Users_history WHERE idUser = ? AND sport_name = ? AND duration = ? AND date = ?";

	UserHistoryDAOImpl( DAOFactory daoFactory ) {
		this.daoFactory = daoFactory;
	}

	@Override
	public void addToHistory(Long idUser, Long idFood, String date, int isEaten) throws DAOException {
		Connection connection = null;
		PreparedStatement preparedStatement = null;
		
		try {
			connection = daoFactory.getConnection();
			preparedStatement = initializationPreparedRequest( connection, SQL_INSERT, false, idUser, idFood, date, isEaten );
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
		int myInt = (food.isEaten()) ? 1 : 0;
		addToHistory(user.getId(), food.getId(), date, myInt);
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
				int checked = (int) resultSet.getInt("checked");
				if(checked==1){
					food.isEaten();
				}else {
					food.notEaten();
				}
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
	public List<Food> getHistoryFoodForDate(User user, String date){
		List<Food> foods = new ArrayList<>();
		Connection connection = null;
		PreparedStatement preparedStatement = null;
		ResultSet resultSet = null;
		FoodDAO foodDAO = null;

		try{
			connection = daoFactory.getConnection();
			preparedStatement = initializationPreparedRequest(connection, SQL_FIND_HISTORY_FOODS_FOR_DATE, false, user.getId(), date);
			resultSet = preparedStatement.executeQuery();
			foodDAO = this.daoFactory.getFoodDAO();
			while (resultSet.next()){
				Long idFood = (long) resultSet.getInt("idFood");
				Food food = foodDAO.findById(idFood);
				int checked = (int) resultSet.getInt("checked");
				if(checked==1){
					food.eaten();
				}else{
					food.notEaten();
				}
				foods.add(food);
			}
		} catch (SQLException e) {
			throw new DAOException (e);
		} finally{
			silentClosures( resultSet, preparedStatement, connection );
		}
		return foods;
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

	@Override
	public void changeEatenStatus(User user, Food food, String date, int status) throws DAOException {
		Connection connection = null;
		PreparedStatement preparedStatement = null;
		
		try {
			connection = daoFactory.getConnection();
			preparedStatement = initializationPreparedRequest( connection, SQL_UPDATE_CHECKED, false, status, user.getId(), food.getId(), date );
			int statut = preparedStatement.executeUpdate();
			if (statut == 0) {
				throw new DAOException ("Failed to change checked status");
			}
		} catch (SQLException e) {
			throw new DAOException(e);
		} finally {
			silentClosures(preparedStatement, connection );
		}
	}

	@Override
	public void deleteFoodFromHistory(User user, Food food, String date) throws DAOException {
		Connection connexion = null;
        PreparedStatement preparedStatement = null;

        try {
            /* Recuperation d'une connexion depuis la Factory */
        	connexion = daoFactory.getConnection();
            preparedStatement = initializationPreparedRequest( connexion, SQL_DELETE, false, user.getId(), food.getId(), date);

            int statut = preparedStatement.executeUpdate();
            /* Analyse du statut retourne par la requete d'insertion */
            if ( statut == 0 ) {
                throw new DAOException( "Failed to delete the food history, no modifications to the table." );  
            }
        } catch ( SQLException e ) {
            throw new DAOException( e );
        } finally {
            silentClosures( preparedStatement, connexion );
        }
	}

	@Override
	public void addSportToHistory(User user, Sport sport, String date) throws DAOException{
		Connection connection = null;
		PreparedStatement preparedStatement = null;
		
		try {
			connection = daoFactory.getConnection();
			preparedStatement = initializationPreparedRequest( connection, SQL_INSERT_SPORT, false, user.getId(), sport.getName(), date, sport.getDuration(), sport.getEnergyConsumed());
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
	public List<Sport> getHistorySportForDate(User user, String date) throws DAOException{
		List<Sport> sports = new ArrayList<>();
		Connection connection = null;
		PreparedStatement preparedStatement = null;
		ResultSet resultSet = null;
		try{
			connection = daoFactory.getConnection();
			preparedStatement = initializationPreparedRequest(connection, SQL_FIND_HISTORY_SPORTS_FOR_DATE, false, user.getId(), date);
			resultSet = preparedStatement.executeQuery();
			while (resultSet.next()){
				String sportName = (String) resultSet.getString("sport_name");
				int duration = (int) resultSet.getInt("duration");
				Float energyConsumed = (Float) resultSet.getFloat("energy_consumed");
				Sport sport = new Sport(sportName, duration, energyConsumed);
				sports.add(sport);
			}
		} catch (SQLException e) {
			throw new DAOException (e);
		} finally{
			silentClosures( resultSet, preparedStatement, connection );
		}
		return sports;
	}

	@Override
	public void deleteSportFromHistory(User user, Sport sport, String date) throws DAOException {
		Connection connexion = null;
        PreparedStatement preparedStatement = null;

        try {
            /* Recuperation d'une connexion depuis la Factory */
        	connexion = daoFactory.getConnection();
            preparedStatement = initializationPreparedRequest( connexion, SQL_DELETE_SPORT, false, user.getId(), sport.getName(), sport.getDuration(), date);

            int statut = preparedStatement.executeUpdate();
            /* Analyse du statut retourne par la requete d'insertion */
            if ( statut == 0 ) {
                throw new DAOException( "Failed to delete the food history, no modifications to the table." );  
            }
        } catch ( SQLException e ) {
            throw new DAOException( e );
        } finally {
            silentClosures( preparedStatement, connexion );
        }
	}
}