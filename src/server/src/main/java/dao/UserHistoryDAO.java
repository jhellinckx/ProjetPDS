package dao;

import java.util.List;
import items.User;
import items.Food;


public interface UserHistoryDAO {

	void addToHistory( User user, Food food, String date ) throws DAOException;

	void addToHistory( Long idUser, Long idFood, String date ) throws DAOException;

	List<Food> getHistoryFoods(User user) throws DAOException;

	List<String> getHistoryFoodNames(User user) throws DAOException;

	List<String> getHistoryDates(User user) throws DAOException;

}