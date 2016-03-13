package dao;

import java.util.List;
import org.calorycounter.shared.models.User;
import org.calorycounter.shared.models.Food;
import org.calorycounter.shared.models.Sport;
import org.calorycounter.shared.models.Recipe;


public interface UserHistoryDAO {

	void addToHistory( User user, Food food, String date ) throws DAOException;

	void addToHistory( Long idUser, Long idFood, String date, int isEaten ) throws DAOException;

	List<Food> getHistoryFoods(User user) throws DAOException;

	List<String> getHistoryFoodNames(User user) throws DAOException;

	List<String> getHistoryDates(User user) throws DAOException;

	List<Food> getHistoryFoodForDate(User user, String date) throws DAOException;

	String getHistoryDate(User user, Food food) throws DAOException ;

	void changeEatenStatus(User user, Food food, String date, int status) throws DAOException; 

	void deleteFoodFromHistory(User user, Food food, String date) throws DAOException;

	void addSportToHistory(User user, Sport sport, String date) throws DAOException;

	List<Sport> getHistorySportForDate(User user, String date) throws DAOException;

	void deleteSportFromHistory(User user, Sport sport, String date) throws DAOException;

	void addRecipeToHistory( User user, Recipe recipe , String date) throws DAOException;

	void addRecipeToHistory( User user, Recipe recipe, String date, int isEaten) throws DAOException;

	void deleteRecipeFromHistory(User user, Recipe recipe, String date) throws DAOException;

	void changeRecipeEatenStatus(User user, Recipe recipe, String date, int status) throws DAOException;

	List<Recipe> getHistoryRecipeForDate(User user, String date) throws DAOException;

	List<Recipe> getHistoryRecipes(User user) throws DAOException;
}