package dao;

import java.util.List;
import org.calorycounter.shared.models.User;
import org.calorycounter.shared.models.Food;


public interface UserHistoryDAO {

	void addToHistory( User user, Food food, String date ) throws DAOException;

	void addToHistory( Long idUser, Long idFood, String date, int isEaten ) throws DAOException;

	List<Food> getHistoryFoods(User user) throws DAOException;

	List<String> getHistoryFoodNames(User user) throws DAOException;

	List<String> getHistoryDates(User user) throws DAOException;

	List<Food> getHistoryFoodForDate(User user, String date) throws DAOException;

	String getHistoryDate(User user, Food food) throws DAOException ;

	void changeEatenStatus(User user, Food food, String date, int status) throws DAOException; 

}