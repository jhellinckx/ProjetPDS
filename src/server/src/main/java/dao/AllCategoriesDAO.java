package dao;

import java.util.List;

public interface AllCategoriesDAO {
	List<String> getAllFoodCategories() throws DAOException;

	List<String> getAllRecipeCategories() throws DAOException;
}